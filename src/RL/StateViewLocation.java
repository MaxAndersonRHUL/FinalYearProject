package RL;

/**
 * Created by max on 20/12/2016.
 */
public class StateViewLocation {
    public State state;
    public Point2D location;

    public StateViewLocation(State state, Point2D location) {
        this.state = state;
        this.location = location;
    }

    public StateViewLocation(State state, int x, int y) {
        this.state = state;
        this.location = new Point2D(x, y);
    }

    public StateViewLocation() {}

}
