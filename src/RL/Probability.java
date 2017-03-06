package RL;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by max on 26/12/2016.
 */
public class Probability implements Serializable{
    double probab;

    public Probability(double probability) {
        setProbability(probability);
    }

    public void setProbability(double probability) {
        if(probability > 100 || probability < 0) {
            System.out.println("Probability is not between 0 and 100");
            throw new InputMismatchException("Probability is not between 0 and 100");
        } else {
            probab = probability;
        }
    }

    public static int randomChooseFromDoubleArray(Double[] probabilities) {
        double total = 0;
        for(Double dub : probabilities) {
            total = total + dub;
        }

        double random = ThreadLocalRandom.current().nextDouble(0.0, total);

        double cumulativeTotal = 0.0;

        int randIndex = 0;
        for(Double dub : probabilities) {
            if(random <= dub+ cumulativeTotal) {
                return randIndex;
            }
            randIndex++;
            cumulativeTotal = cumulativeTotal +  dub;
        }
        return -1;
    }

    public static State randomChooseStateFromHashMap(HashMap<State, Probability> map) {
        double total = 0;
        ArrayList<Double> tempStorage = new ArrayList<Double>();
        for(Probability dub : map.values()) {
            total = total + dub.probab;
            tempStorage.add(dub.probab);
        }

        double random = ThreadLocalRandom.current().nextDouble(0.0, total);

        double cumulativeTotal = 0.0;
        for(Map.Entry<State, Probability> dub : map.entrySet()) {
            if(random <= dub.getValue().probab + cumulativeTotal) {
                return dub.getKey();
            }
            cumulativeTotal = cumulativeTotal +  dub.getValue().probab;
        }
        return null;
    }

    // 2 methods that do almost the same thing exist here to avoid the computational cost of
    // either wrapping doubles into new probability objects and putting them into a list,
    // or unwrapping a list of probabilities into a new list of doubles.
    public static double randomChooseFromProabilityList(Collection<Probability> probabilities) {
        double total = 0;
        ArrayList<Double> tempStorage = new ArrayList<Double>();
        for (Probability dub : probabilities) {
            total = total + dub.probab;
            tempStorage.add(dub.probab);
        }

        double random = ThreadLocalRandom.current().nextDouble(0.0, total);

        double cumulativeTotal = 0.0;
        for (Probability dub : probabilities) {
            if (dub.probab + cumulativeTotal <= random) {
                return dub.probab;
            }
            cumulativeTotal = cumulativeTotal + dub.probab;
        }
        return -1;
    }

}
