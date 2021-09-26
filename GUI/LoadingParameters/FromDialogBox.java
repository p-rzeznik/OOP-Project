package GUI.LoadingParameters;



import Classes.RectangularMap;
import GUI.SimulationEngine;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class FromDialogBox  {

    private static GridPane layout;

    public static void handle() {
        Stage DialogBox = new Stage();

        DialogBox.initModality(Modality.APPLICATION_MODAL);
        DialogBox.setTitle("Input data");

        layout = new GridPane();
        layout.setPadding(new Insets(10,10,10,10));
        layout.setVgap(8);
        layout.setHgap(10);


        Label head = new Label("Input data:");
        head.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(head, 0,0);

        Label width = new Label("Map width:");
        GridPane.setConstraints(width, 0,1);
        TextField w = new TextField();
        GridPane.setConstraints(w, 1,1);

        Label height = new Label("Map height:");
        GridPane.setConstraints(height, 0,3);
        TextField h = new TextField();
        GridPane.setConstraints(h, 1,3);

        Label startEnergy = new Label("Animal start energy:");
        GridPane.setConstraints(startEnergy, 0,5);
        TextField se = new TextField();
        GridPane.setConstraints(se, 1,5);

        Label moveEnergy = new Label("Animal move energy:");
        GridPane.setConstraints(moveEnergy, 0,7);
        TextField me = new TextField();
        GridPane.setConstraints(me, 1,7);

        Label plantEnergy = new Label("Plant energy:");
        GridPane.setConstraints(plantEnergy, 0,9);
        TextField pe = new TextField();
        GridPane.setConstraints(pe, 1,9);

        Label jungleRatio = new Label("Jungle to savanna ratio\n(side attitude in %):");
        GridPane.setConstraints(jungleRatio, 0,11);
        TextField jr = new TextField();
        GridPane.setConstraints(jr, 1,11);

        Label errw =  errLabel(2);

        Label errh = errLabel(4);

        Label errse =  errLabel(6);

        Label errme = errLabel(8);

        Label errpe = errLabel(10);

        Label errjr = errLabel(12);

        Button continueButton = new Button();
        GridPane.setConstraints(continueButton, 1,13);
        continueButton.setText("Continue");

        continueButton.setOnAction(e-> {
            boolean valid = true;

            valid = validate(w, errw, valid);
            valid = validate(h, errh, valid);
            valid = validate(se, errse, valid);
            valid = validate(me, errme, valid);
            valid = validate(pe, errpe, valid);
            valid = validate(jr, errjr, valid);

            if (valid){
                SimulationEngine engine = new SimulationEngine(new RectangularMap(toInt(w),toInt(h), toInt(se), toInt(me), toInt(pe), toInt(jr)));
                DialogBox.close();
                engine.run();
            }

        });

        layout.getChildren().addAll(head, width,w,height,h,startEnergy,se,moveEnergy,me, plantEnergy,pe, jungleRatio,jr, continueButton);


        layout.setAlignment(Pos.CENTER);


        Scene mainScene = new Scene(layout, 350, 450);

        DialogBox.setScene(mainScene);
        DialogBox.showAndWait();

    }

    private static int toInt(TextField input){
        return Integer.parseInt(input.getText());
    }


    private static boolean isPositiveInt(TextField input){
        try{
            int number = Integer.parseInt(input.getText());
            return number > 0;
        }
        catch (NumberFormatException e){
            return false;
        }
    }

    private static Label errLabel(int pos){
        Label err = new Label("Input is not positive int");
        err.setTextFill(Color.web("#ff0000"));
        GridPane.setConstraints(err, 1,pos);
        return err;
    }

    private static boolean validate(TextField input, Label err, boolean valid){
        if(! isPositiveInt(input)){
            if(! layout.getChildren().contains(err)){
                layout.getChildren().add(err);
            }
            return false;
        }else{
            layout.getChildren().remove(err);
            return valid;
        }
    }
}
