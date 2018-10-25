package Pieces;

import ChessField.ChessFieldHolder;
import ChessField.Move;
import ChessField.Position;

import java.util.ArrayList;

public class Rook implements Piece {
    private boolean wasMoved;
    private Color color;

    public Rook(Color color) {
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

            if (ownPosition.getCol() != targetPosition.getCol()) {
                int ownCol = ownPosition.getCol();
                int targetCol = targetPosition.getCol();
                int distanceHorizontal = targetCol - ownCol;

                for (int col = distanceHorizontal < 0 ? targetCol : ownCol;
                     distanceHorizontal < 0 ? col < ownCol : col < targetCol;
                     col++) {
                    path.add(new Position(ownPosition.getRow(), col));
                }
            } else {
                int ownRow = ownPosition.getRow();
                int targetRow = targetPosition.getRow();
                int distanceVertical = targetRow - ownRow;

                for (int row = distanceVertical < 0 ? targetRow : ownRow;
                     distanceVertical < 0 ? row < ownRow : row < targetRow;
                     row++) {
                    path.add(new Position(row, ownPosition.getCol()));
                }
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

        if (distanceVertical != 0 && distanceHorizontal != 0) {
            return false;
        } else if (distanceHorizontal != 0 && distanceVertical == 0) {
            for (int col = distanceHorizontal < 0 ? newCol + 1 : oldCol + 1;
                 distanceHorizontal < 0 ? col < oldCol : col < newCol;
                 col++) {
                if (chessField.getPiece(new Position(oldRow, col)) != null) {
                    return false;
                }
            }
        } else if (distanceHorizontal == 0 && distanceVertical != 0) {
            for (int row = distanceVertical < 0 ? newRow + 1 : oldRow + 1;
                 distanceVertical < 0 ? row < oldRow : row < newRow;
                 row++) {
                if (chessField.getPiece(new Position(row, oldCol)) != null) {
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
