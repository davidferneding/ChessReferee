package ChessField;

import Pieces.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Calendar;

public class ChessFieldPane extends Canvas implements ChessFieldViewRepresentation {
    private static final int BOARD_SIZE = 600;
    private static final int FIELD_SIZE = BOARD_SIZE / 8;
    private static final int IMAGE_HEIGHT = FIELD_SIZE;
    private static final int IMAGE_WIDTH = (int) ((double) IMAGE_HEIGHT * 0.6);
    private static final int IMAGE_PADDING = (FIELD_SIZE - IMAGE_WIDTH) / 2;

    private GraphicsContext graphicsContext;
    private Position draggedPiecePosition;
    private int moveOffsetX;
    private int moveOffsetY;
    private Position moveStartPosition;
    private Position moveEndPosition;
    private ImageHolder imageHolder;
    private ChessFieldHolder chessField;
    private Pieces.Color currentMoveColor;

    public ChessFieldPane() {
        this.setWidth(BOARD_SIZE);
        this.setHeight(BOARD_SIZE);

        imageHolder = new ImageHolder();

        this.setOnMousePressed(event -> {
            Position clickedPosition = new Position((int) event.getY() / FIELD_SIZE, (int) event.getX() / FIELD_SIZE);
            if (chessField.getPiece(clickedPosition) != null && chessField.getPiece(clickedPosition).getColor() == currentMoveColor) {
                moveStartPosition = clickedPosition;
                draggedPiecePosition = new Position((int) event.getY(), (int) event.getX());
                moveOffsetX = (int) (event.getX() % FIELD_SIZE);
                moveOffsetY = (int) (event.getY() % FIELD_SIZE);
                drawField(null, false);
            }
        });

        this.setOnMouseDragged(event -> {
            if (moveStartPosition != null) {
                draggedPiecePosition = new Position((int) event.getY(), (int) event.getX());
                drawField(null, false);
            }
        });

        this.setOnMouseReleased(event -> {
            if (moveStartPosition != null) {
                moveEndPosition = new Position((int) event.getY() / FIELD_SIZE, (int) event.getX() / FIELD_SIZE);

                if (moveStartPosition.equals(moveEndPosition)) {
                    moveStartPosition = null;
                    moveEndPosition = null;
                }

                draggedPiecePosition = null;
            }
        });

        graphicsContext = this.getGraphicsContext2D();

        chessField = new ChessFieldHolder();
        drawField(null, false);
    }

    public void drawField(ChessFieldHolder chessField, Move move, boolean withAnimation) {
        this.chessField = chessField;
        Move moveWithAbsoluteStartPosition = null;

        if (move != null) {
            moveWithAbsoluteStartPosition = new Move(
                    new Position(move.getOldPosition().getRow() * FIELD_SIZE,
                            move.getOldPosition().getCol() * FIELD_SIZE),
                    move.getNewPosition());
        }

        drawField(moveWithAbsoluteStartPosition, withAnimation);

        if (withAnimation) {
            Move finalMoveWithAbsoluteStartPosition = moveWithAbsoluteStartPosition;
            new Thread(() -> drawAnimatedMove(finalMoveWithAbsoluteStartPosition)).start();
        }
    }

    /***
     * @param move oldPosition: absolute position on the canvas, newPosition: target board position
     * @param withAnimation if the move should be animated
     */
    private void drawField(Move move, boolean withAnimation) {
        graphicsContext.clearRect(0, 0, BOARD_SIZE, BOARD_SIZE);

        for (int row = 0; row < BOARD_SIZE; row += FIELD_SIZE) {
            for (int col = 0; col < BOARD_SIZE; col += FIELD_SIZE) {
                graphicsContext.setFill((row + col / FIELD_SIZE) % 2 == 0 ? Color.LIGHTGREY : Color.DARKGREY);
                graphicsContext.fillRect(col, row, FIELD_SIZE, FIELD_SIZE);
            }
        }

        for (Position position : chessField.getAll().keySet()) {
            if (moveStartPosition == null || !moveStartPosition.equals(position)) {
                if (move != null && move.getNewPosition().equals(position) && withAnimation) {
                    graphicsContext.drawImage(imageHolder.getImageForPiece(chessField.getPiece(position)),
                            move.getOldPosition().getCol() + IMAGE_PADDING,
                            move.getOldPosition().getRow(),
                            IMAGE_WIDTH,
                            IMAGE_HEIGHT);
                } else {
                    graphicsContext.drawImage(imageHolder.getImageForPiece(chessField.getPiece(position)),
                            position.getCol() * FIELD_SIZE + IMAGE_PADDING,
                            position.getRow() * FIELD_SIZE,
                            IMAGE_WIDTH,
                            IMAGE_HEIGHT);
                }
            } else {
                //draw piece
                graphicsContext.drawImage(imageHolder.getImageForPiece(chessField.getPiece(position)),
                        draggedPiecePosition.getCol() + IMAGE_PADDING - moveOffsetX,
                        draggedPiecePosition.getRow() - moveOffsetY,
                        IMAGE_WIDTH,
                        IMAGE_HEIGHT);

                //mark start position
                graphicsContext.setStroke(Color.BLUE);
                graphicsContext.strokeRect(position.getCol() * FIELD_SIZE,
                        position.getRow() * FIELD_SIZE,
                        FIELD_SIZE,
                        FIELD_SIZE);

                //mark valid moves
                ArrayList<Position> validMoves = chessField.getPiece(position).getValidMoves(position, chessField);
                for (Position validPosition : validMoves) {
                    //mark start position
                    graphicsContext.setStroke(Color.GREEN);
                    graphicsContext.strokeRect(validPosition.getCol() * FIELD_SIZE,
                            validPosition.getRow() * FIELD_SIZE,
                            FIELD_SIZE,
                            FIELD_SIZE);
                }

                //mark hover position if valid
                Position currentField = new Position((draggedPiecePosition.getRow() / FIELD_SIZE),
                        draggedPiecePosition.getCol() / FIELD_SIZE);
                if (validMoves.contains(currentField) && !currentField.equals(position)) {
                    graphicsContext.setStroke(Color.RED);
                    graphicsContext.strokeRect(currentField.getCol() * FIELD_SIZE,
                            currentField.getRow() * FIELD_SIZE,
                            FIELD_SIZE,
                            FIELD_SIZE);
                }
            }
        }
    }

