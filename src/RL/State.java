package RL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by max on 16/10/2016.
 */
public abstract class State {

    protected List<Action> actions;
    public double reward = 0;

    public abstract StateIdentity getStateIdentity();

    public State() {
        actions = Collections.synchronizedList(new ArrayList<Action>());
    }

    public void addAction(Action act) {
        actions.add(act);
    }

    public List<Action> getActions() {
        return actions;
    }

    public List<Action> getActiveActions() {
        List<Action> activeActions = new ArrayList<>();
        for(Action action : actions) {
            if(action.active) {
                activeActions.add(action);
            }
        }
        return activeActions;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    // A state asks if it can transition to this state.
    public boolean addActionToState(State state) {
        state.addAction(new Action(this));
        return true;
    }

}
