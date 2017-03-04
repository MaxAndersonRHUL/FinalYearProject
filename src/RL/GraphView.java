package RL;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;
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
    private static NumberAxis xAxis;
    private static TableView<ExperimentableValue>  tableView;

    public static void start() {
        VBox root = new VBox();

        HBox content = new HBox();

        Text title = new Text("Graphs");
        title.setFont(new Font(20));

        tableView = new TableView<ExperimentableValue>();
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

        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        tableView.setRowFactory(new Callback<TableView<ExperimentableValue>, TableRow<ExperimentableValue>>() {
            @Override
            public TableRow<ExperimentableValue> call(TableView<ExperimentableValue> p) {
                final TableRow<ExperimentableValue> row = new TableRow<ExperimentableValue>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, e-> {
                    if (! row.isEmpty() && e.getClickCount() == 1) {
                        clickedTableRow(row);
                        e.consume();
                    }
                });
                return row;
            }
        });

        TableColumn<ExperimentableValue,String> nameCol = new TableColumn<ExperimentableValue,String>("Name");

        TableColumn<ExperimentableValue, Number> recordsCol = new TableColumn<>("Records");

        nameCol.setCellValueFactory(
                cellData -> cellData.getValue().getObservableName());

        recordsCol.setCellValueFactory(cellData -> cellData.getValue().getObservableAmountOfRecords());

        tableView.getColumns().setAll(nameCol, valCol, recordsCol);

        /*
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            showExperimentalValueChart(newSelection);
        });
        */

        tableView.getSelectionModel().clearSelection();

        nameCol.setPrefWidth(100);


        yAxis = new NumberAxis();
        xAxis = new NumberAxis();

        xAxis.setLabel("Iterations");
        lineChart = new LineChart<Number, Number>(xAxis, yAxis);

        lineChart.setCreateSymbols(false);

        series = new XYChart.Series();

        lineChart.getData().add(series);

        content.getChildren().addAll(lineChart, tableView);

        content.setPadding(new Insets(10,10,10,10));

        root.setAlignment(Pos.TOP_CENTER);
        content.setAlignment(Pos.TOP_CENTER);

        root.getChildren().addAll(title, content);

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        periodicallyUpdateGraph();

    }

    public static void clickedTableRow(IndexedCell cell) {
        if(tableView.getSelectionModel().isSelected(cell.getIndex())) {
            tableView.getSelectionModel().clearSelection(cell.getIndex());
            showExperimentalValueChart();
        } else {
            if(tableView.getSelectionModel().getSelectedCells().size() == 2) {
                tableView.getSelectionModel().clearSelection(tableView.getSelectionModel().getSelectedCells().get(0).getRow());
            }
            tableView.getSelectionModel().select(cell.getIndex());
            showExperimentalValueChart();
        }
    }

    public static void dataAdded() {
        dataAdded = true;
    }

    public static void periodicallyUpdateGraph() {
        Runnable updateVals = new Runnable() {
            public void run() {
                if(dataAdded) {
                    showExperimentalValueChart();
                }
            }
        };

        updateControl = Executors.newScheduledThreadPool(1);
        updateControl.scheduleAtFixedRate(updateVals, 0, valueUpdateTimeStep, TimeUnit.MILLISECONDS);

    }

    public static void showExperimentalValueChart() {
        ObservableList<ExperimentableValue> selectedCells = tableView.getSelectionModel().getSelectedItems();

        if(selectedCells.size() == 0) {
            return;
        }

        series = new XYChart.Series();
        dataAdded = false;

        if(selectedCells.size() == 1) {
            ExperimentableValue exprVal = selectedCells.get(0);


            ConcurrentLinkedDeque<VariableRecord> records = ExperimentationController.getRecordedValuesOfExperementableVar(exprVal);
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
                    yAxis.setLabel(exprVal.getName());
                    xAxis.setLabel("Iterations");
                    series.getData().addAll(list);
                    lineChart.getData().setAll(series);
                }
            });

        } else if(selectedCells.size() == 2) {
            ExperimentableValue exprValY = selectedCells.get(0);
            ExperimentableValue exprValX = selectedCells.get(1);
            yAxis.setLabel(exprValY.getName());
            xAxis.setLabel(exprValX.getName());

            ConcurrentLinkedDeque<VariableRecord> recordsY = ExperimentationController.getRecordedValuesOfExperementableVar(exprValY);
            ConcurrentLinkedDeque<VariableRecord> recordsX = ExperimentationController.getRecordedValuesOfExperementableVar(exprValX);

            if(recordsX == null || recordsY == null) {
                return;
            }

            ArrayList<XYChart.Data> dataList = new ArrayList<>();

            Iterator<VariableRecord> iterX = recordsX.iterator();
            VariableRecord currentX = iterX.next();
            Iterator<VariableRecord> iterY = recordsY.iterator();
            VariableRecord currentY = iterY.next();

            while(iterX.hasNext() && iterY.hasNext()) {
                long iterRecordedY = currentY.getIterationsRecordedOn();
                long iterRecordedX = currentX.getIterationsRecordedOn();
                if(iterRecordedY == iterRecordedX) {
                    dataList.add(new XYChart.Data(currentX.getValue(), currentY.getValue()));
                    currentX = iterX.next();
                    currentY = iterY.next();
                } else if(iterRecordedX > iterRecordedY) {
                    currentY = iterY.next();
                } else {
                    currentX = iterX.next();
                }
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    series.getData().addAll(dataList);
                    //lineChart.getData().clear();
                    lineChart.getData().setAll(series);
                    //lineChart.getData().add(series);
                }
            });

        }
    }

}
