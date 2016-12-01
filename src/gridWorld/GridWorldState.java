package gridWorld;

import javafx.scene.paint.Color;

/**
 * Created by max on 16/10/2016.
 */
public class GridWorldState extends State {

    private Color stateColor = null;

    public int x, y;

    public GridWorldState(int locX, int locY) {
        super();
        x = locX;
        y = locY;
    }

    // A state asks if it can transition to this state.
    public boolean addActionToState(State state) {
        state.addAction(new Action(this));
        return true;
    }

    public void removeAction(Action act) {
        actions.remove(act);
    }

    public String toString() {
        return "GridWorldState[ X: " + x + " Y: " + y  + " ]";
    }

    public Color getStateColor() {
        return stateColor;
    }

    public void setStateColor(Color color) {
        stateColor = color;
    }

}
