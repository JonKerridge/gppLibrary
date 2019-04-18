package gppLibrary.gppVis;

import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;

/**
 * Author: Scott Hendrie
 * ProcessMap is a data structure for storing/organizing the processes which appear in a network.
 */

public class ProcessMap extends HashMap<String, Process>{

    //add a single process.
    public Process populateMap(String name) {
        Process process = new Process(name);
        put(name, process);
        return process;
    }

    //create the processes of the group of pipelines.
    public VBox addGoP(int groups, String... stages) {
        VBox vb = new VBox();
        for (int i = 0; i < groups; i++){
            HBox hb = new HBox();
            for(String phase: stages){
                //create a process in the pipeline and add it to a hbox
                hb.getChildren().add(populateMap(i + ", " + phase));
                //put a border around the pipeline
                hb.setStyle(
                        //"-fx-border-style: solid inside;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-insets: 1;" +
                        "-fx-border-color: black;");
            }
            //add the complete pipeline to the vbox
            vb.getChildren().add(hb);
            vb.setAlignment(Pos.CENTER);
        }
        return vb;
    }

    //create workers.
    public VBox addWorkers(int numOfWorkers, String name) {
        VBox vb = new VBox();
        for (int i = 0; i < numOfWorkers; i++){
            //add a process to the vbox and to the process map
            vb.getChildren().add(populateMap(i + ", " + name));
        }
        return vb;
    }
}
