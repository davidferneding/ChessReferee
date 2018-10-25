package ChessField;

import Main.ApplicationViewHolder;
import Pieces.Color;
import Pieces.King;
import Player.Player;
import com.sun.javafx.tools.packager.Log;

import java.util.ArrayList;
import java.util.Calendar;

public final class ChessFieldHelper {
    private ApplicationViewHolder applicationViewHolder;
    private ChessFieldHolder chessField;
    private ChessFieldViewRepresentation viewRepresentation;
    private Player playerBlack;
    private Player playerWhite;
    private Player checkmate;
    private int moveCount;
    private boolean isPaused;
    private Move lastMove;

    public ChessFieldHelper(ChessFieldViewRepresentation view, Player playerBlack, Player playerWhite, ApplicationViewHolder viewHolder) {
        this.viewRepresentation = view;
        this.playerBlack = playerBlack;
        this.playerWhite = playerWhite;
        this.applicationViewHolder = viewHolder;

        chessField = new ChessFieldHolder();
    }

    public void startPlaying() {
        checkmate = null;
        moveCount = 0;

        viewRepresentation.drawField(chessField, null, false);

        lastMove = null;
        requestNextMove();
    }

    private void requestNextMove() {
        if (!isPaused) {
            Player activePlayer;
            if (++moveCount % 2 == 0) {
                activePlayer = playerBlack;
                viewRepresentation.setCurrentMoveColor(Color.BLACK);
            } else {
                activePlayer = playerWhite;
                viewRepresentation.setCurrentMoveColor(Color.WHITE);
            }

            new Thread(() -> activePlayer.getNextMove(this, lastMove)).start();
        }
    }

    public void nextMoveCallback(Move nextMove) {
        executeMove(nextMove);
        lastMove = nextMove;

        if (checkmate == null) {
            Player activePlayer;
            if (moveCount % 2 == 0) {
                activePlayer = playerWhite;
            } else {
                activePlayer = playerBlack;
            }

            if (!activePlayer.isHuman()) {
                long timestampEnd = Calendar.getInstance().getTimeInMillis() + 2000;
                while (Calendar.getInstance().getTimeInMillis() < timestampEnd) {
                    Thread.yield();
                }
            }

            requestNextMove();
        } else if (checkmate.equals(playerBlack)) {
            applicationViewHolder.gameEndCallback(playerWhite);
        } else {
            applicationViewHolder.gameEndCallback(playerBlack);
        }
    }

    private void executeMove(Move move) {
        boolean isMoveValid = isMoveValid(move);

        if (move != null && chessField.getPiece(move.getOldPosition()) != null) {
            boolean withAnimation = !((moveCount % 2 == 0) ? playerBlack.isHuman() : playerWhite.isHuman());

            chessField.doMove(move);
            viewRepresentation.drawField(chessField, move, withAnimation);
        } else {
            if (moveCount % 2 == 0) {
                playerWhite.setDisplayElement("Null als Move übergeben. " + playerWhite.getDisplayElement());
                checkmate = playerBlack;
            } else {
                playerBlack.setDisplayElement("Null als Move übergeben. " + playerBlack.getDisplayElement());
                checkmate = playerWhite;
            }

            return;
        }

        if (isMoveValid) {
            checkCheckmate();
        } else {
            Log.debug("Invalid move");
            if (moveCount % 2 == 0) {
                playerWhite.setDisplayElement("Nicht erlaubter Spielzug. " + playerWhite.getDisplayElement());
                checkmate = playerBlack;
            } else {
                playerBlack.setDisplayElement("Nicht erlaubter Spielzug. " + playerBlack.getDisplayElement());
                checkmate = playerWhite;
            }
        }
    }

    private boolean isMoveValid(Move move) {
        if (move != null && chessField.getPiece(move.getOldPosition()) != null
                && chessField.getPiece(move.getOldPosition()).getColor() == (moveCount % 2 == 0 ? Color.BLACK : Color.WHITE)) {
            return chessField.getPiece(move.getOldPosition()).isMoveValid(move, chessField);
        } else {
            return false;
        }
    }

