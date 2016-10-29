package sample;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by max on 18/10/2016.
 */
public class GridWorldQLearning {

    GridWorldModel model;
    static GridWorldQLearning instance;

    private long timeSaveTemp;
    private int targetFPS = 8;
    private long evoRate = 1000000000 / targetFPS;
    private double decayValue = 0.85;
    private double explorationValue = 2;

    private ConcurrentLinkedDeque<Integer> forAverageIterationRate = new ConcurrentLinkedDeque<Integer>();

    private GridWorldQLearning() {
        model = GridWorldModel.getInstance();
        runTestThread();
    }

    public void setExploValue(double value) {
        if(value < 0.01) {
            explorationValue = 0.01;
            return;
        }
        explorationValue = value;
    }

    public static GridWorldQLearning getInstance() {
        if (instance == null) {
            instance = new GridWorldQLearning();
        }
        return instance;
    }

    public void setIterationSpeed(int iterationsPerSecond) {
        targetFPS = iterationsPerSecond;
        evoRate = 1000000000 / targetFPS;

        forAverageIterationRate.clear();
    }

    private int calculateAverage(ConcurrentLinkedDeque<Integer> valList) {
        double sum = 0;
        if (!valList.isEmpty()) {
            for (Integer mark : valList) {
                sum += mark;
            }
            // Round up
            return (int) ((sum / valList.size()) + 0.5 ) ;
        }
        return 0;
    }

    private void addValueToRateList(int val) {
        if (forAverageIterationRate.size() > evoRate) {
            forAverageIterationRate.removeFirst();
        }
        forAverageIterationRate.addLast(val);
    }

    public int getAverageIterationRate() {
        return calculateAverage(forAverageIterationRate);
    }

    private void runTestThread() {
        new Thread() {
            public void run() {
                while (true) {
                    long now = System.nanoTime();
                    long updateLength = now - timeSaveTemp;
                    addValueToRateList((int) (1 / ((now - timeSaveTemp) / 1000000000.0)));
                    timeSaveTemp = now;

                    long sleepTime = (timeSaveTemp - System.nanoTime() + evoRate) / 1000000;
                    if (sleepTime > 0) {
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    makeActionChoice();
                    //model.moveAgentRandom();
                    updateQValues();

                }
            }
        }.start();
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

    private void makeActionChoice() {
        State cs = model.getAgent().currentState;
        if (cs.getActions().size() <= 0) {
            return;
        }

        double max = 0;
        HashMap<Double, Action> kPowerActionValues = new HashMap<>();
        for (Action act : cs.getActions()) {
            if (act.getValue() > max) {
                max = act.getValue();
            }
            kPowerActionValues.put(Math.pow(explorationValue, (act.getValue())), act);
        }

        if (max == 0) {
            model.moveAgentRandom();
            return;
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
        System.out.println(total);
        int ran = random.nextInt((int) (total * 1000.0));


        double cumulative = 0;
        for (Map.Entry<Double, Action> entry : kPowerActionValues.entrySet()) {
            cumulative = cumulative + (entry.getKey()*1000.0);
            System.out.println("DUB: " + entry.getKey() + " CUMULA: " + cumulative + " RAN: " + ran + " CURRENT STATE: " + model.getAgent().currentState + " ACTION: " + entry.getValue());
            if(ran < cumulative) {
                model.getAgent().doAction(entry.getValue());
                System.out.println("Breaking");
                break;
            }
        }
        System.out.println("############# END ##############");
    }

    private void updateQValues() {
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

    private void removeRandomAction() {
        Random rng = new Random();
        int numY = rng.nextInt(model.getGridWorldStates().length);
        int numX = rng.nextInt(model.getGridWorldStates()[0].length);
        ArrayList<Action> acts = model.getGridWorldStates()[numY][numX].getActions();
        if (acts.size() > 0) {
            acts.remove(rng.nextInt(acts.size()));
        }
    }
}
