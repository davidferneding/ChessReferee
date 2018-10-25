package Pieces;

import ChessField.ChessFieldHolder;
import ChessField.Move;
import ChessField.Position;

import java.util.ArrayList;

public interface Piece {
    boolean isMoveValid(Move move, ChessFieldHolder chessField);

    ArrayList<Position> getValidMoves(Position position, ChessFieldHolder chessField); //TODO: refactor in all classes

    ArrayList<Position> getPathToTakePiece(Position ownPosition, Position targetPosition, ChessFieldHolder chessField); //TODO MAYBE: implement pathfinding

    boolean wasMoved();

    void setWasMoved(boolean wasMoved);

    Color getColor();
}
