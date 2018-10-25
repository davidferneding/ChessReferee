package Pieces;

import ChessField.ChessFieldHolder;
import ChessField.Move;
import ChessField.Position;

import java.util.ArrayList;

public class Pawn implements Piece {
    private boolean wasMoved;
    private Color color;

    public Pawn(Color color) {
        this.color = color;
        this.wasMoved = false;
    }

    @Override
    public ArrayList<Position> getValidMoves(Position position, ChessFieldHolder chessField) {
//        ArrayList<Position> validMoves = new ArrayList<>();
//        int row = position.getRow();
//        int col = position.getCol();
//        int rowfactor = chessField.getPiece(position).getColor() == Color.BLACK ? 1 : -1;
//
//        if (chessField.getPiece(new Position(row + rowfactor, col)) == null) {
//            validMoves.add(new Position(row + rowfactor, col));
//
//            if (!wasMoved) {
//                validMoves.add(new Position(row + rowfactor + rowfactor, col));
//            }
//        }
//
//        if (chessField.getPiece(new Position(row + rowfactor, col + 1)) != null
//                && !chessField.getPiece(new Position(row + rowfactor, col + 1)).getColor().equals(this.color)) {
//            validMoves.add(new Position(row + rowfactor, col + 1));
//        }
//
//        if (chessField.getPiece(new Position(row + rowfactor, col - 1)) != null
//                && !chessField.getPiece(new Position(row + rowfactor, col - 1)).getColor().equals(this.color)) {
//            validMoves.add(new Position(row + rowfactor, col - 1));
//        }
//
//        return validMoves;

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
            return new ArrayList<>();
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

        int distanceVertical = color == Color.BLACK ? newRow - oldRow : oldRow - newRow;
        int distanceHorizontal = oldCol < newCol ? newCol - oldCol : oldCol - newCol;

        if (distanceHorizontal == 1) {
            //attack
            if (distanceVertical != 1) {
                return false;
            } else {
                return chessField.getPiece(move.getNewPosition()) != null && chessField.getPiece(move.getNewPosition()).getColor() != color;
            }
        } else if (distanceHorizontal == 0) {
            //move
            if (distanceVertical > 2 || distanceVertical < 1) {
                return false;
            } else if (distanceVertical == 2 && wasMoved) {
                return false;
            } else {
                return chessField.getPiece(move.getNewPosition()) == null
                        && (distanceVertical == 1
                        || chessField.getPiece(
                        new Position(
                                color == Color.WHITE
                                        ? move.getNewPosition().getRow() + 1
                                        : move.getNewPosition().getRow() - 1,
                                move.getNewPosition().getCol()))
                        == null);
            }
        } else {
            return false;
        }
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
