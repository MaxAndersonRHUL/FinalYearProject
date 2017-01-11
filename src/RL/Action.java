package RL;

import java.util.HashMap;

/**
 * Created by max on 16/10/2016.
 */
public class Action {

    public HashMap<State, Probability> resultingStates = new HashMap<>();
    protected double value = 0;
    boolean active = true;

    private State highestProbabilityState = null;
    private double highestProbability = -1;

    public Action(State resultState, double probability) {
        addResultingState(resultState, probability);
    }

    public Action() {

    }

    public State getMostProbableState() {
        return highestProbabilityState;
    }

    public void addResultingState(State resultState, double probability) {
        addResultingState(resultState, new Probability(probability));
    }

    public void addResultingState(State resultState, Probability probability) {
        resultingStates.put(resultState, probability);
        if(probability.probab > highestProbability) {
            highestProbability = probability.probab;
            highestProbabilityState = resultState;
        }
    }

    public HashMap<State, Probability> getResultingStates() {
        return resultingStates;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double newValue) {
        value = newValue;
    }

    public String toString() {
        return "Action[ Result State: " + resultingStates + " ]";
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
