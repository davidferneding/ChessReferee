package Player;

import ChessField.ChessFieldHelper;
import ChessField.ChessFieldViewRepresentation;
import ChessField.Move;
import Pieces.Color;

public abstract class Player {
    public Color color;
    ChessFieldViewRepresentation viewRepresentation;
    String displayElement;

    public void initialize(ChessFieldViewRepresentation viewRepresentation, String displayElement, Color color) {
        this.viewRepresentation = viewRepresentation;
        this.displayElement = displayElement;
        this.color = color;
    }

    public abstract void getNextMove(ChessFieldHelper chessFieldHelper, Move lastMove);

    public String getDisplayElement() {
        return displayElement;
    }

    public void setDisplayElement(String displayElement) {
        this.displayElement = displayElement;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public abstract boolean isHuman();
}
