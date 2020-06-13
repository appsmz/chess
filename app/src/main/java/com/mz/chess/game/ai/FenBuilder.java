package com.mz.chess.game.ai;

import com.mz.chess.game.Board;
import com.mz.chess.game.Field;
import com.mz.chess.game.Figure;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.FigureType;
import com.mz.chess.game.GameState;

public class FenBuilder {
    static String buildFenString(Board board, FigureColor cpuColor, GameState gameState, boolean reverseFields) {
        StringBuilder fen = new StringBuilder();

        String fieldsPart = buildFieldsPart(board);
        if (reverseFields) {
            fieldsPart = new StringBuilder(fieldsPart).reverse().toString();
        }

        fen.append(fieldsPart);

        fen.append(" ");

        fen.append(cpuColor == FigureColor.WHITE ? "w" : "b");

        fen.append(" ");

        fen.append(buildCastlingPart(board));

        fen.append(" ");

        fen.append(buildEnPassantPart(board));

        fen.append(" ");

        fen.append(gameState.getMovesNotChanging());

        fen.append(" ");

        fen.append(gameState.getFullMoveCount());

        return fen.toString();
    }

    private static String buildEnPassantPart(Board board) {
        if (board.getEnPassantCandidate() != null) {
            Field enPassantCandidate = board.getFields()[board.getEnPassantCandidate().first][board.getEnPassantCandidate().second];

            if (board.isBlackOnTop() && enPassantCandidate.getFigure().getColor() == FigureColor.BLACK) {
                return "" + (char)('a' + enPassantCandidate.getX()) + enPassantCandidate.getY();
            } else {
                return "" + (char)('a' + enPassantCandidate.getX()) + (enPassantCandidate.getY() + 2);
            }
        } else {
            return "-";
        }
    }

    private static String buildCastlingPart(Board board) {
        StringBuilder fen = new StringBuilder();

        if (!board.getWhiteKing().getFigure().isMoved()) {
            Field figureAt0 = board.getFields()[board.getWhiteKing().getX()][0];
            if (figureAt0.getFigure().getType() == FigureType.ROOK && !figureAt0.getFigure().isMoved()) {
                fen.append(board.getWhiteKing().getY() == 3 ? "K" : "Q");
            }
            Field figureAt7 = board.getFields()[board.getWhiteKing().getX()][7];
            if (figureAt7.getFigure().getType() == FigureType.ROOK && !figureAt7.getFigure().isMoved()) {
                fen.append(board.getWhiteKing().getY() == 3 ? "Q" : "K");
            }
        }
        if (!board.getBlackKing().getFigure().isMoved()) {
            Field figureAt0 = board.getFields()[board.getBlackKing().getX()][0];
            if (figureAt0.getFigure().getType() == FigureType.ROOK && !figureAt0.getFigure().isMoved()) {
                fen.append(board.getWhiteKing().getY() == 3 ? "k" : "q");
            }
            Field figureAt7 = board.getFields()[board.getBlackKing().getX()][7];
            if (figureAt7.getFigure().getType() == FigureType.ROOK && !figureAt7.getFigure().isMoved()) {
                fen.append(board.getWhiteKing().getY() == 3 ? "q" : "k");
            }
        }

        return fen.toString();
    }

    private static String buildFieldsPart(Board board) {
        StringBuilder fen = new StringBuilder();
        Field[][] fields = board.getFields();

        for (int i = 0; i < fields.length; i++) {
            int rowEmptyCount = 0;

            for (int j = 0; j < fields[i].length; j++) {
                Figure figure = fields[i][j].getFigure();
                if (figure.getType() == FigureType.EMPTY) {
                    rowEmptyCount++;
                } else {
                    if (rowEmptyCount > 0) {
                        fen.append(rowEmptyCount);
                        rowEmptyCount = 0;
                    }
                    String figureCode = figure.getType().getFenCode();
                    fen.append(figure.getColor() == FigureColor.WHITE ? figureCode.toUpperCase() : figureCode);
                }
            }

            if (rowEmptyCount > 0) {
                fen.append(rowEmptyCount);
            }

            if (i != fields.length - 1) {
                fen.append("/");
            }
        }

        return fen.toString();
    }
}
