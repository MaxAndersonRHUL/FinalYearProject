package RL;

import javafx.beans.value.ObservableValueBase;

/**
 * Created by max on 23/02/2017.
 */
public class ObservableNumber extends ObservableValueBase<Number> {

    Number value;

    public ObservableNumber(Number value) {
        super();
        this.value = value;
    }

    @Override
    public Number getValue() {
        return value;
    }

    public void setValue(Number value) {
        this.value = value;
        fireValueChangedEvent();
    }
}
