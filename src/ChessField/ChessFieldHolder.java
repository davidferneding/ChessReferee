package ChessField;

import Pieces.*;

import java.util.HashMap;
import java.util.Random;

public class ChessFieldHolder {
    private HashMap<Position, Piece> pieces;

    private ChessFieldHolder(ChessFieldHolder other) {
        this.pieces = new HashMap<>();
        this.pieces.putAll(other.pieces);
    }

    public ChessFieldHolder() {
        pieces = new HashMap<>();

        for (int col = 0; col < 8; col++) {
            addPiece(new Pawn(Color.BLACK), new Position(1, col));
            addPiece(new Pawn(Color.WHITE), new Position(6, col));
        }

        addPiece(new Rook(Color.BLACK), new Position(0, 0));
        addPiece(new Rook(Color.BLACK), new Position(0, 7));

        addPiece(new Knight(Color.BLACK), new Position(0, 1));
        addPiece(new Knight(Color.BLACK), new Position(0, 6));

        addPiece(new Bishop(Color.BLACK), new Position(0, 2));
        addPiece(new Bishop(Color.BLACK), new Position(0, 5));

        addPiece(new Queen(Color.BLACK), new Position(0, 3));

        addPiece(new King(Color.BLACK), new Position(0, 4));


        addPiece(new Rook(Color.WHITE), new Position(7, 0));
        addPiece(new Rook(Color.WHITE), new Position(7, 7));

        addPiece(new Knight(Color.WHITE), new Position(7, 1));
        addPiece(new Knight(Color.WHITE), new Position(7, 6));

        addPiece(new Bishop(Color.WHITE), new Position(7, 2));
        addPiece(new Bishop(Color.WHITE), new Position(7, 5));

        addPiece(new Queen(Color.WHITE), new Position(7, 3));

        addPiece(new King(Color.WHITE), new Position(7, 4));
    }

    private void addPiece(Piece piece, Position position) {
        pieces.put(position, piece);
    }

    public Piece getPiece(Position position) {
        return pieces.get(position);
    }

    public HashMap<Position, Piece> getAll() {
        return pieces;
    }

    public void doMove(Move move) {
        pieces.remove(move.getNewPosition());

        if (pieces.get(move.getOldPosition()) instanceof Pawn && (move.getNewPosition().getRow() == 0 || move.getNewPosition().getRow() == 7)) {
            addPiece(new Queen(pieces.get(move.getOldPosition()).getColor()), move.getNewPosition());
        } else {
            addPiece(pieces.get(move.getOldPosition()), move.getNewPosition());
        }

        pieces.remove(move.getOldPosition());

        getPiece(move.getNewPosition()).setWasMoved(true);
    }

    public ChessFieldHolder getCopy() {
        return new ChessFieldHolder(this);
    }

    public Position getRandomPiecePosition(Color color) {
        Position position = null;
        while (position == null || !getPiece(position).getColor().equals(color)) {
				        position = (Position) pieces.keySet().toArray()[(new Random()).nextInt(pieces.size())];
        }
        
        return position;
    }
}

