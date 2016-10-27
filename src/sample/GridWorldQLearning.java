package sample;

import sun.awt.image.ImageWatched;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

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
    private double explorationValue = 0.1;

    private ConcurrentLinkedDeque<Integer> forAverageIterationRate = new ConcurrentLinkedDeque<Integer>();

    private GridWorldQLearning() {
        model = GridWorldModel.getInstance();
        runTestThread();
    }

    public static GridWorldQLearning getInstance() {
        if(instance == null) {
            instance = new GridWorldQLearning();
        }
        return instance;
    }

    public void setIterationSpeed(int iterationsPerSecond) {
        targetFPS = iterationsPerSecond;
        evoRate = 1000000000 / targetFPS;
    }

    private int calculateAverage(ConcurrentLinkedDeque<Integer> valList) {
        int sum = 0;
        if(!valList.isEmpty()) {
            for (Integer mark : valList) {
                sum += mark;
            }
            return sum / valList.size();
        }
        return sum;
    }

    private void addValueToRateList(int val) {
        if(forAverageIterationRate.size() > evoRate) {
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
                while(true) {
                    long now = System.nanoTime();
                    long updateLength = now - timeSaveTemp;
                    addValueToRateList((int) (1/((now - timeSaveTemp)/ 1000000000.0)));
                    timeSaveTemp = now;

                    long sleepTime = (timeSaveTemp-System.nanoTime() + evoRate)/1000000;
                    if(sleepTime > 0) {
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

    private double sumOfArray(ArrayList<Double> list) {
        double sum = 0;
        for(Double d : list) {
            sum = sum + d;
        }
        return sum;
    }

    private void makeActionChoice() {
        State cs = model.getAgent().currentState;
        if(cs.getActions().size() <= 0) {
            return;
        }

        double max = 0;
        int index = 0;
        ArrayList<Double> kPowerActionValues = new ArrayList<>();
        for(Action act : cs.getActions()) {
            if(act.getValue() > max) {
                max = act.getValue();
                break;
            }
            index++;
            kPowerActionValues.add(Math.pow(explorationValue, (act.getValue())));
        }

        if(max == 0) {
            model.moveAgentRandom();
            return;
        }
        model.getAgent().doAction(cs.getActions().get(index));

        double sumValues = sumOfArray(kPowerActionValues);

        HashMap<Double, Action> probabilities = new HashMap<Double, Action>();
        for(int i = 0; i < kPowerActionValues.size(); i++) {
            Double value = kPowerActionValues.get(i)/sumValues;
            probabilities.put(value, cs.getActions().get(i));
            //System.out.println("Probability: " + value + " QValue: " + cs.getActions().get(i).value);
        }

        Random random = new Random();
        int ran = random.nextInt(1000);

        probabilities.entrySet().stream()
                .sorted(Map.Entry.<Double, Action>comparingByKey())
                .forEachOrdered(x -> probabilities.put(x.getKey(), x.getValue()));

        Action chosenAct = null;
        double cumulative = 0;
    }

    private void updateQValues() {
        Action highestAct = model.highestValueFromState(model.getAgent().currentState);
        double highestVal = 0;
        if(highestAct != null) {
            highestVal = highestAct.value;
        }
        double reward = model.getAgent().currentState.reward;
        Action lastAction = model.getAgent().getLastActionTaken();
        if(lastAction != null) {
            lastAction.value = (decayValue*highestVal) + reward;
        }
    }

    private void removeRandomAction() {
        Random rng = new Random();
        int numY = rng.nextInt(model.getGridWorldStates().length);
        int numX = rng.nextInt(model.getGridWorldStates()[0].length);
        ArrayList<Action> acts = model.getGridWorldStates()[numY][numX].getActions();
        if(acts.size() > 0) {
            acts.remove(rng.nextInt(acts.size()));
        }
    }
}
