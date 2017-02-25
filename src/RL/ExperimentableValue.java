package RL;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by max on 23/02/2017.
 */
public class ExperimentableValue {

    private ObservableNumber value;
    private SimpleStringProperty name;
    private SimpleLongProperty amountOfRecords;

    public ExperimentableValue(Number wVal, String valName) {
        value = new ObservableNumber(wVal);
        name = new SimpleStringProperty(valName);
        amountOfRecords = new SimpleLongProperty(0);
        ExperimentationController.registerExperimentableValue(this);
    }

    public String getName() {
        return name.getValue();
    }

    public SimpleStringProperty getObservableName() {
        return name;
    }

    public Number getValue() {
        return value.value;
    }

    public void setValue(Number nVal) {
        value.setValue(nVal);
    }

    public ObservableNumber getObservableValue() {
        return value;
    }

    public void setAmountOfRecords(long num) {
        amountOfRecords.set(num);
    }

    public SimpleLongProperty getObservableAmountOfRecords(){
        return amountOfRecords;
    }

}