    private void checkCheckmate() {
        Color opponentColor = moveCount % 2 == 0 ? Color.WHITE : Color.BLACK;
        Color ownColor = moveCount % 2 != 0 ? Color.WHITE : Color.BLACK;

        Position kingPosition = getKingPosition(opponentColor);

        if (kingPosition == null) { //The King has been taken
            checkmate = opponentColor == Color.BLACK ? playerBlack : playerWhite;

            if (opponentColor == Color.BLACK) {
                playerWhite.setDisplayElement("König geschlagen. " + playerWhite.getDisplayElement());
            } else {
                playerBlack.setDisplayElement("König geschlagen. " + playerBlack.getDisplayElement());
            }

            return;
        }


        if (isInCheck(kingPosition, ownColor, chessField)) {
            checkmate = opponentColor == Color.BLACK ? playerBlack : playerWhite;

            //we know it's in check so we have to check if we can get out by moving the king
            ChessFieldHolder checkChessField;
            for (Position position : chessField.getPiece(kingPosition).getValidMoves(kingPosition, chessField)) {
                checkChessField = chessField.getCopy();

                checkChessField.doMove(new Move(kingPosition, position));

                if (!isInCheck(position, ownColor, checkChessField)) {
                    checkmate = null;
                    break;
                }
            }

            if (checkmate != null) {
                //we can't take it by moving away so we have to check if we can take the checking opponent or get in the way
                ArrayList<Position> checkingOpponents = getCheckingOpponents(kingPosition, ownColor);
                if (checkingOpponents.size() == 1) { //taking it and blocking it only work if there's just one checking opponent
                    if (isInCheck(checkingOpponents.get(0), opponentColor, chessField)) {
                        ArrayList<Position> kingSaviors = getCheckingOpponents(checkingOpponents.get(0), opponentColor);
                        if (kingSaviors.size() == 1 && chessField.getPiece(kingSaviors.get(0)) instanceof King) {
                            ChessFieldHolder chessFieldCopy = chessField.getCopy();
                            chessFieldCopy.doMove(new Move(kingPosition, checkingOpponents.get(0)));
                            if (!isInCheck(checkingOpponents.get(0), ownColor, chessFieldCopy)) {
                                checkmate = null;
                            }
                        } else {
                            checkmate = null;
                        }
                    } else {
                        ArrayList<Position> pathToKing = chessField.getPiece(checkingOpponents.get(0)).getPathToTakePiece(checkingOpponents.get(0), kingPosition, chessField);

                        for (Position position : pathToKing) {
                            if (isInCheck(position, opponentColor, chessField)) {
                                checkmate = null;
                            }
                        }
                    }
                }
            }
        }

        if (checkmate == playerBlack) {
            playerWhite.setDisplayElement("Schachmatt. " + playerWhite.getDisplayElement());
        } else if (checkmate == playerWhite) {
            playerBlack.setDisplayElement("Schachmatt. " + playerBlack.getDisplayElement());
        }
    }

    private Position getKingPosition(Color color) {
        //get King position
        Position kingPosition = null;
        for (Position position : chessField.getAll().keySet()) {
            if (chessField.getPiece(position).getColor() == color && chessField.getPiece(position) instanceof King) {
                kingPosition = position;
                break;
            }
        }

        return kingPosition;
    }

    private boolean isInCheck(Position kingPosition, Color opponentColor, ChessFieldHolder chessField) {
        for (Position position : chessField.getAll().keySet()) {
            if (chessField.getPiece(position).getColor() == opponentColor) {
                if (chessField.getPiece(position).getValidMoves(position, chessField).contains(kingPosition)) {
                    return true;
                }
            }
        }

        return false;
    }

    private ArrayList<Position> getCheckingOpponents(Position ownPosition, Color opponentColor) {
        ArrayList<Position> opponents = new ArrayList<>();

        for (Position position : chessField.getAll().keySet()) {
            if (chessField.getPiece(position).getColor() == opponentColor) {
                if (chessField.getPiece(position).getValidMoves(position, chessField).contains(ownPosition)) {
                    opponents.add(position);
                }
            }
        }

        return opponents;
    }

    public void pauseGame() {
        isPaused = true;
    }
}
