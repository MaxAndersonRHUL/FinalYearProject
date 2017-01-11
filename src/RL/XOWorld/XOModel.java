package RL.XOWorld;

import RL.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by max on 11/12/2016.
 */
public class XOModel extends Model {

    int boardSizeX = 2;
    int boardSizeY = 2;

    int amountAdjacentToWin = 2;

    int rewardForWin = 10;
    int rewardForLoss = -10;

    private static XOModel instance;
    XOState previousState;

    // We use an exit flag to display the last board state, as otherwise, as soon as a win condition is met,
    // the board is reset before being displayed on the screen. As this flag is checked and acted upon in the
    // stateChanged method, the decrease to simulation speed is 1 iteration of the simulation.
    private boolean exitFlag;

    private XOState initialState;

    LocationState playerMarker = LocationState.CROSS;
    LocationState opponentMarker = LocationState.NAUGHT;

    ArrayList<Point> opponentStates;

    public static XOModel getInstance() {
        if(instance == null) {
            instance = new XOModel();
        }
        return instance;
    }

    private XOModel() {
        opponentStates = new ArrayList<>();
    }

    private void resetSimulation() {
        opponentStates.clear();
        mainAgent.currentState = initialState;
        System.out.println("Rest simulation. Start state is: " + initialState);
        previousState = null;
    }

    public XOBoard copyPlayerMarkersToBoard(XOBoard fromBoard, XOBoard toBoard) {
        LocationState[][] clonedBoard = cloneArray(toBoard.board);
        for(int y = 0; y < boardSizeY; y++) {
            for(int x = 0; x < boardSizeX; x++) {
                if(fromBoard.board[y][x] == playerMarker) {
                    clonedBoard[y][x] = playerMarker;
                }
            }
        }
        return new XOBoard(clonedBoard);
    }

    @Override
    public State decideActionChoiceResult(Action act) {
        // We want any state that results from an action. MostProbableState is used here as it is an easily accessible action.
        // Since every possible state that results from this action has the location of the learning algorithms choice, we can pick
        // any action and remove the opponent marker.

        System.out.println("CurrentState in ActionChoice: \n " + mainAgent.currentState);
        System.out.println("currentState action size: " + mainAgent.currentState.getActions().size());

        if(mainAgent.currentState.getActions().size() == 1) {
            System.out.println("Setting agent state");
            return mainAgent.currentState.getActions().get(0).getMostProbableState();
        }

        XOBoard prevBoard = (XOBoard) mainAgent.currentState.getStateIdentity();
        XOBoard playerMovedBoard = copyPlayerMarkersToBoard((XOBoard) act.getMostProbableState().getStateIdentity(), prevBoard);
        XOBoard board = randomOpponentMove(playerMovedBoard);

        return states.get(board);
    }

    /*
    public XOBoard getFullBoard(XOBoard shortBread) {

        XOBoard shortBoard = new XOBoard(cloneArray(shortBread.board));

        for(Point location : opponentStates) {
            /*
            if(shortBoard.board[location.y][location.x] != LocationState.EMPTY) {
                System.out.println("Error with getting full board! There is already a marker in an enemy position");
                System.out.println("At location: " + location.x + ", " + location.y);
                System.out.println("Working with shortBoard: \n" + shortBoard);
                System.out.println("Agent's current state: \n" + mainAgent.currentState);
                System.out.println("Opponent Positions: ");
                for(Point point : opponentStates) {
                    System.out.println(point);
                }
                for(Action act : mainAgent.currentState.getActions()) {
                    System.out.println(act.resultingState);
                }

                return null;
            }

            shortBoard.board[location.y][location.x] = opponentMarker;
        }
        return shortBoard;
    }
    */

    @Override
    public void stateChanged() {
/*
        if(exitFlag) {
            resetSimulation();
            exitFlag = false;
            return;
        }
*/
        if(checkWinCondition((XOBoard) mainAgent.currentState.getStateIdentity(), playerMarker)) {
            CurrentSimulationReference.view.setStatusText(playerMarker.name() + " WINS!");
            resetSimulation();
            exitFlag = true;
            return;
        }

        /*
        for(Action act : mainAgent.currentState.getActions()) {
            System.out.println("Act value: " + act.getValue());
        }
        */

        if(previousState != null) {
            //reactivateAllActionsInState(previousState);
        }


        if(mainAgent.currentState.getActions().size() == 0) {
            buildNextPossibleStateSet((XOState) mainAgent.currentState);
        }

        if(checkWinCondition((XOBoard) mainAgent.currentState.getStateIdentity(), opponentMarker)) {
            CurrentSimulationReference.view.setStatusText(opponentMarker.name() + " WINS!");
            resetSimulation();
            exitFlag = true;
            return;
        }

        //deactivateActionsNotAllowed();

        previousState = (XOState) mainAgent.currentState;

    }

    public void reactivateAllActionsInState(XOState state) {
        for(Action act : state.getActions()) {
            act.setActive(true);
        }
    }

    public void deactivateActionsNotAllowed() {
        XOBoard board = (XOBoard) (mainAgent.currentState.getStateIdentity());
        for(Point point : opponentStates) {
            LocationState[][] clonedBoard = cloneArray(board.board);
            clonedBoard[point.y][point.x] = playerMarker;
            XOBoard newBoard = new XOBoard(clonedBoard);

            Action action = getStateTransitionTo(mainAgent.currentState, states.get(newBoard));

            if(action != null) {
                action.setActive(false);
            } else {
            }
        }
    }

