package GridWorld;

import java.util.LinkedList;

/**
 * Created by max on 16/10/2016.
 */
public class Agent {

    GridWorldState currentState;
    LinkedList<Action> actionsTaken;

    private final int MAX_ACTIONS_HISTORY = 100;

    public Agent() {
        actionsTaken = new LinkedList<Action>();
    }

    public void doAction(Action act) {
        if (!currentState.getActions().contains((act))) {
            System.out.println("The agent attempted to move to a state it isn't able to!");
            return;
        }
        currentState = act.resultingState;
        addActionToRecord(act);
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

}
