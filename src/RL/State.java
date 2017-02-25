package RL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by max on 16/10/2016.
 */
public abstract class State implements Serializable{

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

    public void removeAllActions() {
        actions.clear();
    }

    public double getReward() {
        return reward;
    }

    public List<Action> getHighestActionValues() {
        return getHighestActionValuesToPrecision(Double.SIZE - 1);
    }

    public List<Action> getHighestActionValuesToPrecision(int precision) {
        Model model = CurrentSimulationReference.model;
        List<Action> highestActions = new ArrayList<Action>();
        double highestVal = -1;
        for(Action act : actions) {
            double actValue = model.round(act.value, precision);
            if(actValue > highestVal) {
                highestActions.clear();
                highestActions.add(act);
                highestVal = actValue;
            } else if(actValue == highestVal) {
                highestActions.add(act);
            }
        }
        return highestActions;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    // A state asks if it can transition to this state.
    public boolean addActionToState(State state) {
        state.addAction(new Action(this, 0));
        return true;
    }

}
