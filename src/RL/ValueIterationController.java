package RL;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by max on 31/01/2017.
 *
 * This controller provides different functionality than the other controllers, such as the Q-Learning controller.
 * Instead of running under the parameters of a normal controller and effecting the model, it creates it's own instance
 * of the model and solves the MDP as fast as it can. It can therefore be run seperately from a normal controller, and
 * alongside a running simulation.
 *
 */
public class ValueIterationController {

    public HashMap<StateIdentity, State> states;
    public HashMap<StateIdentity, Double> stateValues;
    public HashMap<StateIdentity, List<Action>> actionsChosen;
    public HashMap<StateIdentity, Double> changesThisIteration;

    private int currentIterations = 0;

    // How many decimal places the controller considers when deciding if a value has changed or not.
    // A high number of decimal places result in many iterations that cause no change in the policy towards
    // the end of the calculation.
    private int iterationFinishedPrecisionPlaces = 5;

    private static ValueIterationController instance;

    double learningValue = 0.9f;

    volatile private boolean complete = false;

    public boolean isComplete() {
        return complete;
    }

    private ValueIterationController() {
        instance = this;
    }

    // Function for effective rounding of doubles from Stackoverflow user 'Jonik'
    // http://stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
    public double round(double value) {
        int places = iterationFinishedPrecisionPlaces;
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static ValueIterationController getInstance() {
        if(instance == null) {
            instance = new ValueIterationController();
        }
        return instance;
    }

    private boolean stochasticIterateValues() {
        boolean valueChanged = false;
        for(Map.Entry<StateIdentity, State> keyVal : states.entrySet()) {
            if(keyVal.getValue().actions.size() == 0) {
                continue;
            }

            double stateValue = calculateStochasticValueOfState(keyVal.getValue());

            if(round(stateValue) != round(stateValues.get(keyVal.getKey()))) {
                changesThisIteration.put(keyVal.getKey(), stateValue);
                valueChanged = true;
            }
        }
        return valueChanged;
    }

    private double calculateStochasticValueOfState(State state) {
        double currentMax = -1;

        /*
        if(state.getReward() > 0) {
            return state.getReward();
        }
        */

        for(Action action : state.getActions()) {
            double totalEstValueForAllPossibleActions = 0.0;
            for(Map.Entry<State, Probability> possibleResult : action.getResultingStates().entrySet()) {
                double calc = (learningValue * stateValues.get(possibleResult.getKey().getStateIdentity())) + state.getReward();
                totalEstValueForAllPossibleActions += possibleResult.getValue().probab * (calc);
            }

            if(totalEstValueForAllPossibleActions > currentMax) {
                currentMax = totalEstValueForAllPossibleActions;
            }
        }
        return currentMax;
    }

    /*
    private List<Action> stochasticMaxActionsOfState(State state) {
        double currentMax = -1;
        List<Action> highestActions = new ArrayList<Action>();
        for(Action action : state.getActions()) {
            double totalEstValueForAllPossibleActions = 0.0;
            for(Map.Entry<State, Probability> possibleResult : action.getResultingStates().entrySet()) {
                double calc = learningValue * stateValues.get(possibleResult.getKey().getStateIdentity());
                totalEstValueForAllPossibleActions += possibleResult.getValue().probab * (possibleResult.getKey().getReward() * calc);
            }
            if(totalEstValueForAllPossibleActions > currentMax) {
                highestActions.clear();
                highestActions.add(action);
                currentMax = totalEstValueForAllPossibleActions;
            } else if(totalEstValueForAllPossibleActions == currentMax) {
                highestActions.add(action);
            }
        }
        return highestActions;
    }

    private List<Action> stochasticGetLargestActioByValueFromState(State state) {
        List<Action> highestActions = new ArrayList<Action>();
        double highestVal = -1;
        for(Action action : state.getActions()) {
            StateIdentity ident = action.getMostProbableState().getStateIdentity();

            double resultStateValue;
            if(!stateValues.containsKey(ident)) {
                continue;
            } else {
                resultStateValue = stateValues.get(ident);
            }

            if(resultStateValue > highestVal) {
                highestActions.clear();
                highestActions.add(action);
                highestVal = resultStateValue;
            } else if(resultStateValue == highestVal) {
                highestActions.add(action);
            }
        }
        return highestActions;
    }
    */

    private boolean deterministicIterateValues() {
        boolean valueChanged = false;
        for(Map.Entry<StateIdentity, State> keyVal : states.entrySet()) {


            double newVal = 0;

            if(keyVal.getValue().actions.size() == 0) {
                newVal = keyVal.getValue().getReward();
            } else {
                double highestVal = getLargestValueFromState(keyVal.getValue());
                newVal = (highestVal * learningValue) + keyVal.getValue().getReward();
            }

            if(round(newVal) != round(stateValues.get(keyVal.getKey()))) {
                changesThisIteration.put(keyVal.getKey(), newVal);
                valueChanged = true;
            }
        }
        return valueChanged;
    }

    private void integrateIterationChanges() {
        for(Map.Entry<StateIdentity, Double> changesEntry : changesThisIteration.entrySet()) {
            stateValues.replace(changesEntry.getKey(), changesEntry.getValue());
        }
    }

    private double getLargestValueFromState(State state) {
        List<Action> largestValues = getLargestActionByValueFromState(state);
        if(largestValues.size() == 0) {
            return state.getReward();
        }
        return stateValues.get(largestValues.get(0).getMostProbableState().getStateIdentity());
    }

    private List<Action> getLargestActionByValueFromState(State state) {
        List<Action> highestActions = new ArrayList<Action>();
        double highestVal = -1;
        for(Action action : state.getActions()) {
            StateIdentity ident = action.getMostProbableState().getStateIdentity();

            double resultStateValue;
            if(!stateValues.containsKey(ident)) {
                continue;
            } else {
                resultStateValue = stateValues.get(ident);
            }

            if(resultStateValue > highestVal) {
                highestActions.clear();
                highestActions.add(action);
                highestVal = resultStateValue;
            } else if(resultStateValue == highestVal) {
                highestActions.add(action);
            }
        }
        return highestActions;
    }

    private void initStateValues() {
        Random rand = new Random();
        stateValues = new HashMap<>();
        actionsChosen = new HashMap<>();
        changesThisIteration = new HashMap<>();
        for(Map.Entry<StateIdentity, State> keyVal : states.entrySet()) {
            stateValues.put(keyVal.getKey(), keyVal.getValue().reward);
        }
    }

    public int getAmountOfIterations (){
        return currentIterations;
    }

    // The hashmap is a shallow copy, and as such, the data in the states is not modified. Instead,
    // the statesValues hashmap is created to store the values of each state, since that is the only
    // information that is needed to be introduced.
    private void copyStatesFromPrimaryModel() {
        states = new HashMap<>(CurrentSimulationReference.model.getStates());
    }

    private void finishedCalculatingDeterminstic() {
        for(State state : states.values()) {
            List<Action> nState = getLargestActionByValueFromState(state);
            actionsChosen.put(state.getStateIdentity(), nState);
        }
        complete = true;
    }

    private void finishedCalculatingStochastic() {
        for(State state : states.values()) {
            List<Action> nState = getLargestActionByValueFromState(state);
            actionsChosen.put(state.getStateIdentity(), nState);
        }
        complete = true;
    }

    public void beginCalculatingValueIteration() {

        copyStatesFromPrimaryModel();
        initStateValues();

        new Thread() {
            public void run() {
                while (!isComplete()) {
                    if(CurrentSimulationReference.controller == null) {
                        continue;
                    }
                    if(CurrentSimulationReference.controller.deterministicEnvironment) {
                        if(deterministicIterateValues() == false) {
                            finishedCalculatingDeterminstic();
                        }
                    } else {
                        if(stochasticIterateValues() == false) {
                            finishedCalculatingStochastic();
                        }
                    }

                    integrateIterationChanges();


                    currentIterations++;


                    /*
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {

                    }
                    */
                }
            }
        }.start();
    }

}
