package RL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by max on 04/12/2016.
 */
public class Model {

    protected HashMap<StateIdentity, State> states;

    protected int gridSizeX = 10;
    protected int gridSizeY = 5;

    protected Agent mainAgent;

    private static Model instance;

    protected State startingState;

    public Model() {
        instance = this;
    }

    public void addState(State state, StateIdentity identity) {
        states.put(identity, state);
    }

    public void addState(State state) {

        addState(state, state.getStateIdentity());
    }

    public void stateChanged() {

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
        for (Action act : srcState.getActions()) {
            if (act.resultingState.equals(dstState)) {
                return act;
            }
        }
        return null;
    }

    public void moveAgentRandom() {
        ArrayList<Action> actions = getAgent().currentState.getActions();
        if (actions.size() < 1) {
            return;
        }
        Random rng = new Random();
        int num = rng.nextInt(actions.size());
        Action exec = actions.get(num);
        getAgent().doAction(exec);
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

