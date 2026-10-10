package com.obscaeris;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.converter.PaintConverter;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.Light.Point;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class Graph {
    public String name;
    public Color color = Color.WHITE;
    public GraphData data = new GraphData();
    public Point2D pos = null;
    public Graph parent = null;
    private Point2D origin = null;
    private Text innerText = null;
    private Circle innerCircle = null;
    private ArrayList<Graph> children = new ArrayList<Graph>();

    public static double circleRadius = 20.0f;

    public Graph(String name, Point2D origin) {
        this.name = name;
        this.origin = origin;
    }

    public Graph(String name, Point2D origin, Color color) {
        this.name = name;
        this.origin = origin;
        this.color = color;
    }

    public Graph(String name, double originX, double originY) {
        this.name = name;
        this.origin = new Point2D(originX, originY);
    }

    public Graph(String name, double originX, double originY, Color color) {
        this.name = name;
        this.origin = new Point2D(originX, originY);
        this.color = color;
    }

    public ArrayList<Graph> getChildren() {
        return this.children;
    }

    public void addChild(Graph g) {
        g.parent = this;
        this.children.add(g);
    }

    public void removeChild(Graph g) {
        if (this.children.contains(g)) {
            g.parent = null;
            this.children.remove(g);
        }
    }

    public Group getShape() {
        Group container = new Group();
        container.setLayoutX(origin.getX());
        container.setLayoutY(origin.getY());
        ObservableList<Node> children = container.getChildren();

        innerCircle = new Circle(Graph.circleRadius);
        innerCircle.setFill(this.color);
        innerCircle.setStroke(Color.BLACK);
        double offX = this.name.length() * 1.5;
        double offY = -4;
        innerText = new Text(innerCircle.getCenterX() - Graph.circleRadius / 2 + offX,
                innerCircle.getCenterY() + Graph.circleRadius / 2 + offY, this.name);
        pos = new Point2D(innerText.getX(), innerText.getY());

        children.add(innerCircle);
        children.add(innerText);
        return container;
    }

    public void printChildren(boolean newLine) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (Graph child : this.children) {
            sb.append(child.toString());
        }
        sb.append(")");
        System.out.print(sb.toString());
        if (newLine)
            System.out.print("\n");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String x = BigDecimal.valueOf(origin.getX()).setScale(2, RoundingMode.UP).toString();
        String y = BigDecimal.valueOf(origin.getY()).setScale(2, RoundingMode.UP).toString();
        sb.append(this.name);
        sb.append("[").append(this.hashCode()).append("]");
        sb.append(": (").append(x).append(",").append(y).append(")");
        return sb.toString();
    }

    public void drawGraph(Group root) {
        this.drawGraph(root, false);
    }

    public void drawGraph(Group root, Point2D startOrigin) {
        drawGraph(root, false, startOrigin);
    }

    public void drawGraph(Group root, boolean debug) {
        this.drawGraph(root, debug, null);
    }

    public void drawGraph(Group root, boolean debug, Point2D startOrigin) {
        this._drawGraph(root, null, null, -1, debug, startOrigin);
    }

    private void _drawGraph(Group root, List<Graph> exploredGraphs, Map<Graph, List<Graph>> links, int counter,
            boolean debug, Point2D startOrigin) {
        if(startOrigin != null && exploredGraphs == null && links == null) this.origin = startOrigin;
        if (exploredGraphs == null)
            exploredGraphs = new ArrayList<Graph>();
        if (links == null)
            links = new HashMap<Graph, List<Graph>>();
        if (counter == -1)
            counter = 0;
        List<Graph> currentLinks = links.get(this);
        if (currentLinks == null)
            links.put(this, new ArrayList<Graph>());

        int childCounter = 0;
        exploredGraphs.add(this);
        root.getChildren().add(this.getShape());

        if (debug)
            System.out.printf("%s begins exploring...\n", this.name);
        for (Graph child : this.children) {
            if (exploredGraphs.contains(child)) {
                if (links.get(this).contains(child) || links.get(child).contains(this)) {
                    // Already explored and linked, no actions to be taken.
                    if (debug)
                        System.out.printf("%s of %s explored and linked, skipping...\n", child.name, this.name);
                } else {
                    // Needs to be linked
                    if (debug)
                        System.out.printf("Linking %s to %s...\n", this.name, child.name);
                    Graph.drawLink(root, this, child, Color.RED, debug);
                }
            } else {
                childCounter++;
                counter++;
                int sign = counter % 2 == 0 ? -1 : 1;
                double offX = sign * ((circleRadius * 2 * childCounter) + 0);
                double offY = 1 * ((circleRadius * 2 * childCounter) + 0);
                child.origin = this.origin.add(offX, offY);
                if (debug)
                    System.out.printf("%s gave origin to %s\n", this, child);
                child._drawGraph(root, exploredGraphs, links, counter, debug, startOrigin);
            }
        }

        if (debug)
            System.out.printf("%s exiting...\n", this);
    }

    public static Graph createGraph(Graph root, List<Graph> children) {
        root.children.clear();
        for (Graph c : children) {
            c.parent = root;
            root.children.add(c);
        }
        return root;
    }

    public static void drawLink(Group root, Graph g0, Graph g1) {
        Graph.drawLink(root, g0, g1, Color.BLACK);
    }

    public static void drawLink(Group root, Graph g0, Graph g1, Color color) {
        Graph.drawLink(root, g0, g1, color, false);
    }

    public static void drawLink(Group root, Graph g0, Graph g1, Color color, boolean debug) {
        Point2D s = g0.origin;
        Point2D e = g1.origin;
        Line l = new Line(s.getX(), s.getY(), e.getX(), e.getY());
        l.setStroke(color);
        l.setStrokeWidth(2.0);
        if (debug)
            System.out.printf("Created line: %s\n", l);
        root.getChildren().add(l);
        l.toBack();
    }

    /**
     * 
     * Expects a list with the following format:
     * <br />
     * <code>String list = "s0:s1,s2;s1:s2;s2:;";</code>
     * <br />
     * The list is checked against the following regex:
     * <code>"^(\w+:\w+(,\w+)*;)*$"</code>
     * 
     * @param list The list in String form.
     * @return a Map where the keys are nodes and the values a list of their
     *         adjacent nodes.
     */
    public static Map<String, List<String>> adjacencyListFromString(String list) {
        Pattern rgx = Pattern.compile("^(\\w+:\\w*(,\\w+)*;)*$", Pattern.CASE_INSENSITIVE);
        if (!rgx.matcher(list).matches())
            return null;

        Map<String, List<String>> finalDict = new HashMap<String, List<String>>();
        String[] nodeLists = list.split(";");
        for (String nL : nodeLists) {
            String[] parts = nL.split(":");
            String node = parts[0];
            ArrayList<String> adjNodeList = new ArrayList<String>();

            if (parts.length == 2 && parts[1].contains(",")) {
                for (String adjN : parts[1].split(",")) {
                    adjNodeList.add(adjN);
                }
            }
            finalDict.put(node, adjNodeList);
        }
        return finalDict;
    }

    /**
     * Creates a graph from an adjacency list.
     * 
     * @param list
     * @return A HashMap of all the graphs created.
     */
    public static Map<String, Graph> fromAdjacencyList(Map<String, List<String>> list) {
        HashMap<String, Graph> graphDict = new HashMap<String, Graph>();

        for (String sNode : list.keySet()) {
            Graph sG = new Graph(sNode, 0, 0);
            graphDict.put(sNode, sG);
        }

        for (String sNode : list.keySet()) {
            Graph parentGraph = graphDict.get(sNode);
            for (String sChild : list.get(sNode)) {
                Graph childGraph = graphDict.get(sChild);
                parentGraph.addChild(childGraph);
            }
        }

        return graphDict;
    }
}
