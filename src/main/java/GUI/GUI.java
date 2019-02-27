package GUI;

import AIS.Antigen;
import AIS.Antibody;
import Algorithm.LegendaryOctoSniffle;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.events.Event;

import java.util.ArrayList;
import java.util.HashMap;

public class GUI extends BorderPane {

    private final static HashMap<String, Integer> dataSetLabelIndexes = new HashMap<>();
    static {
        dataSetLabelIndexes.put("iris.data", 4);
        dataSetLabelIndexes.put("wine.data", 0);
    }
    private final ChoiceBox<String> dataSetBox = new ChoiceBox<>(FXCollections.observableArrayList("iris.data", "wine.data"));
    private int labelIndex;

    private final Stage primaryStage;
    private final LegendaryOctoSniffle LOS;
    private StatisticGraph statisticGraph;

    private SolutionGraph solutionGraph;
    public final Button startButton = new Button("Start");
    public final Button stopButton = new Button("Stop");

    private final TextField inputIterations = new TextField("10");
    private final TextField inputPopulationSize = new TextField("100");
    private final TextField inputMutationRate = new TextField("0.8");
    private final TextField inputNumberOfTournaments = new TextField("5");
    private final TextField inputDataSetSplit = new TextField("0.1");
    public final TextField iterationTextField = new TextField();

    private final int sceneWidth = 1200;
    private final int sceneHeight = 850;
    private final int solutionGraphWidth = 600;
    private final int solutionGraphHeight = 600;
    private final int statisticGraphWidth = 800;
    private final int statisticGraphHeight = 500;

    private HBox menu;
    private VBox menuWrapper;
    private ArrayList<HashMap<String,ArrayList<Antibody>>> antibodyGenerations;

    private ChoiceBox<String> iterationBox;

    public GUI(Stage primaryStage, LegendaryOctoSniffle LOS){
        super();
        this.antibodyGenerations = null;
        this.primaryStage = primaryStage;
        this.LOS = LOS;
        final Scene scene = new Scene(this, sceneWidth, sceneHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("legendary-octo-sniffle");

        //make sure when selecting the data sets, the index of the label on each row is also set
        dataSetBox.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, number2) -> {
            labelIndex = dataSetLabelIndexes.getOrDefault(dataSetBox.getItems().get((int) number2), 0);;
        });

        iterationTextField.setPrefWidth(40);
        menuWrapper = new VBox(5);
        menuWrapper.setPadding(new Insets(5,0,5,0));
        dataSetBox.setValue("iris.data");
        dataSetBox.setPrefWidth(150);
        menu = new HBox(5);
        //menu.setPadding(new Insets(5,0,10,0));
        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(startButton, stopButton);
        menuWrapper.getChildren().addAll(menu);
        setTop(menuWrapper);

        VBox options = new VBox(10);
        options.setPadding(new Insets(5, 5, 5, 10));
        options.setAlignment(Pos.CENTER);
        options.getChildren().addAll(new Text("Iterations:"), inputIterations,
                new Text("Population size:"), inputPopulationSize,
                new Text("Mutation rate:"), inputMutationRate,
                new Text("Number of tournaments:"),inputNumberOfTournaments,
                new Text("Name of dataset:"),dataSetBox,
                new Text("Dataset split:"),inputDataSetSplit);
        setLeft(options);

        startButton.setOnAction((e) -> {
            LOS.run(Integer.valueOf(inputIterations.getText()),Integer.valueOf(inputPopulationSize.getText()),Double.valueOf(inputMutationRate.getText()),Integer.valueOf(inputNumberOfTournaments.getText()),dataSetBox.getValue(),labelIndex,Double.valueOf(inputDataSetSplit.getText()));
        });
        stopButton.setOnAction(event -> LOS.stopRunning());

