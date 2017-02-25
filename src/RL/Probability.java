package RL;

import java.io.Serializable;
import java.util.InputMismatchException;

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
        }
    }
}
