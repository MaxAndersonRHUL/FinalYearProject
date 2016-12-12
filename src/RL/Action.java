package RL;

/**
 * Created by max on 16/10/2016.
 */
public class Action {

    public State resultingState;
    protected double value = 0;

    public Action(State resultState) {
        resultingState = resultState;
    }

    public State getResultingState() {
        return resultingState;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double newValue) {
        value = newValue;
    }

    public String toString() {
        return "Action[ Result State: " + resultingState + " ]";
    }

}
