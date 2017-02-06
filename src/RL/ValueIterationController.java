package RL;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    private static ValueIterationController instance;

    double learningValue = 0.9f;

    private ValueIterationController() {
        instance = this;
    }

    public static ValueIterationController getInstance() {
        if(instance == null) {
            instance = new ValueIterationController();
        }
        return instance;
    }

    private void iterateValues() {
        for(Map.Entry<StateIdentity, State> keyVal : states.entrySet()) {
            double highestVal = getLargestValueFromState(keyVal.getValue());
            stateValues.replace(keyVal.getKey(), (highestVal * learningValue) + keyVal.getValue().getReward());
        }
    }

    private double getLargestValueFromState(State state) {
        double max = 0;
        for(Action action : state.getActions()) {
            double resultStateValue = stateValues.get(action.getMostProbableState().getStateIdentity());
            if(resultStateValue > max) {
                max = resultStateValue;
            }
        }
        return max;
    }

    private void initStateValues() {
        Random rand = new Random();
        stateValues = new HashMap<>();
        for(Map.Entry<StateIdentity, State> keyVal : states.entrySet()) {
            stateValues.put(keyVal.getKey(), rand.nextDouble());
        }
    }

    // The hashmap is a shallow copy, and as such, the data in the states is not modified. Instead,
    // the statesValues hashmap is created to store the values of each state, since that is the only
    // information that is needed to be introduced.
    private void copyStatesFromPrimaryModel() {
        states = new HashMap<>(CurrentSimulationReference.model.getStates());
    }

    private boolean hasModelConverged() {
        return false;
    }

    private void finishedCalculating() {

    }

    public void beginCalculatingValueIteration() {

        copyStatesFromPrimaryModel();
        initStateValues();

        new Thread() {
            public void run() {
                while (true) {

                    iterateValues();
                    if(hasModelConverged()) {
                        finishedCalculating();
                        break;
                    }

                }
            }
        }.start();
    }

}
