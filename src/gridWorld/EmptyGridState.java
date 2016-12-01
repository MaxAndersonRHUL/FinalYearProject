package gridWorld;

import javafx.scene.paint.Color;

/**
 * Created by max on 19/10/2016.
 */
public class EmptyGridState extends GridWorldState {

    public EmptyGridState(int locX, int locY) {
        super(locX, locY);
        setStateColor(Color.CHOCOLATE);
    }

    public void addAction(Action act) {
        return;
    }

    public double getReward() {
        return 0;
    }

    // States cannot have actions that go to this state.
    public boolean addActionToState(State state) {
        return false;
    }
}
