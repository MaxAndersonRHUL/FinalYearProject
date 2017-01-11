package RL;

import java.util.*;

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

    private double sumOfArray(Set<Double> set) {
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
            //System.out.println("############################### THE AGENT CAN TAKE NO ACTIONS! #######################################");
            return null;
        }

        double max = 0;
        HashMap<Double, Action> kPowerActionValues = new HashMap<>();
        for (Action act : activeActions) {
            if (act.getValue() > max) {
                max = act.getValue();
            }
            kPowerActionValues.put(Math.pow(explorationValue, (act.getValue())), act);
        }

        if (max == 0) {
            //System.out.println("MOVING THE AGENT RANDOMLY");
            return model.moveAgentRandom();
        }

        double sumValues = sumOfArray(kPowerActionValues.keySet());

        HashMap<Double, Action> temp = new HashMap<>();

        double total = 0;
        for(Iterator<Map.Entry<Double, Action>> it = kPowerActionValues.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Double, Action> entry = it.next();
            Double value = (entry.getKey() / sumValues);
            temp.put(value, entry.getValue());

            it.remove();
            total = total + value;
        }

        for (Map.Entry<Double, Action> entry : temp.entrySet()) {
            kPowerActionValues.put(entry.getKey(), entry.getValue());
        }

        Random random = new Random();
        int ran = random.nextInt((int) (total * 1000.0));



        double cumulative = 0;
        //System.out.println("############ Probabilities for state: " + cs + " ##################");
        for (Map.Entry<Double, Action> entry : kPowerActionValues.entrySet()) {
            //System.out.println("Action with value: " + entry.getValue().getValue() +", " + entry.getKey());
            cumulative = cumulative + (entry.getKey()*1000.0);
            if(ran < cumulative) {
                //System.out.println("GETTING THE AGENT TO DO ACTION THAT RESULTS IN STATE: \n" + entry.getValue().resultingState);
                return entry.getValue();
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
