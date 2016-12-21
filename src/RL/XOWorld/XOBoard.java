package RL.XOWorld;

import RL.StateIdentity;

/**
 * Created by max on 11/12/2016.
 */
public class XOBoard extends StateIdentity{

    LocationState[][] board;
    int savedHashCode;

    public XOBoard(LocationState[][] board) {
        this.board = board;
        savedHashCode = makeHashCode(board);
    }

    public int makeHashCode(LocationState[][] board) {
        int hc=board[0].length * board.length;
        for (int j = 0; j<board.length; j++) {
            for (int i = 0; i < board[0].length; i++) {
                if(board[j][i] == LocationState.CROSS) {
                    hc = (hc*31);
                } else if(board[j][i] == LocationState.NAUGHT) {
                    hc = (hc*31) + 1;
                } else if(board[j][i] == LocationState.EMPTY) {
                    hc = (hc*31) + 2;
                }

            }
        }
        return hc;
    }

    public String toString() {
        String string = "";
        for (int j = 0; j<board.length; j++) {
            for (int i = 0; i < board[0].length; i++) {
                if(board[j][i] == LocationState.EMPTY) {
                    string = string + "-";
                } else if(board[j][i] == LocationState.CROSS) {
                    string = string + "X";
                } else if(board[j][i] == LocationState.NAUGHT) {
                    string = string + "O";
                }
            }
            string  = string + "\n";
        }
        return string;
    }

    @Override
    public int hashCode() {
        return savedHashCode;
    }

    public boolean equals(Object o) {
        if(o instanceof  XOBoard) {
            return hashCode() == o.hashCode();
        }
        return false;
    }

}
