package GridWorld;

import java.util.ArrayList;

/**
 * Created by max on 16/10/2016.
 */
public class State {

    ArrayList<Action> actions;
    double reward = 0;

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

}
