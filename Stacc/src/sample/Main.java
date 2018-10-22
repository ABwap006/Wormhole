package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    public static final int TILE_SIZE = 50;
    public static ArrayList<ArrayList<Integer>> shortestPathList;
    public static ArrayList<ArrayList<Integer>> graphAdj;
    private Group tileGroup = new Group();
    private Group labelGroup = new Group();
    public static boolean buildGraph = true;

    /**
     * Creates and shows the tiles for the board, in addition to the Shortest Path - button.
     * This method uses the Connect.getInformationFromStacc() method to use the information it recieves from
     * Stacc's API. This is also used when creating the adjacency-list for the graph.
     *
     * @return root - Pane.
     * @throws IOException
     * @throws ParseException
     */
    private Parent createContent() throws IOException, ParseException {
        Pane root = new Pane();
        int dimX = Integer.parseInt(Connect.getInformationFromStacc("dimX", ""));
        int dimY = Integer.parseInt(Connect.getInformationFromStacc("dimY", ""));
        int goal = Integer.parseInt(Connect.getInformationFromStacc("goal", ""));
        root.setPrefSize(dimX * TILE_SIZE + 150, (dimY * TILE_SIZE));
        root.getChildren().addAll(tileGroup, labelGroup);

        for (int i = 0; i < dimX * dimY; i++) {
            int posX = Integer.parseInt(Connect.getInformationFromStacc("posX", "/" + String.valueOf(i + 1))) - 1;
            int posY = Integer.parseInt(Connect.getInformationFromStacc("posY", "/" + String.valueOf(i + 1))) - 1;
            Tile tile = new Tile(Color.BLANCHEDALMOND, i);
            tile.setTranslateX(posX * TILE_SIZE);
            tile.setTranslateY((TILE_SIZE * (dimY - 1)) - (posY * TILE_SIZE));
            tile.setStrokeWidth(2);
            tile.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        for (int k = 0; k < shortestPathList.get(tile.index).size(); k++) {
                            Label l = (Label) labelGroup.getChildren().get(shortestPathList.get(tile.index).get(k));
                            Tile t = (Tile) tileGroup.getChildren().get(shortestPathList.get(tile.index).get(k));
                            l.setText(String.valueOf(k + 1));
                            t.setFill(Color.GREEN);

                        }
                        Text txt = new Text("Minimun amount of\nrolls needed is: " +
                                minNumberOfDiceRolls(shortestPathList.get(tile.index), goal));
                        txt.setTranslateX(TILE_SIZE * dimX + 20);
                        txt.setTranslateY(TILE_SIZE * 5);
                        labelGroup.getChildren().addAll(txt);
                    } catch (NullPointerException e) {

                    }
                }
            });

            tile.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    try {
                        for (int k = 0; k < shortestPathList.get(tile.index).size(); k++) {
                            Label l = (Label) labelGroup.getChildren().get(shortestPathList.get(tile.index).get(k));
                            Tile t = (Tile) tileGroup.getChildren().get(shortestPathList.get(tile.index).get(k));
                            l.setText(String.valueOf(shortestPathList.get(tile.index).get(k) + 1));
                            t.setFill(Color.BLANCHEDALMOND);
                        }
                        labelGroup.getChildren().remove(labelGroup.getChildren().size() - 1);
                    } catch (NullPointerException e) {

                    }
                }
            });

            Label lab = new Label(String.valueOf(i + 1));
            lab.setTranslateX((posX * TILE_SIZE) + 17);
            lab.setTranslateY((TILE_SIZE * (dimY - 1)) - (posY * TILE_SIZE));
            lab.addEventHandler(MouseEvent.ANY, e -> tile.getEventDispatcher());

            labelGroup.getChildren().addAll(lab);
            tileGroup.getChildren().addAll(tile);
        }

        Text explain = new Text("After you have\nclicked the button,\nyou can click on\n" +
                "a square to find\nits shortest path.");
        explain.setTranslateX(TILE_SIZE * dimX + 20);
        explain.setTranslateY(TILE_SIZE * 3);
        labelGroup.getChildren().addAll(explain);
        Button graphBtn = new Button("Shortest Paths");
        graphBtn.setTranslateX(TILE_SIZE * dimX + 20);
        graphBtn.setTranslateY(TILE_SIZE);
        graphBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (buildGraph) {
                    buildGraph = false;
                    try {
                        graphAdj = Connect.buildGraph();
                        shortestPathList = new ArrayList<>();
                        for (int i = 0; i < dimX * dimY; i++) {
                            shortestPathList.add(ShortesPaths.bfsShortest(i, goal, graphAdj));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        tileGroup.getChildren().add(graphBtn);
        return root;
    }

    private int minNumberOfDiceRolls(ArrayList<Integer> path, int goal) {
        int rolls = 0;
        int rollsNormalMoves = 0;
        int moves = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            if (Math.abs(path.get(i) - path.get(i + 1)) > 1) {
                rolls += (int) Math.ceil(moves / 6.0);
                moves = 0;
            }
            else
                moves++;
        }
        rolls += (int) Math.ceil(moves / 6.0);
        moves = 0;
        int start = path.get(0);
        if (start < goal) {
            for (int i = start; i < goal; i++)
                moves++;
            rollsNormalMoves = (int) Math.ceil(moves / 6.0);
        } else {
            rollsNormalMoves = Integer.MAX_VALUE;
        }


        return (rolls <= rollsNormalMoves) ? rolls : rollsNormalMoves;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(createContent(), Color.CORAL);
        primaryStage.setTitle("Wormhole");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