    private void drawAnimatedMove(Move move) {
        int stepcount = 25;
        int counter = 0;
        double colFactor = ((double) (move.getNewPosition().getCol() * FIELD_SIZE) - move.getOldPosition().getCol()) / (double) stepcount;
        double rowFactor = ((double) (move.getNewPosition().getRow() * FIELD_SIZE) - move.getOldPosition().getRow()) / (double) stepcount;

        while (counter < stepcount) {
            counter++;
            drawField(new Move(new Position((int) (move.getOldPosition().getRow() + (rowFactor * counter)),
                    (int) (move.getOldPosition().getCol() + (colFactor * counter))), move.getNewPosition()), true);

            long timestampEnd = Calendar.getInstance().getTimeInMillis() + 20;
            while (Calendar.getInstance().getTimeInMillis() < timestampEnd) {
                Thread.yield();
            }
        }
    }

    @Override
    public Move requestMoveFromHumanPlayer(String playerName) {
        if (moveStartPosition == null || moveEndPosition == null) {
            return null;
        } else {
            Position moveStartPositionCopy = moveStartPosition;
            Position moveEndPositionCopy = moveEndPosition;
            moveStartPosition = null;
            moveEndPosition = null;

            return new Move(moveStartPositionCopy, moveEndPositionCopy);
        }
    }

    public void setCurrentMoveColor(Pieces.Color currentMoveColor) {
        this.currentMoveColor = currentMoveColor;
    }
}

class ImageHolder {
    private Image blackPawn;
    private Image blackRook;
    private Image blackKnight;
    private Image blackBishop;
    private Image blackQueen;
    private Image blackKing;

    private Image whitePawn;
    private Image whiteRook;
    private Image whiteKnight;
    private Image whiteBishop;
    private Image whiteQueen;
    private Image whiteKing;

    public ImageHolder() {
        loadImages();
    }

    private void loadImages() {
        blackPawn = new Image(getClass().getClassLoader().getResource("res/chess_pieces/black_pawn.png").toExternalForm());
        blackRook = new Image(getClass().getClassLoader().getResource("res/chess_pieces/black_rook.png").toExternalForm());
        blackKnight = new Image(getClass().getClassLoader().getResource("res/chess_pieces/black_knight.png").toExternalForm());
        blackBishop = new Image(getClass().getClassLoader().getResource("res/chess_pieces/black_bishop.png").toExternalForm());
        blackQueen = new Image(getClass().getClassLoader().getResource("res/chess_pieces/black_queen.png").toExternalForm());
        blackKing = new Image(getClass().getClassLoader().getResource("res/chess_pieces/black_king.png").toExternalForm());

        whitePawn = new Image(getClass().getClassLoader().getResource("res/chess_pieces/white_pawn.png").toExternalForm());
        whiteRook = new Image(getClass().getClassLoader().getResource("res/chess_pieces/white_rook.png").toExternalForm());
        whiteKnight = new Image(getClass().getClassLoader().getResource("res/chess_pieces/white_knight.png").toExternalForm());
        whiteBishop = new Image(getClass().getClassLoader().getResource("res/chess_pieces/white_bishop.png").toExternalForm());
        whiteQueen = new Image(getClass().getClassLoader().getResource("res/chess_pieces/white_queen.png").toExternalForm());
        whiteKing = new Image(getClass().getClassLoader().getResource("res/chess_pieces/white_king.png").toExternalForm());
    }

    Image getImageForPiece(Piece piece) {
        if (piece instanceof Pawn) {
            if (piece.getColor() == Pieces.Color.BLACK) {
                return blackPawn;
            } else {
                return whitePawn;
            }
        }

        if (piece instanceof Rook) {
            if (piece.getColor() == Pieces.Color.BLACK) {
                return blackRook;
            } else {
                return whiteRook;
            }
        }

        if (piece instanceof Knight) {
            if (piece.getColor() == Pieces.Color.BLACK) {
                return blackKnight;
            } else {
                return whiteKnight;
            }
        }

        if (piece instanceof Bishop) {
            if (piece.getColor() == Pieces.Color.BLACK) {
                return blackBishop;
            } else {
                return whiteBishop;
            }
        }

        if (piece instanceof Queen) {
            if (piece.getColor() == Pieces.Color.BLACK) {
                return blackQueen;
            } else {
                return whiteQueen;
            }
        }

        if (piece instanceof King) {
            if (piece.getColor() == Pieces.Color.BLACK) {
                return blackKing;
            } else {
                return whiteKing;
            }
        }

        return null;
    }
}