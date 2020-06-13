package com.mz.chess.game;

import android.util.Pair;

import com.mz.chess.Utils;
import com.mz.chess.game.check.CheckValidator;
import com.mz.chess.game.moves.PossibleMovesGenerator;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Board {

    private static final String START_BOARD_STRING_BLACK_TOP = "-3,-5,-4,-1,-2,-4,-5,-3,-6,-6,-6,-6,-6,-6,-6,-6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,6,6,6,6,6,6,3,5,4,1,2,4,5,3";
    private static final String START_BOARD_STRING_WHITE_TOP = "3,5,4,2,1,4,5,3,6,6,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-6,-6,-6,-6,-6,-6,-6,-6,-3,-5,-4,-2,-1,-4,-5,-3";
    private Field[][] fields = new Field[8][8];
    private List<Figure> beatenByWhite = new ArrayList<>();
    private List<Figure> beatenByBlack = new ArrayList<>();
    private Field whiteKing;
    private Field blackKing;
    private boolean blackOnTop;
    private CheckValidator checkValidator = new CheckValidator();
    private Pair<Integer, Integer> enPassantCandidate = null;

    Board(boolean blackOnTop) {
        this.blackOnTop = blackOnTop;
        initNewBoard();
    }

    Board(GamePersistentState persistentState) {
        initBoard(mapIntArrayToBoard(persistentState.getBoard()));
        this.blackOnTop = persistentState.isBlackOnTop();
        this.beatenByWhite = mapBeaten(persistentState.getBeatenByWhite(), FigureColor.BLACK);
        this.beatenByBlack = mapBeaten(persistentState.getBeatenByBlack(), FigureColor.WHITE);
        this.enPassantCandidate = persistentState.getEnPassantCandidate();
    }

    private List<Figure> mapBeaten(List<Integer> beaten, FigureColor color) {
        List<Figure> list = new ArrayList<>();

        for (Integer integer : beaten) {
            list.add(new Figure(color, FigureType.values()[integer]));
        }

        return list;
    }

    private Board(Field[][] fields, boolean blackOnTop) {
        this.blackOnTop = blackOnTop;
        initBoard(fields);
    }

    private void initBoard(Field[][] fields) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.fields[i][j] = new Field(new Figure(fields[i][j].getFigure().getColor(), fields[i][j].getFigure().getType()), i, j);

                if (this.fields[i][j].getFigure().getType() == FigureType.KING) {
                    if (this.fields[i][j].getFigure().getColor() == FigureColor.WHITE) {
                        whiteKing = this.fields[i][j];
                    } else {
                        blackKing = this.fields[i][j];
                    }
                }
            }
        }
    }

    private void initNewBoard() {
        for (int i = 0; i < 8; i++) {
            fields[0][i] = new Field(new Figure(blackOnTop ? FigureColor.BLACK : FigureColor.WHITE,
                    blackOnTop ? FigureType.NEW_BOARD_FIGURE_LINE[i] : FigureType.NEW_BOARD_FIGURE_LINE_WHITE_ON_TOP[i]), 0, i);
            fields[1][i] = new Field(new Figure(blackOnTop ? FigureColor.BLACK : FigureColor.WHITE, FigureType.PAWN), 1, i);

            for (int j = 2; j < 6; j++) {
                fields[j][i] = new Field(new Figure(FigureColor.NONE, FigureType.EMPTY), j, i);
            }

            fields[6][i] = new Field(new Figure(blackOnTop ? FigureColor.WHITE : FigureColor.BLACK, FigureType.PAWN), 6, i);
            fields[7][i] = new Field(new Figure(blackOnTop ? FigureColor.WHITE : FigureColor.BLACK,
                    blackOnTop ? FigureType.NEW_BOARD_FIGURE_LINE[i] : FigureType.NEW_BOARD_FIGURE_LINE_WHITE_ON_TOP[i]), 7, i);

            if (blackOnTop) {
                if (FigureType.NEW_BOARD_FIGURE_LINE[i] == FigureType.KING) {
                    blackKing = fields[0][i];
                    whiteKing = fields[7][i];
                }
            } else {
                if (FigureType.NEW_BOARD_FIGURE_LINE_WHITE_ON_TOP[i] == FigureType.KING) {
                    blackKing = fields[7][i];
                    whiteKing = fields[0][i];
                }
            }
        }

    }

    public Board copy(int fromX, int fromY, int toX, int toY) {
        Board board = new Board(fields, this.isBlackOnTop());
        board.moveFigureAndCheckPromotion(fromX, fromY, toX, toY);
        return board;
    }

    public boolean moveFigureAndCheckPromotion(int fromX, int fromY, int toX, int toY) {
        FigureColor movingFigureColor = fields[fromX][fromY].getFigure().getColor();
        if (!fields[toX][toY].isEmpty() && movingFigureColor != fields[toX][toY].getFigure().getColor()) {
            Figure beatenFigure = new Figure(fields[toX][toY].getFigure().getColor(), fields[toX][toY].getFigure().getType());
            if (movingFigureColor == FigureColor.WHITE) {
                beatenByWhite.add(beatenFigure);
            } else {
                beatenByBlack.add(beatenFigure);
            }
        }
        fields[toX][toY].setFigure(fields[fromX][fromY].getFigure());
        fields[fromX][fromY].clearField();
        fields[toX][toY].getFigure().setMoved();

        if (fields[toX][toY].getFigure().getType() == FigureType.KING) {
            if (fields[toX][toY].getFigure().getColor() == FigureColor.WHITE) {
                whiteKing = fields[toX][toY];
            } else {
                blackKing = fields[toX][toY];
            }

            if (Math.abs(fromY - toY) == 2) {
                if (toY == 6) {
                    fields[toX][5].setFigure(fields[fromX][7].getFigure());
                    fields[fromX][7].clearField();
                    fields[toX][5].getFigure().setMoved();
                } else if (toY == 2) {
                    fields[toX][3].setFigure(fields[fromX][0].getFigure());
                    fields[fromX][0].clearField();
                    fields[toX][3].getFigure().setMoved();
                } else if (toY == 5) {
                    fields[toX][4].setFigure(fields[fromX][7].getFigure());
                    fields[fromX][7].clearField();
                    fields[toX][4].getFigure().setMoved();
                } else if (toY == 1) {
                    fields[toX][2].setFigure(fields[fromX][0].getFigure());
                    fields[fromX][0].clearField();
                    fields[toX][2].getFigure().setMoved();
                }
            }
        }

        if (fields[toX][toY].getFigure().getType() == FigureType.PAWN) {
            if (enPassantCandidate != null && enPassantCandidate.first == fromX && enPassantCandidate.second == toY) {
                Figure beatenFigure = new Figure(fields[fromX][toY].getFigure().getColor(), fields[fromX][toY].getFigure().getType());
                if (movingFigureColor == FigureColor.WHITE) {
                    beatenByWhite.add(beatenFigure);
                } else {
                    beatenByBlack.add(beatenFigure);
                }
                fields[fromX][toY].clearField();
            }
        }

        if (fields[toX][toY].getFigure().getType() == FigureType.PAWN && Math.abs(fromX - toX) == 2) {
            enPassantCandidate = new Pair<>(toX, toY);
        } else {
            enPassantCandidate = null;
        }

        return fields[toX][toY].getFigure().getType() == FigureType.PAWN && (toX == 0 || toX == 7);
    }

    public boolean isCheck(FigureColor color) {
        int kingX, kingY;

        if (color == FigureColor.WHITE) {
            kingX = whiteKing.getX();
            kingY = whiteKing.getY();
        } else {
            kingX = blackKing.getX();
            kingY = blackKing.getY();
        }

        return checkValidator.isCheck(kingX, kingY, color, fields, blackOnTop);
    }

    public int generatePossibleMoves(FigureColor currentPlayer) {
        return PossibleMovesGenerator.generatePossibleMoves(this, currentPlayer);
    }

    public boolean isEnPassantPossible(int x, int y) {
        return enPassantCandidate != null && enPassantCandidate.first == x && enPassantCandidate.second == y;
    }

    public List<Figure> getBeatenByWhite() {
        return beatenByWhite;
    }

    public List<Figure> getBeatenByBlack() {
        return beatenByBlack;
    }


    private Field[][] mapIntArrayToBoard(int[][] intBoard) {
        Field[][] board = new Field[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Figure figure = new Figure(FigureColor.fromMultiplier(intBoard[i][j]), mapFigureType(intBoard[i][j]));
                board[i][j] = new Field(figure, i, j);
            }
        }

        return board;
    }

    private FigureType mapFigureType(int value) {
        if (value == 0) {
            return FigureType.EMPTY;
        }

        return FigureType.values()[Math.abs(value) - 1];
    }

    public static String getDefaultBoardString(boolean blackOnTop) {
        return blackOnTop ? START_BOARD_STRING_BLACK_TOP : START_BOARD_STRING_WHITE_TOP;
    }

    public Pair<Integer, Integer> findPawnToPromote(FigureColor figureColor) {
        //checking opposite color because current player is actually not the one to be promoted
        int row = blackOnTop && figureColor == FigureColor.BLACK || !blackOnTop && figureColor == FigureColor.WHITE ? 7 : 0;

        for (int i = 0; i < 8; i++) {
            if (fields[row][i].getFigure().getType() == FigureType.PAWN) {
                return new Pair<>(row, i);
            }
        }

        throw new IllegalArgumentException("Pawn to be promoted not found");
    }

    public void promotePawn(Pair<Integer, Integer> pawnToPromote, FigureType figureToPromote) {
        fields[pawnToPromote.first][pawnToPromote.second].getFigure().promote(figureToPromote);
    }

    public boolean checkMaterialIsSufficient() {
        Field foundFigure = null;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                FigureType figure = fields[i][j].getFigure().getType();
                if (figure == FigureType.PAWN || figure == FigureType.QUEEN || figure == FigureType.ROOK) {
                    return true;
                }

                if (figure == FigureType.KNIGHT) {
                    if (foundFigure == null) {
                        foundFigure = fields[i][j];
                    } else {
                        return true;
                    }
                }

                if (figure == FigureType.BISHOP) {
                    if (foundFigure == null) {
                        foundFigure = fields[i][j];
                    } else {
                        if (foundFigure.getFigure().getType() == FigureType.KNIGHT ||
                                foundFigure.getX() % 2 == foundFigure.getY() % 2 && i % 2 != j % 2 ||
                                foundFigure.getX() % 2 != foundFigure.getY() % 2 && i % 2 == j % 2) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public String getBoardString() {
        return Utils.arrayToString(Utils.mapBoardToIntArray(fields));
    }
}
