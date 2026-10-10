package com.obscaeris;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
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

    public void initRoot(Group root) {
        String adjString = "s0:s1,s2;s1:s0,s2;s2:s0,s1;s3:;";
        Map<String, List<String>> adjList = Graph.adjacencyListFromString(adjString);
        Map<String, Graph> graphs = Graph.fromAdjacencyList(adjList);

        graphs.get("s0").drawGraph(root);
        graphs.get("s3").drawGraph(root);
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