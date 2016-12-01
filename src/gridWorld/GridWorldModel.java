package gridWorld;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by max on 17/10/2016.
 */
public class GridWorldModel {

    private GridWorldState[][] states;

    private int gridSizeX = 10;
    private int gridSizeY = 5;

    private Agent mainAgent;

    private static GridWorldModel instance;

    private GridWorldModel() {
        instance = this;
    }

    public boolean canStateTransitionTo(State srcState, State dstState) {
        return getStateTransitionTo(srcState, dstState) != null;
    }

    public Action getStateTransitionTo(State srcState, State dstState) {
        //ArrayList<Action> acts = srcState.getActions()
        for (Action act : srcState.getActions()) {
            if (act.resultingState.equals(dstState)) {
                return act;
            }
        }
        return null;
    }

    public void setupEmptyGrid() {
        fillStates();
        mainAgent = new Agent();
        mainAgent.currentState = states[0][0];

    }

    public void setupFullGrid() {
        setupEmptyGrid();
        fillActions();
    }

    public static GridWorldModel getInstance() {
        if (instance == null) {
            instance = new GridWorldModel();
        }
        return instance;
    }

    public void fillStates() {
        states = new GridWorldState[gridSizeY][gridSizeX];
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                states[y][x] = new GridWorldState(x, y);
            }
        }
        states[1][1].setReward(5);
    }

    public void fillActions() {
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                if (x > 0) {
                    states[y][x - 1].addActionToState((states[y][x]));
                }
                if (x < gridSizeX - 1) {
                    states[y][x + 1].addActionToState((states[y][x]));
                }
                if (y < gridSizeY - 1) {
                    states[y + 1][x].addActionToState((states[y][x]));
                }
                if (y > 0) {
                    states[y - 1][x].addActionToState((states[y][x]));
                }
            }
        }
    }

    public void moveAgentRandom() {
        ArrayList<Action> actions = getAgent().currentState.getActions();
        if (actions.size() < 1) {
            return;
        }
        Random rng = new Random();
        int num = rng.nextInt(actions.size());
        Action exec = actions.get(num);
        getAgent().doAction(exec);
    }

    // Returns null if no actions on state, or if all actions are 0.
    public Action highestValueFromState(GridWorldState state) {
        Action highestAct = null;
        double highestVal = 0;
        for (Action act : state.getActions()) {
            if (act.value > highestVal) {
                highestVal = act.value;
                highestAct = act;
            }
        }
        return highestAct;
    }

    public Agent getAgent() {
        return mainAgent;
    }

    public GridWorldState[][] getGridWorldStates() {
        return states;
    }

    public int getGridSizeX() {
        return gridSizeX;
    }

    public int getGridSizeY() {
        return gridSizeY;
    }

    public void setGridSizeX(int size) {
        gridSizeX = size;
    }

    public void setGridSizeY(int size) {
        gridSizeY = size;
    }


}
