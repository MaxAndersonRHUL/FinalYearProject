package RL;

import java.util.ArrayList;

/**
 * Created by max on 16/10/2016.
 */
public abstract class State {

    protected ArrayList<Action> actions;
    public double reward = 0;

    public abstract StateIdentity getStateIdentity();

    public State() {
        actions = new ArrayList<Action>();
    }

    public void addAction(Action act) {
        actions.add(act);
    }

    public ArrayList<Action> getActions() {
        return actions;
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
