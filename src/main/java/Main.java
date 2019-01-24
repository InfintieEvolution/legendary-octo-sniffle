import AIS.AIS;
import AIS.Antigen;
import GUI.Graph;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {


    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("legendary-octo-sniffle");


        DataSet dataSet = new DataSet("./DataSets/iris.data");

        AIS ais = new AIS(dataSet.antigens,dataSet.antigenMap,10, 0.1, 5);
        for(int i=0;i<100;i++){
            ais.iterate();
        }
        /*for(Antigen antigen:dataSet.antigens){
            System.out.println(antigen);
        }*/

        Graph graph = new Graph(800, 500, 0, 0, 7, 7);
        BorderPane.setAlignment(graph, Pos.CENTER);
        graph.setAntigens(dataSet.antigens);
//        graph.setAntibodies(ais.getAntibodies());
        borderPane.setCenter(graph);

        primaryStage.show();
    }
}
