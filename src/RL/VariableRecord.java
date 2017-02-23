package RL;

/**
 * Created by max on 23/02/2017.
 */
public class VariableRecord {
    private Number value;
    private long iterationNumberRecorded;

    public VariableRecord(Number val, long iterations) {
        value = val;
        iterationNumberRecorded = iterations;
    }

    public Number getValue( ){
        return value;
    }

    public long getIterationsRecordedOn() {
        return iterationNumberRecorded;
    }

}
