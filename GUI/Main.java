package GUI;

import GUI.LoadingParameters.FromDialogBox;
import GUI.LoadingParameters.FromJson;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application  {


    private Stage primaryStage;


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        primaryStage = stage;

        Label head = new Label("Choose number of maps");
        head.setStyle("-fx-font-weight: bold");

        ChoiceBox<Integer> numberOfMaps = new ChoiceBox<>();
        for (int i = 1; i<10; i++){
            numberOfMaps.getItems().add(i);
        }


        Button accept = new Button();
        accept.setText("Accept");
        accept.setOnAction(e->{
            primaryStage.close();
            for(int i = 0; i<numberOfMaps.getValue() ;i++){
                showChoiceStage();
            }
        });

        GridPane layout = new GridPane();
        layout.setPadding(new Insets(10,10,10,10));
        layout.add(head,0,0);
        layout.addRow(1, numberOfMaps,accept);

        primaryStage.setScene(new Scene(layout, 350,250));
        primaryStage.show();
    }

    public void showChoiceStage(){
        Stage window = new Stage();

        window.setTitle("World Simulation");

        Label head = new Label("Choose method to load parameters");
        head.setStyle("-fx-font-weight: bold");

        Button fromJson = new Button();
        fromJson.setText("Load parameters from json");
        fromJson.setOnAction(e ->{
            FromJson.handle();
            window.close();

        });

        Button fromDialogBox = new Button();
        fromDialogBox.setText("Set parameters manually");
        fromDialogBox.setOnAction(e->{
            FromDialogBox.handle();
            window.close();
        });


        BorderPane border = new BorderPane();


        VBox top = new VBox();
        top.setAlignment(Pos.CENTER);
        top.getChildren().add(head);


        HBox center = new HBox();
        center.setAlignment(Pos.CENTER);
        center.getChildren().add(fromJson);
        center.getChildren().add(fromDialogBox);


        border.setTop(top);
        border.setCenter(center);


        Scene scene = new Scene(border, 350, 100);
        window.setScene(scene);


        window.show();
    }

}
