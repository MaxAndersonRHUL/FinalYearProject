package RL.gridWorld;

import RL.*;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by max on 17/10/2016.
 */
public class GridWorldModel extends Model {

    static GridWorldModel instance;

    private boolean isEpisodic = false;

    public static GridWorldModel getInstance() {
        if(instance == null) {
            instance = new GridWorldModel();
        }
        return instance;
    }

    @Override
    public void stateChanged() {
        super.stateChanged();
        if (isEpisodic) {
            if (getAgent().getCurrentState().getReward() != 0) {
                getAgent().forceSetCurrentState(startingState);
            }
        }
    }

    public void setupEmptyGrid() {
        fillStates();
        startingState = states.get(new GridWorldCoordinate(0,0));
        mainAgent = new Agent();
        mainAgent.forceSetCurrentState(startingState);
    }

    public void setupFullGrid() {
        setupEmptyGrid();
        fillActions();
    }

    public void setEpisodic(boolean episodic) {
        isEpisodic = episodic;
    }

    @Override
    public State decideActionChoiceResult(Action action) {
        if(action.getResultingStates().size() == 1) {
            return action.getResultingStates().keySet().iterator().next();
        }
        State result = Probability.randomChooseStateFromHashMap(action.getResultingStates());
        return result;
    }

    void printAllHashCodes() {
        for(StateIdentity state : states.keySet()) {
            System.out.println(state.hashCode());
        }
    }

    public void fillStates() {
        states = new ConcurrentHashMap<StateIdentity, State>();
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                GridWorldState state = new GridWorldState(new GridWorldCoordinate(x, y));
                addState(state);
            }
        }
    }

    public void fillActions() {
        for (int y = 0; y < gridSizeY; y++) {
            for (int x = 0; x < gridSizeX; x++) {
                GridWorldCoordinate identity = new GridWorldCoordinate(x, y);
                State identityState = states.get(identity);
                if (x > 0) {
                    states.get(new GridWorldCoordinate(x-1, y)).addActionToState(identityState, 1);
                }
                if (x < gridSizeX - 1) {
                    states.get(new GridWorldCoordinate(x+1, y)).addActionToState(identityState, 1);
                }
                if (y < gridSizeY - 1) {
                    states.get(new GridWorldCoordinate(x, y+1)).addActionToState(identityState, 1);
                }
                if (y > 0) {
                    states.get(new GridWorldCoordinate(x, y-1)).addActionToState(identityState, 1);
                }

            }
        }
        // Setting up custom rules for non-deterministic environments.
        /*
        for(Action act : states.get(new GridWorldCoordinate(2,2)).getActions()) {
            for(Map.Entry<State, Probability> entry : act.getResultingStates().entrySet()) {
                entry.getValue().setProbability(0.6);
            }
            act.addResultingState(states.get(new GridWorldCoordinate(6,4)), 0.4);
        }
        */

    }

    protected boolean canStateTransitionTo(GridWorldState state1, GridWorldState state2) {
        if(state1.getStateIdentity().x == state2.getStateIdentity().x - 1 && state1.getStateIdentity().y == state2.getStateIdentity().y) {
            return true;
        }
        if(state1.getStateIdentity().x == state2.getStateIdentity().x + 1 && state1.getStateIdentity().y == state2.getStateIdentity().y) {
            return true;
        }
        if(state1.getStateIdentity().x == state2.getStateIdentity().x && state1.getStateIdentity().y == state2.getStateIdentity().y - 1) {
            return true;
        }
        if(state1.getStateIdentity().x == state2.getStateIdentity().x && state1.getStateIdentity().y == state2.getStateIdentity().y + 1) {
            return true;
        }
        return false;
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
