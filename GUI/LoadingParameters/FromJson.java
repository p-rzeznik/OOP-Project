package GUI.LoadingParameters;


import Classes.RectangularMap;
import GUI.SimulationEngine;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class FromJson  {


    public static void handle() {
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject)parser.parse(new FileReader("./parameters.json"));


            long width = (long) jsonObject.get("width");
            long height = (long) jsonObject.get("height");
            long se = (long) jsonObject.get("startEnergy");
            long me = (long)jsonObject.get("moveEnergy");
            long pe = (long)jsonObject.get("plantEnergy");
            long jr = (long)jsonObject.get("jungleRatio");

            SimulationEngine engine = new SimulationEngine(new RectangularMap((int) width, (int)height, (int)se,(int) me, (int)pe, (int)jr));

            engine.run();


        } catch(Exception fe) {
            fe.printStackTrace();
        }

    }
}
