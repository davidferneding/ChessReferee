package Player;

import ChessField.ChessFieldHelper;
import ChessField.ChessFieldHolder;
import ChessField.Move;
import ChessField.Position;

import java.util.ArrayList;
import java.util.Random;

public class DummyPlayer extends Player {
    private ChessFieldHolder chessField;

    public DummyPlayer() {
    }

    @Override
    public void getNextMove(ChessFieldHelper chessFieldHelper, Move lastMove) {
        if (chessField == null) {
            chessField = new ChessFieldHolder();
        }

        if (lastMove != null) {
            this.chessField.doMove(lastMove);
        }

        Position position = null;
        ArrayList<Position> validMoves = null;

        while (validMoves == null || validMoves.size() <= 0) {
            position = chessField.getRandomPiecePosition(color);
            validMoves = chessField.getPiece(position).getValidMoves(position, chessField);
        }

        Move move = new Move(position, validMoves.get((new Random()).nextInt(validMoves.size())));
        chessField.doMove(move);
        chessFieldHelper.nextMoveCallback(move);
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}
