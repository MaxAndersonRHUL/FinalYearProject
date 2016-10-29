package sample;

/**
 * Created by max on 16/10/2016.
 */
public class Action {

    GridWorldState resultingState;
    double value;

    public Action(GridWorldState resultState) {
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
