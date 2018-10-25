package ChessField;

import Pieces.Color;

public interface ChessFieldViewRepresentation {
    void setCurrentMoveColor(Color currentMoveColor);

    void drawField(ChessFieldHolder chessField, Move move, boolean withAnimation);

    Move requestMoveFromHumanPlayer(String playerName);
}
