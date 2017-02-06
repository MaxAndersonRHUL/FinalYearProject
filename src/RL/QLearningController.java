package RL;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by max on 18/10/2016.
 */
public class QLearningController extends Controller{

    Model model;
    static QLearningController instance;

    private double decayValue = 0.85;

    private QLearningController() {
        model = CurrentSimulationReference.model;
    }


    public static QLearningController getInstance() {
        if (instance == null) {
            instance = new QLearningController();
        }
        return instance;
    }

    // Code for fast integer powers from: http://stackoverflow.com/questions/8071363/calculating-powers-in-java
    // By user Qx__
    private long intPow(long a, int b) {
        if (b == 0) {
            return 1;
        }
        if (b == 1) {
            return a;
        }
        if ((b & 1) == 0) {
            return intPow(a * a, b / 2); //even a=(a^2)^b/2
        } else return a * intPow(a * a, b / 2); //odd  a=a*(a^2)^b/2
    }

    private double sumOfArray(Double[] set) {
        double sum = 0;
        for (Double d : set) {
            sum = sum + d;
        }
        return sum;
    }

    protected Action makeActionChoice() {
        State cs = model.getAgent().currentState;
        List<Action> activeActions = cs.getActiveActions();
        if (activeActions.size() <= 0) {
            System.out.println("############################### THE AGENT CAN TAKE NO ACTIONS! #######################################");
            return null;
        }

        double max = 0;
        Double[] kPowerActionValues = new Double[activeActions.size()];
        for (int i = 0; i < activeActions.size(); i++) {
            Action act = activeActions.get(i);
            if (act.getValue() > max) {
                max = act.getValue();
            }
            kPowerActionValues[i] = (Math.pow(explorationValue, (act.getValue())));
        }

        if (max == 0) {
            return model.moveAgentRandom();
        }

        double sumValues = sumOfArray(kPowerActionValues);

        HashMap<Double, Action> temp = new HashMap<>();

        double total = 0;
        for(int i = 0; i < kPowerActionValues.length; i++) {
            Double value = (kPowerActionValues[i]) / sumValues;
            kPowerActionValues[i] = value;
            total = total + value;
        }

        double ran = ThreadLocalRandom.current().nextDouble(0, total);

        double cumulative = 0;

        for (int i = 0; i < kPowerActionValues.length; i++) {
            cumulative = cumulative + kPowerActionValues[i];
            if(ran <= cumulative) {
                return activeActions.get(i);
            }
        }
        return null;
    }

    @Override
    protected void updateAgentLearningValues() {
        Action highestAct = model.highestValueFromState(model.getAgent().currentState);
        double highestVal = 0;
        if (highestAct != null) {
            highestVal = highestAct.value;
        }
        double reward = model.getAgent().currentState.reward;
        Action lastAction = model.getAgent().getLastActionTaken();
        if (lastAction != null) {
            lastAction.value = (decayValue * highestVal) + reward;
        }
    }

    /*
    @Override
    public void stepSimulation() {
        makeActionChoice();
        //model.moveAgentRandom();
        updateAgentLearningValues();
    }
    */
}
