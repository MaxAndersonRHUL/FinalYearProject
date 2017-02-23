package RL;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by max on 23/02/2017.
 */
public class GraphView {

    private static LineChart<Number, Number>  lineChart;
    private static XYChart.Series series;

    private static ScheduledExecutorService updateControl;

    private static ExperimentableValue currentShowing;

    private static final long valueUpdateTimeStep = 2000;

    private static boolean dataAdded = false;

    private static NumberAxis yAxis;

    public static void start() {
        VBox root = new VBox();

        HBox content = new HBox();

        Text title = new Text("Graphs");
        title.setFont(new Font(20));

        TableView<ExperimentableValue> tableView = new TableView<ExperimentableValue>();
        tableView.setItems(ExperimentationController.getObservableValues());



        TableColumn<ExperimentableValue,Number> valCol = new TableColumn<ExperimentableValue,Number>("Value");

        valCol.setCellValueFactory(
                cellData -> cellData.getValue().getObservableValue());

        valCol.setCellFactory(column -> {
            return new TableCell<ExperimentableValue, Number>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item != null) {
                        setText(item.toString());
                    }
                }
            };
        });

        TableColumn<ExperimentableValue,String> nameCol = new TableColumn<ExperimentableValue,String>("Name");

        nameCol.setCellValueFactory(
                cellData -> cellData.getValue().getObservableName());



        tableView.getColumns().setAll(nameCol, valCol);

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            showExperimentalValueChart(newSelection);
        });

        yAxis = new NumberAxis();
        NumberAxis xAxis = new NumberAxis();

        xAxis.setUpperBound(1000000);

        xAxis.setLabel("Iterations");
        lineChart = new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setCreateSymbols(false);

        series = new XYChart.Series();

        lineChart.getData().add(series);

        content.getChildren().addAll(lineChart, tableView);

        root.setAlignment(Pos.TOP_CENTER);
        content.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(title, content);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        periodicallyUpdateGraph();

    }

    public static void dataAdded() {
        dataAdded = true;
    }

    public static void periodicallyUpdateGraph() {
        Runnable updateVals = new Runnable() {
            public void run() {
                if(currentShowing != null && dataAdded) {
                    showExperimentalValueChart(currentShowing);
                }
            }
        };

        updateControl = Executors.newScheduledThreadPool(1);
        updateControl.scheduleAtFixedRate(updateVals, 0, valueUpdateTimeStep, TimeUnit.MILLISECONDS);

    }

    public static void showExperimentalValueChart(ExperimentableValue exprVal) {
        series = new XYChart.Series();
        currentShowing = exprVal;
        dataAdded = false;
        yAxis.setLabel(exprVal.getName());
        ArrayList<VariableRecord> records = ExperimentationController.getRecordedValuesOfExperementableVar(exprVal);
        if(records == null) {
            return;
        }

        ArrayList<XYChart.Data> list = new ArrayList<>();
        for(VariableRecord record : records) {
            list.add(new XYChart.Data(record.getIterationsRecordedOn(), record.getValue()));
        }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    series.getData().addAll(list);
                    lineChart.getData().clear();
                    lineChart.getData().add(series);
                }
            });
    }

}
