package GUI;


import Classes.*;
import EnumClasses.MapDirection;
import Interfaces.IEngine;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONObject;


import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.TreeMap;

public class SimulationEngine implements IEngine {


    private final RectangularMap map;

    private GridPane mapVis;
    private GridPane left;
    private final int fieldSize = 10;
    private int lastItem = 0;
    private Timeline timeline;
    private boolean active = false;
    private int numberOfEpochsForAnimalStatistics = -1;

    private int epoch = 0;
    private int totalAnimalCountAfterNEpochs = 0;
    private int avgAliveAnimalCountAfterNEpochs = 0;
    private int avgDeadAnimalCountAfterNEpochs = 0;
    private int avgAverageLifeLengthAfterNEpochs = 0;
    private int avgAverageEnergyLevelAfterNEpochs = 0;
    private int avgGrassCountAfterNEpochs = 0;
    private int actualDominatingGene;
    private int avgAverageChildrenCountAfterNEpochs = 0;
    private Animal actualAnimal = null;
    private int actualAnimalDeathEpoch =-1;

    public SimulationEngine(RectangularMap map) {
        this.map = map;

    }

    @Override
    public void run() {
        Stage Simulation = new Stage();
        Simulation.setTitle("World Simulation");
        BorderPane layout = new BorderPane();
        mapVis = new GridPane();
        layout.setRight(mapVis);

        left = new GridPane();
        left.setAlignment(Pos.CENTER);
        left.setHgap(8);
        left.setVgap(10);
        left.setPadding(new Insets(10,10,10,10));


        Button StopStart = new Button("Stop/Start");
        StopStart.setOnAction(e->{
            if(active){
                active = false;
                timeline.stop();
            }
            else{
                active = true;
                timeline.play();
            }
        });
        left.add(StopStart,5,5);

        Button Restart = new Button("Restart");
        Restart.setOnAction(e->{
            SimulationEngine engine = new SimulationEngine( new RectangularMap(map.width,  map.height, map.startEnergy, map.moveEnergy, map.plantEnergy, map.jungleRatio));
            engine.run();
            Simulation.close();
            timeline.stop();
        });
        left.add(Restart,6,5);
        Button saveStatisticsToFile = new Button("Save statistics to file");
        saveStatisticsToFile.setOnAction(e->{
            writeStatisticsToFile();
        });
        left.add(saveStatisticsToFile,5,6);


        layout.setLeft(left);
        mapVis.setPadding(new Insets(10,10,10,10));
        mapVis.setVgap(0);
        mapVis.setHgap(0);

        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {
                Rectangle rect = new Rectangle(fieldSize, fieldSize);
                rect.setFill(Color.LIGHTGREEN);
                mapVis.add(rect, x, y);
                lastItem++;
            }
        }

        for(int y = map.jungleBottomY; y<map.jungleTopY; y++){
            for(int x = map.jungleLeftX; x< map.jungleRightX; x++){
                Rectangle rect = new Rectangle(fieldSize, fieldSize);
                rect.setFill(Color.GREEN);
                mapVis.add(rect, x, y);
                lastItem++;
            }
        }


        int animalsOnStart = 20;
        for(int l = 0; l< animalsOnStart; l++) {
            Vector2d v1 = new Vector2d((int) (Math.random() * map.width), (int) (Math.random() * map.height));
            while(map.isOccupiedByAnimal(v1)){
                v1 = new Vector2d((int) (Math.random() * map.width), (int) (Math.random() * map.height));
            }
            Animal animal1 = new Animal(v1, v1, map, map.startEnergy, new Genes(Genes.generateGenes()), MapDirection.generateDirection());
            map.placeAnimal(animal1, v1);
        }

