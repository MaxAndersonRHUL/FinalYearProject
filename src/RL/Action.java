package RL;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by max on 16/10/2016.
 */
public class Action implements Serializable{

    protected HashMap<State, Probability> resultingStates = new HashMap<>();

    // An actions value is not to be confused the value iteration - the value variable in this case
    // is a general purpose variable that learning controllers can use to conveniently save some
    // information to an action, without having to use there own data structures. In the case of the Q-learning
    // controller, the value variable is the current Q-value estimation.
    protected double value = 0;
    // How many times has the agent taken this action?
    private long amountOfTimesTaken;
    boolean active = true;

    private State highestProbabilityState = null;
    private double highestProbability = -1;

    protected double totalProability = 0;

    public Action(State resultState, double probability) {
        addResultingState(resultState, probability);
    }

    // Initialization of an action without resulting states is allowed.
    public Action() {

    }

    public void incrTimesTaken() {
        amountOfTimesTaken++;
    }

    public long getAmountOfTimesTaken() {
        return amountOfTimesTaken;
    }

    // Action probabilities do not need to add up 1. Probabilities are all relative to each other.
    // for example, the probabilities for each resulting state could add up to 300, and the chance
    // for a state with probability of 150 to be the result would be 50%.
    public State getMostProbableState() {
        return highestProbabilityState;
    }

    public void addResultingState(State resultState, double probability) {
        addResultingState(resultState, new Probability(probability));
    }

    public void addResultingState(State resultState, Probability probability) {
        resultingStates.put(resultState, probability);
        totalProability = totalProability + probability.probab;
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
