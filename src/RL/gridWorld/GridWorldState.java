package RL.gridWorld;

import RL.Action;
import RL.State;
import javafx.scene.paint.Color;

/**
 * Created by max on 16/10/2016.
 */
public class GridWorldState extends State {

    private Color stateColor = null;
    private GridWorldCoordinate identity;

    public GridWorldState(GridWorldCoordinate location) {
        super();
        identity = location;
    }

    public int getX() {
        return getStateIdentity().x;
    }

    public int getY() {
        return getStateIdentity().y;
    }

    public void removeAction(Action act) {
        actions.remove(act);
    }

    public String toString() {
        return getStateIdentity().toString();
    }

    public Color getStateColor() {
        return stateColor;
    }

    public void setStateColor(Color color) {
        stateColor = color;
    }

    @Override
    public GridWorldCoordinate getStateIdentity() {
        return identity;
    }
}
