package RL;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Created by max on 16/10/2016.
 */
public class Agent implements Serializable{

    private State currentState;
    protected LinkedList<Action> actionsTaken;

    private final int MAX_ACTIONS_HISTORY = 100;


    public Agent() {
        actionsTaken = new LinkedList<Action>();
    }

    public void doAction(Action act) {
        if (!currentState.getActions().contains((act))) {
            System.out.println("The agent attempted to take an action it does not have!");
            return;
        }
        //System.out.println("Agent is moving from state: \n" + currentState);
        //System.out.println("To state: \n" + act.resultingState);
        act.incrTimesTaken();
        addActionToRecord(act);
    }

    public State getCurrentState() {
        return currentState;
    }

    // Bypasses all probabilities and literally sets the current state of the agent.
    public void forceSetCurrentState(State state) {
        currentState = state;
    }

    // Due to memory issues when iterating thousands of actions per second,
    // only store the last 100.
    public void addActionToRecord(Action act) {
        if (actionsTaken.size() > MAX_ACTIONS_HISTORY) {
            actionsTaken.removeFirst();
        }
        actionsTaken.add(act);
    }

    public Action getLastActionTaken() {
        if (actionsTaken.size() < 1) {
            return null;
        }
        return actionsTaken.get(actionsTaken.size() - 1);
    }

    public void resetActionsTaken() {
        actionsTaken.clear();
    }

}