        stopButton.setDisable(true);
        primaryStage.show();
    }

    public void setAntibodyGenerations(ArrayList<HashMap<String,ArrayList<Antibody>>> antibodyGenerations, HashMap<String,ArrayList<Antigen>> antigenMap, HashMap<String,ArrayList<Antigen>> antigenTestMap, HashMap<String,ArrayList<Antibody>> antibodyTestMap){
        this.antibodyGenerations = antibodyGenerations;

        HBox menu2 = new HBox(5);
        menu2.setAlignment(Pos.CENTER);
        menu2.getChildren().setAll(new Text("View iteration 0-"+(antibodyGenerations.size()-1)+":"),iterationTextField);
        menuWrapper.getChildren().setAll(menu,menu2);

        iterationTextField.setText("Test");
        iterationTextField.setOnKeyPressed((e) ->{
            boolean isInteger = tryParseInt(iterationTextField.getText());
            if(e.getCode() == KeyCode.ENTER){
                if(!isInteger){
                    this.drawSolution(antigenTestMap,antibodyTestMap);
                }else{
                    int iteration = Integer.valueOf(iterationTextField.getText());
                    if(iteration >= 0 && iteration <antibodyGenerations.size()){
                        HashMap<String,ArrayList<Antibody>> antibodyMap = antibodyGenerations.get(iteration);
                        this.drawSolution(antigenMap,antibodyMap);
                    }
                }
            }
            else if(e.getCode() == KeyCode.DOWN && isInteger){
                int iteration = Integer.valueOf(iterationTextField.getText())-1;
                if(iteration >= 0 && iteration <antibodyGenerations.size()){
                    HashMap<String,ArrayList<Antibody>> antibodyMap = antibodyGenerations.get(iteration);
                    this.drawSolution(antigenMap,antibodyMap);
                    iterationTextField.setText(Integer.toString(iteration));
                }
            }else if(e.getCode() == KeyCode.DOWN){
                int iteration = antibodyGenerations.size()-1;
                HashMap<String,ArrayList<Antibody>> antibodyMap = antibodyGenerations.get(iteration);
                this.drawSolution(antigenMap,antibodyMap);
                iterationTextField.setText(Integer.toString(iteration));
            }
            else if(e.getCode() == KeyCode.UP && isInteger){
                int iteration = Integer.valueOf(iterationTextField.getText())+1;
                if(iteration >= 0 && iteration < antibodyGenerations.size()){
                    HashMap<String,ArrayList<Antibody>> antibodyMap = antibodyGenerations.get(iteration);
                    this.drawSolution(antigenMap,antibodyMap);
                    iterationTextField.setText(Integer.toString(iteration));
                }else if(iteration == antibodyGenerations.size()){
                    iterationTextField.setText("Test");
                    this.drawSolution(antigenTestMap,antibodyTestMap);
                }
            }
        });
    }

    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public void createSolutionGraph(HashMap<String, double[][]> featureMap, HashMap<String,ArrayList<Antibody>> antibodyMap) {
        this.solutionGraph = new SolutionGraph(solutionGraphWidth, solutionGraphHeight, featureMap,antibodyMap);
        setCenter(solutionGraph);
    }

    public void drawSolution(HashMap<String, ArrayList<Antigen>> antigenMap, HashMap<String, ArrayList<Antibody>> antibodyMap){
        solutionGraph.drawSolutionGraph(antigenMap,antibodyMap);
    }

    public void setAccuracy(double accuracy) {
        solutionGraph.setAccuracy(accuracy);
    }

    public void createStatisticGraph(int iterations) {
        statisticGraph = new StatisticGraph(statisticGraphWidth, statisticGraphHeight, iterations);
        //primaryStage.setHeight(700);
        setCenter(statisticGraph);
    }

    public void addIteration(double fitness) {
        statisticGraph.addIteration(fitness);
    }

    public void setBestAccuracy(double accuracy) {
        statisticGraph.setBestAccuracy(accuracy);
    }

    public void setBestAccuracyIteration(double accuracy, int iteration) {
        solutionGraph.setBestAccuracyIteration(accuracy, iteration);
    }
}