        int grassOnStart = 2;
        for(int m = 0; m< grassOnStart; m++){
            Vector2d v1 = new Vector2d((int) (Math.random() * map.width), (int) (Math.random() * map.height));
            while(map.isOccupiedByAnimal(v1) || map.isOccupiedByAnimal(v1)){
                v1 = new Vector2d((int) (Math.random() * map.width), (int) (Math.random() * map.height));
            }
            Grass grass = new Grass(v1, map);
            map.placeGrass(grass, v1);
        }


        int delay = 100;
        timeline = new Timeline(new KeyFrame(
                Duration.millis(delay),
                ae -> existence()
        ));

        timeline.setCycleCount(Timeline.INDEFINITE);

        Scene scene =new Scene(layout, map.width*10+500,map.height*11);
        Simulation.setScene(scene);
        Simulation.show();



    }
    private void existence(){
        if(! map.isAnyAlive() ){
            timeline.stop();
        }
        epoch++;
        showStatistics();
        visualise();
        map.removeDeadAnimals();
        map.move();
        map.eating();
        map.copulation();
        map.addGrass();
        monitorSingleAnimalStatistics();


    }

    public void visualise(){
        for(int i = mapVis.getChildren().size()-1; i>= this.lastItem; i--){
            mapVis.getChildren().remove(i);
        }

        for (TreeMap<Integer, LinkedList<Animal>> group : map.animals.values()) {
            for (LinkedList<Animal> animalsWithSameEnergy : group.values()) {
                for (Animal animal : animalsWithSameEnergy) {
                    if (animal.isAlive()) {
                        Circle circle = new Circle(animal.getPosition().x, animal.getPosition().y, fieldSize / 2);
                        circle.setFill(Color.BLACK);
                        Tooltip info = new Tooltip();
                        info.setText("Position:("+animal.getPosition().x+","+animal.getPosition().y+")\nEnergy:"+ animal.getEnergy()+ "\nLife length: "+animal.lifeLength +"\nDirection:"+ animal.getDirection() + "\nGenes: " + animal.getGenes().genes);
                        Tooltip.install(circle,info);
                        circle.setOnMouseClicked(e->{
                            actualAnimal =animal;
                            getAnimalsStatistics();
                        });
                        mapVis.add(circle, (animal.getPosition().x+map.width)%map.width, (map.height-animal.getPosition().y-1 + map.height)%map.height);
                    }
                }
            }
        }
        for(Grass grass : map.plants.values()){
            Circle circle = new Circle(fieldSize / 2);
            circle.setFill(Color.GREENYELLOW);
            Tooltip info = new Tooltip();
            info.setText("Position:("+grass.getPosition().x+","+grass.getPosition().y+")\nEnergy:"+ map.plantEnergy);
            Tooltip.install(circle,info);
            mapVis.add(circle, (grass.getPosition().x+map.width)%map.width, (map.height-grass.getPosition().y-1+map.height)%map.height);
        }

    }


    private void showStatistics(){
        for(int i = left.getChildren().size()-1; i>= 3; i--){
            left.getChildren().remove(i);
        }
        Label epochLabel = new Label();
        epochLabel.setText("Epoch: " + epoch);
        left.add(epochLabel,0,0);

        totalAnimalCountAfterNEpochs = 0;
        int aliveAnimalCount = 0;
        int deadAnimalCount = 0;
        int averageLifeLength = 0;
        int averageEnergyLevel = 0;
        int[] totalGenesCount = new int[8];
        int averageChildrenCount = 0;

        for (TreeMap<Integer, LinkedList<Animal>> group : map.animals.values()) {
            for (LinkedList<Animal> animalsWithSameEnergy : group.values()) {
                for (Animal animal : animalsWithSameEnergy) {
                    if (animal.isAlive()) {
                        aliveAnimalCount++;
                        averageEnergyLevel+=animal.getEnergy();
                        averageChildrenCount += animal.children.size();
                        for(Integer i : animal.getGenes().genes){
                            totalGenesCount[i]++;
                        }

                    }else {
                        averageLifeLength+=animal.lifeLength;
                        deadAnimalCount++;
                    }
                    totalAnimalCountAfterNEpochs++;

                }
            }
        }
        int max = 0;
        for(int i = 0; i<8; i++){
            if(max< totalGenesCount[i]){
                max=totalGenesCount[i];
                actualDominatingGene = i;
            }
        }


        Label animalCount = new Label("Total animal count: "+ totalAnimalCountAfterNEpochs);
        left.add(animalCount, 0,1);


        Label aliveAnimals = new Label("Alive animal count: " + aliveAnimalCount);
        left.add(aliveAnimals, 0,2);


        Label deadAnimals = new Label("Dead animals count: "+ deadAnimalCount);
        left.add(deadAnimals, 0, 3);


        Label avgLifeLength = new Label();
        if(deadAnimalCount!=0){
            averageLifeLength = averageLifeLength/deadAnimalCount;
            avgLifeLength.setText("Average life length: "+ averageLifeLength);
        }
        else{
            avgLifeLength.setText("Average life length: --");
        }

        left.add(avgLifeLength, 0, 4);


        Label avgEnergyLevel = new Label();
        if(aliveAnimalCount!=0){
            averageEnergyLevel = averageEnergyLevel/aliveAnimalCount;
            avgEnergyLevel.setText("Average energy level: "+ averageEnergyLevel);
        }
        else{
            avgEnergyLevel.setText("Average energy level: --");
        }
        left.add(avgEnergyLevel, 0, 5);


        Label dominatingGene = new Label("Actual dominating gene: "+ actualDominatingGene);
        left.add(dominatingGene,0,6);


        Label avgChildrenCount = new Label();
        if(aliveAnimalCount!=0){
            averageChildrenCount = averageChildrenCount/aliveAnimalCount;
            avgChildrenCount.setText("Average children count: "+ averageChildrenCount);
        }
        else{
            avgChildrenCount.setText("Average children count:  --");
        }
        left.add(avgChildrenCount,0,7);

        Label totalGrass = new Label("Total grass count: "+ map.plants.size());
        left.add(totalGrass, 0, 8);

        if(epoch>0) {
            avgAliveAnimalCountAfterNEpochs = ((epoch - 1) * avgAliveAnimalCountAfterNEpochs + aliveAnimalCount) / epoch;
            avgDeadAnimalCountAfterNEpochs = ((epoch - 1) * avgDeadAnimalCountAfterNEpochs + deadAnimalCount) / epoch;
            avgAverageLifeLengthAfterNEpochs = ((epoch - 1) * avgAverageLifeLengthAfterNEpochs + averageLifeLength)/epoch;
            avgAverageEnergyLevelAfterNEpochs = ((epoch - 1) * avgAliveAnimalCountAfterNEpochs + averageEnergyLevel)/epoch;
            avgGrassCountAfterNEpochs = ((epoch - 1) * avgGrassCountAfterNEpochs + map.plants.size()) / epoch;
            avgAverageChildrenCountAfterNEpochs = ((epoch - 1) * avgAverageChildrenCountAfterNEpochs + averageChildrenCount) / epoch;
        }
    }

    private void writeStatisticsToFile(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("TotalAnimalCount", totalAnimalCountAfterNEpochs);
        jsonObject.put("AverageAliveAnimalCount", avgAliveAnimalCountAfterNEpochs);
        jsonObject.put("AverageDeadAnimalCount", avgDeadAnimalCountAfterNEpochs);
        jsonObject.put("AverageOfAverageLifeLength", avgAverageLifeLengthAfterNEpochs);
        jsonObject.put("AverageOfAverageEnergyLevel", avgAverageEnergyLevelAfterNEpochs);
        jsonObject.put("ActualDominatingGene", actualDominatingGene);
        jsonObject.put("AverageOfAverageChildrenCount", avgAliveAnimalCountAfterNEpochs);

        try {
            FileWriter file = new FileWriter("./output.json");
            file.write(jsonObject.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getAnimalsStatistics(){
        Stage DialogBox = new Stage();

        DialogBox.initModality(Modality.APPLICATION_MODAL);
        DialogBox.setTitle("Input data");

        GridPane dBLayout = new GridPane();
        dBLayout.setPadding(new Insets(10,10,10,10));
        dBLayout.setVgap(8);
        dBLayout.setHgap(10);



        Label epochs = new Label("Number of epochs: ");
        dBLayout.add(epochs, 1,0);
        TextField input = new TextField();
        dBLayout.add(input, 1,1);

        Label err = new Label("Input is not positive int");
        err.setTextFill(Color.web("#ff0000"));


        Button continueButton = new Button();
        dBLayout.add(continueButton, 3,1);
        continueButton.setText("Continue");

        continueButton.setOnAction(e-> {


            if (validate(input, err, dBLayout )){
                numberOfEpochsForAnimalStatistics = toInt(input);
                DialogBox.close();
                timeline.play();
            }

        });



        dBLayout.setAlignment(Pos.CENTER);

        DialogBox.setScene(new Scene(dBLayout, 300, 200));
        DialogBox.showAndWait();

    }
    private int toInt (TextField input){
        return Integer.parseInt(input.getText());
    }

    private boolean isPositiveInt(TextField input){
        try{
            int number = Integer.parseInt(input.getText());
            return number > 0;
        }
        catch (NumberFormatException e){
            return false;
        }
    }


    private boolean validate(TextField input, Label err, GridPane dBLayout){
        if(! isPositiveInt(input)){
            if(! dBLayout.getChildren().contains(err)){
                dBLayout.add(err,1,1);
            }
            return false;
        }else{
            dBLayout.getChildren().remove(err);
            return true;
        }
    }
    private void monitorSingleAnimalStatistics(){
        if( numberOfEpochsForAnimalStatistics != -1){
            numberOfEpochsForAnimalStatistics--;

            if(actualAnimalDeathEpoch ==-1){
                if(! actualAnimal.isAlive()){
                    actualAnimalDeathEpoch = epoch-1;
                }
            }
            if(numberOfEpochsForAnimalStatistics==0){
                timeline.stop();
                showSingleAnimalStatistics();
                for (TreeMap<Integer, LinkedList<Animal>> group : map.animals.values()) {
                    for (LinkedList<Animal> animalsWithSameEnergy : group.values()) {
                        for (Animal animal : animalsWithSameEnergy) {
                            animal.visited = false;
                        }
                    }
                }
                actualAnimal = null;
                actualAnimalDeathEpoch = -1;
                numberOfEpochsForAnimalStatistics = -1;
            }
        }
    }
    private void showSingleAnimalStatistics(){
        Stage Window = new Stage();

        Window.initModality(Modality.APPLICATION_MODAL);
        Window.setTitle("Input data");

        GridPane WindowLayout = new GridPane();
        WindowLayout.setPadding(new Insets(10,10,10,10));
        WindowLayout.setVgap(8);
        WindowLayout.setHgap(10);


        Label deathEpoch = new Label();
        if( actualAnimalDeathEpoch==-1){
            deathEpoch.setText("Animal is alive");
        }else{
            deathEpoch.setText("Death epoch:" + actualAnimalDeathEpoch);
        }
        WindowLayout.add(deathEpoch, 0,0);

        Label children = new Label("Number of children: "+ actualAnimal.children.size());
        WindowLayout.add(children, 0,1);

        Label descendants = new Label("Number of descendants: "+ actualAnimal.getDescendants());
        WindowLayout.add(descendants, 0,2);

        Button continueButton = new Button();
        WindowLayout.add(continueButton, 0,3);
        continueButton.setText("Continue");

        continueButton.setOnAction(e-> {
            Window.close();
        });

        WindowLayout.setAlignment(Pos.CENTER);


        Window.setScene(new Scene(WindowLayout, 350, 250));
        Window.show();


    }

}

