package com.obscaeris;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {

        float winH = 640;
        float winW = 480;
        Group root = new Group();
        root.setTranslateY(winW / 2);
        root.setTranslateX(winW / 2);

        initRoot(root);

        scene = new Scene(root, winW, winH);
        stage.setScene(scene);
        stage.show();
    }

    public String genInterconnectedGraph(String[] nodes){
        StringBuilder sb = new StringBuilder();

        for(String n : nodes) {
            ArrayList<String> copy = new ArrayList<String>(Arrays.asList(nodes));
            copy.removeIf(arg0 -> arg0 == n);
            String s = Graph.genAdjString(n, copy.toArray(new String[]{}));
            sb.append(s);
        }
        return sb.toString();
    }

    public void initRoot(Group root) {

        String adjString = Graph.genAdjString("s0", new String[]{"s1", "s2"});
        adjString += Graph.genAdjString("s1", new String[]{"s0", "s2"});
        adjString += Graph.genAdjString("s2", new String[]{"s0", "s1"});

        // ArrayList<String> nodes = new ArrayList<String>(Arrays.asList("s0", "s1", "s2", "s3", "s4", "s5"));
        // adjString = genInterconnectedGraph(nodes.toArray(new String[]{}));
        Map<String, List<String>> adjList = Graph.adjacencyListFromString(adjString);
        if (adjList != null) {
            Map<String, Graph> graphs = Graph.fromAdjacencyList(adjList);
            graphs.get("s0").drawGraph(root);
        } else {
            System.err.println("Failed to create Graph, verify your adjencyString is correctly formatted !");
        }

        // root.getChildren().add(graphs.get("s3").drawGraph(null));
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }

}