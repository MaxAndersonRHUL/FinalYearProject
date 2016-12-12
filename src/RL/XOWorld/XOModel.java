package RL.XOWorld;

import RL.*;

import java.util.HashMap;

/**
 * Created by max on 11/12/2016.
 */
public class XOModel extends Model {

    int boardSizeX = 3;
    int boardSizeY = 3;

    int amountAdjacentToWin = 3;

    int rewardForWin = 10;

    private static XOModel instance;

    private XOState initialState;

    private LocationState playerMarker = LocationState.CROSS;

    public static XOModel getInstance() {
        if(instance == null) {
            instance = new XOModel();
        }
        return instance;
    }

    public void resetSimulation() {
        mainAgent.currentState = initialState;
    }

    @Override
    public void stateChanged() {
        buildNextPossibleStateSet((XOState) mainAgent.currentState);
        if(checkWinCondition((XOBoard) mainAgent.currentState.getStateIdentity(), playerMarker)) {
            resetSimulation();
        }
    }

    @Override
    public void addState(State state, StateIdentity identity) {
        super.addState(state, identity);
        if(checkWinCondition((XOBoard) state.getStateIdentity(), playerMarker)) {
            state.setReward(10);
        }
    }

    public void setupInitialState() {

        states = new HashMap<StateIdentity, State>();

        LocationState[][] startBoard = new LocationState[boardSizeY][boardSizeX];
        for(int y = 0; y < boardSizeY; y++) {
            for(int x = 0; x < boardSizeX; x++) {
                startBoard[y][x] = LocationState.EMPTY;
            }
        }
        XOState startState = new XOState(new XOBoard(startBoard));

        initialState = startState;

        addState(startState);

        buildNextPossibleStateSet(startState);

        this.startingState = startState;
        mainAgent = new Agent();
        mainAgent.currentState = startState;
    }

    public LocationState[][] cloneArray(LocationState[][] initial) {
        LocationState[][] result = new LocationState[initial.length][];
        for (int r = 0; r < initial.length; r++) {
            result[r] = initial[r].clone();
        }
        return result;
    }

    public void buildNextPossibleStateSet(XOState state) {
        LocationState[][] board = state.getStateIdentity().board;

        for(int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                if(board[y][x] == LocationState.EMPTY) {
                    LocationState[][] newBoard = cloneArray(board);
                    newBoard[y][x] = LocationState.CROSS;
                    XOState newState = new XOState(new XOBoard(newBoard));
                    state.addAction(new Action(newState));
                    addState(newState);
                }
            }
        }
    }

    public boolean checkWinCondition(XOBoard board, LocationState playerMarker) {
        for(int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                if(board.board[y][x] == playerMarker) {

                    boolean up = true;
                    boolean down = true;
                    boolean left = true;
                    boolean right = true;
                    boolean upleft = true;
                    boolean downleft = true;
                    boolean upright = true;
                    boolean downright = true;

                    for(int i = 0; i < amountAdjacentToWin; i++) {

                        if(y+i < boardSizeY) {
                            if(down && board.board[y+i][x] != playerMarker) {
                                down = false;
                            }
                        } else {
                            down = false;
                        }

                        if(y-i >= 0) {
                            if(up && board.board[y-i][x] != playerMarker) {
                                up = false;
                            }
                        } else {
                            up = false;
                        }

                        if(x+i < boardSizeX) {
                            if(left && board.board[y][x+i] != playerMarker) {
                                left = false;
                            }
                        } else {
                            left = false;
                        }

                        if(x-i >= 0) {
                            if(right && board.board[y][x-i] != playerMarker) {
                                right = false;
                            }
                        } else {
                            right = false;
                        }

                        if(y+i < boardSizeY && x-i >= 0) {
                            if(downleft && board.board[y+i][x-i] != playerMarker) {
                                downleft = false;
                            }
                        } else {
                            downleft = false;
                        }

                        if(y+i < boardSizeY && x+i < boardSizeX) {
                            if(downright && board.board[y+i][x+i] != playerMarker) {
                                downright = false;
                            }
                        } else {
                            downright = false;
                        }

                        if(y-i >= 0 && x - i >= 0) {
                            if(upleft && board.board[y-i][x-i] != playerMarker) {
                                upleft = false;
                            }
                        } else {
                            upleft = false;
                        }

                        if(y-i >= 0 && x+i < boardSizeX) {
                            if(upright && board.board[y-i][x+i] != playerMarker) {
                                upright = false;
                            }
                        } else {
                            upright = false;
                        }
                    }
                    if (up || down || left || right || upleft || upright || downleft || downright) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
