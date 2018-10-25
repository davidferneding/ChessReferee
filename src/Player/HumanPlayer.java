package Player;

import ChessField.ChessFieldHelper;
import ChessField.Move;

public class HumanPlayer extends Player {
    public HumanPlayer() {
    }

    @Override
    public void getNextMove(ChessFieldHelper chessFieldHelper, Move lastMove) {
        Move nextMove = null;
        while (nextMove == null) {
            nextMove = viewRepresentation.requestMoveFromHumanPlayer(displayElement);
            Thread.yield();
        }

        chessFieldHelper.nextMoveCallback(nextMove);
    }

    @Override
    public boolean isHuman() {
        return true;
    }
}
