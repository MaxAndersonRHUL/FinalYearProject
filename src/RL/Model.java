package RL;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by max on 04/12/2016.
 */
public class Model implements Serializable{

    protected HashMap<StateIdentity, State> states;

    protected int gridSizeX = 10;
    protected int gridSizeY = 5;

    protected Agent mainAgent;

    private static Model instance;

    protected State startingState;

    int convergenceCheckPrecision = 2;

    transient ExperimentableValue currentConvergencePercent = new ExperimentableValue(0.0, "Policy Accuracy (%)");
    transient ExperimentableValue averageRewardPer100Actions = new ExperimentableValue(0.0, "Average Reward per 100 actions");

    public Model() {
        instance = this;
    }

    // Function for effective rounding of doubles from Stackoverflow user 'Jonik'
    // http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void addState(State state, StateIdentity identity) {
        states.put(identity, state);
    }

    public void addState(State state) {

        addState(state, state.getStateIdentity());
    }

    // Called by the controller at the end of every action choice and execution.
    public void stateChanged() {
        if(mainAgent.currentState.getReward() != 0) {

        }
    }

    public void setStartingState(State state) {
        startingState = state;
    }

    // By default, a model simply returns the first state in an action. This will typically be overridden by child classes.
    public State decideActionChoiceResult(Action action) {
        for(Map.Entry<State,Probability> entry : action.getResultingStates().entrySet()) {
            return entry.getKey();
        }
        return null;
    }

    public void removeState(State state) {
        states.remove(state);
    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public boolean canStateTransitionTo(State srcState, State dstState) {
        return getStateTransitionTo(srcState, dstState) != null;
    }

    public Action getStateTransitionTo(State srcState, State dstState) {

       // System.out.println("#################################");
       // System.out.println("WE ARE TRYING TO FIND A TRANSITION FROM: \n" + srcState);
       // System.out.println("TO STATE: \n" + dstState);

        synchronized (srcState.getActions()) {
            for (Action act : srcState.getActions()) {
                if (act.getResultingStates().containsKey(dstState)) {
                    // System.out.println("WE FOUND AN ACTION!");
                    // System.out.println("#################################");
                    return act;

                }
            }
        }
        // System.out.println("Found nothing! But we did search: " + i + " actions in the source state");
        return null;
    }

    public Action moveAgentRandom() {
        List<Action> actions = getAgent().currentState.getActiveActions();

        if (actions.size() < 1) {
            System.out.println("Agent has no actions to pick randomly!");
            return null;
        }
        Random rng = new Random();
        int num = rng.nextInt(actions.size());
        Action exec = actions.get(num);
        return exec;
    }

    // Used to find how close the current model has converged on the true policy. Finding the hashmap from state identities
    // to actions that form the optimal policy can be found via the ValueIterationController.

    public double calculateCurrentConvergancePercent(HashMap<StateIdentity, List<Action>> actionsChosen) {
        int numberOfCorrectActions = 0;
        int totalActions = 0;
        for(State state : states.values()) {
            totalActions++;

            List<Action> actionChosen = actionsChosen.get(state.getStateIdentity());

            if(actionChosen == null) {
                continue;
            }

            List<Action> highestActions = state.getHighestActionValuesToPrecision(convergenceCheckPrecision);

            if(highestActions.size() != actionChosen.size()) {
                continue;
            }

            boolean complete = true;
            for(Action nsAction : actionChosen) {
                boolean contained = false;
                for(Action nbAction : highestActions) {
                    if(nsAction == nbAction) {
                        contained = true;
                        break;
                    }
                }
                if(!contained) {
                    complete = false;
                    break;
                }
            }
            if(complete) {
                numberOfCorrectActions++;
            }
        }

        double percent = (double) numberOfCorrectActions / (double)totalActions;

        currentConvergencePercent.setValue(percent);

        return (percent * 100.0);
    }

    // Returns null if no actions on state, or if all actions are 0.
    public Action highestValueFromState(State state) {
        Action highestAct = null;
        double highestVal = 0;
        for (Action act : state.getActions()) {
            if (act.value > highestVal) {
                highestVal = act.value;
                highestAct = act;
            }
        }
        return highestAct;
    }

    public Agent getAgent() {
        return mainAgent;
    }

    public HashMap<StateIdentity, State> getStates() {
        return states;
    }

}

