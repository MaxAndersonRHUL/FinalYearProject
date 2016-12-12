package RL.gridWorld;

import RL.Action;
import RL.State;

/**
 * Created by max on 19/10/2016.
 */
public class EmptyGridState {


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
