package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle{
    public boolean highlight;
    public int index;

    public Tile(Color c, int i) {
        this.highlight = false;
        this.index = i;
        setWidth(Main.TILE_SIZE);
        setHeight(Main.TILE_SIZE);
        setFill(c);
        setStroke(Color.BLACK);
    }

    public void changeHighlight() {
        this.highlight = (this.highlight) ?  false : true;
    }

    public boolean getHighlight() {
        return this.highlight;
    }


}
