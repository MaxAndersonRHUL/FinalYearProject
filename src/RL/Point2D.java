package RL;

/**
 * Created by max on 17/12/2016.
 */
public class Point2D {
    public int x;
    public int y;

    public Point2D(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Code for fast hash function using bitshift operation instead of multiplication from
    // Stackoverflow user Boris Pavlović. http://stackoverflow.com/questions/3934100/good-hash-function-for-list-of-2-d-positions.
    public int hashCode() {
        int hash = 17;
        hash = ((hash + x) << 5) - (hash + x);
        hash = ((hash + y) << 5) - (hash + y);
        return hash;
    }

    public boolean equals(Object obj) {
        if(obj instanceof Point2D) {
            Point2D state = (Point2D) obj;
            return state.x == x && state.y == y;
        }
        return false;
    }
}
