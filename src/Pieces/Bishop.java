package Pieces;

import ChessField.ChessFieldHolder;
import ChessField.Move;
import ChessField.Position;

import java.util.ArrayList;

public class Bishop implements Piece {
    private boolean wasMoved;
    private Color color;

    public Bishop(Color color) {
        this.color = color;
        this.wasMoved = false;
    }

    @Override
    public ArrayList<Position> getValidMoves(Position position, ChessFieldHolder chessField) {
        ArrayList<Position> validMoves = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Position destination = new Position(row, col);
                if (isMoveValid(new Move(position, destination), chessField)) {
                    validMoves.add(destination);
                }
            }
        }

        return validMoves;
    }

    @Override
    public ArrayList<Position> getPathToTakePiece(Position ownPosition, Position targetPosition, ChessFieldHolder chessField) {
        if (isMoveValid(new Move(ownPosition, targetPosition), chessField)) {
            ArrayList<Position> path = new ArrayList<>();

            int ownCol = ownPosition.getCol();
            int targetCol = targetPosition.getCol();
            int ownRow = ownPosition.getRow();
            int targetRow = targetPosition.getRow();

            int distanceVertical = targetRow - ownRow;
            int distanceHorizontal = targetCol - ownCol;

            int rowfactor = distanceVertical > 0 ? 1 : -1;
            int colfactor = distanceHorizontal > 0 ? 1 : -1;

            for (int row = ownRow + rowfactor, col = ownCol + colfactor;
                 distanceVertical > 0 ? row < targetRow : row > targetRow;
                 row += rowfactor, col += colfactor) {
                path.add(new Position(row, col));
            }

            return path;
        } else {
            return null;
        }
    }

    @Override
    public boolean isMoveValid(Move move, ChessFieldHolder chessField) {
        if (move.getOldPosition().equals(move.getNewPosition())) {
            return false;
        }

        int oldCol = move.getOldPosition().getCol();
        int newCol = move.getNewPosition().getCol();
        int oldRow = move.getOldPosition().getRow();
        int newRow = move.getNewPosition().getRow();

        int distanceVertical = newRow - oldRow;
        int distanceHorizontal = newCol - oldCol;

        int rowfactor = distanceVertical > 0 ? 1 : -1;
        int colfactor = distanceHorizontal > 0 ? 1 : -1;

        if (!(distanceHorizontal == distanceVertical
                || ((distanceHorizontal * colfactor) - (distanceVertical * rowfactor)) == 0)) {
            return false;
        } else {
            for (int row = oldRow + rowfactor, col = oldCol + colfactor;
                 distanceVertical > 0 ? row < newRow : row > newRow;
                 row += rowfactor, col += colfactor) {
                if (chessField.getPiece(new Position(row, col)) != null) {
                    return false;
                }
            }
        }

        return chessField.getPiece(move.getNewPosition()) == null
                || chessField.getPiece(move.getNewPosition()).getColor() != color;
    }

    @Override
    public boolean wasMoved() {
        return wasMoved;
    }

    @Override
    public void setWasMoved(boolean wasMoved) {
        this.wasMoved = wasMoved;
    }

    @Override
    public Color getColor() {
        return color;
    }
}