    public XOBoard randomOpponentMove(XOBoard addingBoard) {

        LocationState[][] nboard = cloneArray(addingBoard.board);

        int i = 0;

        for(int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                if(nboard[y][x] == LocationState.EMPTY) {
                    i++;
                }
            }
        }
        Random rand = new Random();
        int num = rand.nextInt(i);

        i = 0;
        for(int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                if(nboard[y][x] == LocationState.EMPTY) {
                    if(i == num) {
                        nboard[y][x] = opponentMarker;
                        return new XOBoard(nboard);
                    }
                    i++;
                }
            }
        }

        return null;

        //XOBoard boardID = new XOBoard(nboard);

        /*
        if(states.containsKey(boardID)) {
            mainAgent.currentState = states.get(new XOBoard(nboard));
            return;
        } else {
            XOState state = new XOState(boardID);
            mainAgent.currentState = state;
            addState(state);
        }
        */
        //System.out.println(mainAgent.currentState);
    }

    @Override
    public void addState(State state, StateIdentity identity) {
        super.addState(state, identity);
        if(checkWinCondition((XOBoard) state.getStateIdentity(), playerMarker)) {
            state.setReward(rewardForWin);
        }
        if(checkWinCondition((XOBoard) state.getStateIdentity(), opponentMarker)) {
            state.setReward(rewardForLoss);
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

    public void removeEnemyStates(LocationState[][] board) {
        for(int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                if(board[y][x] == opponentMarker) {
                    board[y][x] = LocationState.EMPTY;
                }
            }
        }
    }

    public void buildNextPossibleStateSet(XOState state) {
         //LocationState[][] board = getFullBoard(state.getStateIdentity()).board;
        LocationState[][] board = state.getStateIdentity().board;

        int placesFree = boardSizeX * boardSizeY;

        for(int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                if(board[y][x] == LocationState.EMPTY) {
                    placesFree --;
                    LocationState[][] playerLocBoard = cloneArray(board);
                    playerLocBoard[y][x] = playerMarker;

                    Action thisAction = new Action();
                    state.addAction(thisAction);

                    for(int y1 = 0; y1 < boardSizeY; y1++) {
                        for (int x1 = 0; x1 < boardSizeX; x1++) {
                            if (playerLocBoard[y1][x1] == LocationState.EMPTY) {
                                LocationState[][] actionBoard = cloneArray(playerLocBoard);
                                actionBoard[y1][x1] = opponentMarker;
                                XOState newState = new XOState(new XOBoard(actionBoard));
                                thisAction.addResultingState(newState, 0.1);
                                addState(newState);
                            }
                        }
                    }
                }
            }
        }
        if(placesFree == (boardSizeX * boardSizeY) - 1 ) {
            for(int y = 0; y < boardSizeY; y++) {
                for (int x = 0; x < boardSizeX; x++) {
                    if (board[y][x] == LocationState.EMPTY) {
                        LocationState[][] playerLocBoard = cloneArray(board);
                        playerLocBoard[y][x] = playerMarker;
                        // Get the state that was added by the previous series of loops.
                        Action thisAction = state.getActions().get(0);
                        XOState newState = new XOState(new XOBoard(playerLocBoard));
                        addState(newState);
                        thisAction.addResultingState(newState, 1);
                    }
                }
            }
        }
    }

    public boolean checkWinCondition(XOBoard bread, LocationState playerMarker) {

        // Check for a draw condition if every position is taken
        int totalFree = boardSizeX * boardSizeY;

        for(int y = 0; y < boardSizeY; y++) {
            for (int x = 0; x < boardSizeX; x++) {
                if(bread.board[y][x] == playerMarker) {
                    totalFree--;
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
                            if(down && bread.board[y+i][x] != playerMarker) {
                                down = false;
                            }
                        } else {
                            down = false;
                        }

                        if(y-i >= 0) {
                            if(up && bread.board[y-i][x] != playerMarker) {
                                up = false;
                            }
                        } else {
                            up = false;
                        }

                        if(x+i < boardSizeX) {
                            if(left && bread.board[y][x+i] != playerMarker) {
                                left = false;
                            }
                        } else {
                            left = false;
                        }

                        if(x-i >= 0) {
                            if(right && bread.board[y][x-i] != playerMarker) {
                                right = false;
                            }
                        } else {
                            right = false;
                        }

                        if(y+i < boardSizeY && x-i >= 0) {
                            if(downleft && bread.board[y+i][x-i] != playerMarker) {
                                downleft = false;
                            }
                        } else {
                            downleft = false;
                        }

                        if(y+i < boardSizeY && x+i < boardSizeX) {
                            if(downright && bread.board[y+i][x+i] != playerMarker) {
                                downright = false;
                            }
                        } else {
                            downright = false;
                        }

                        if(y-i >= 0 && x - i >= 0) {
                            if(upleft && bread.board[y-i][x-i] != playerMarker) {
                                upleft = false;
                            }
                        } else {
                            upleft = false;
                        }

                        if(y-i >= 0 && x+i < boardSizeX) {
                            if(upright && bread.board[y-i][x+i] != playerMarker) {
                                upright = false;
                            }
                        } else {
                            upright = false;
                        }
                    }
                    if (up || down || left || right || upleft || upright || downleft || downright) {
                        return true;
                    }
                } else if(bread.board[y][x] == opponentMarker) {
                    totalFree--;
                }
            }
        }
        if(totalFree == 0) {
            return true;
        }

        return false;
    }
}
