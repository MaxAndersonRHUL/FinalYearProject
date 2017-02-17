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

    private int currentIterations = 0;

    // How many decimal places the controller considers when deciding if a value has changed or not.
    // A high number of decimal places result in many iterations that cause no change in the policy towards
    // the end of the calculation.
    private int iterationFinishedPrecisionPlaces = 3;

    private static ValueIterationController instance;

    double learningValue = 0.9f;

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

    private boolean iterateValues() {
        boolean valueChanged = false;
        for(Map.Entry<StateIdentity, State> keyVal : states.entrySet()) {
            if(keyVal.getValue().actions.size() == 0) {
                continue;
            }
            double highestVal = getLargestValueFromState(keyVal.getValue());
            double newVal = (highestVal * learningValue) + keyVal.getValue().getReward();
            if(round(newVal) != round(stateValues.get(keyVal.getKey()))) {
                stateValues.replace(keyVal.getKey(), newVal);
                valueChanged = true;
            }
        }
        return valueChanged;
    }

    private double getLargestValueFromState(State state) {
        return stateValues.get(getLargestActionByValueFromState(state).get(0).getMostProbableState().getStateIdentity());
    }

    private List<Action> getLargestActionByValueFromState(State state) {
        List<Action> highestActions = new ArrayList<Action>();
        double highestVal = -1;
        for(Action action : state.getActions()) {
            double resultStateValue = stateValues.get(action.getMostProbableState().getStateIdentity());
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
        for(Map.Entry<StateIdentity, State> keyVal : states.entrySet()) {
            if(keyVal.getValue().actions.size() == 0) {
                continue;
            }
            stateValues.put(keyVal.getKey(), rand.nextDouble());
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

    private void finishedCalculating() {
        for(State state : states.values()) {
            List<Action> nState = getLargestActionByValueFromState(state);
            actionsChosen.put(state.getStateIdentity(), nState);
        }
    }

    public void beginCalculatingValueIteration() {

        copyStatesFromPrimaryModel();
        initStateValues();

        new Thread() {
            public void run() {
                while (true) {
                    if(iterateValues() == false){
                        finishedCalculating();
                        break;
                    }
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
