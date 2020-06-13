package com.mz.chess.game.moves;

import android.util.Pair;

import com.mz.chess.game.Board;
import com.mz.chess.game.Field;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.FigureType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;

public class PossibleMovesGenerator {

    public static int generatePossibleMoves(Board board, FigureColor currentPlayer) {

        int possibleMoves = 0;

        for (Field[] fieldsRow : board.getFields()) {
            for (Field field : fieldsRow) {
                if (field.isOppositeColor(currentPlayer)) {
                    continue;
                }

                List<Pair<Integer, Integer>> moves = null;
                switch (field.getFigure().getType()) {
                    case PAWN: moves = findMovesForPawn(field, board); break;
                    case BISHOP: moves = findMovesForBishop(field, board); break;
                    case ROOK: moves = findMovesForRook(field, board); break;
                    case QUEEN: moves = findMovesForQueen(field, board); break;
                    case KING: moves = findMovesForKing(field, board); break;
                    case KNIGHT: moves = findMovesForKnight(field, board); break;
                    case EMPTY: moves = Collections.emptyList();
                }
                field.getFigure().setPossibleMoves(moves);
                possibleMoves += moves.size();
            }
        }

        return possibleMoves;
    }

    private static List<Pair<Integer, Integer>> findMovesForPawn(Field field, Board board) {
        List<Pair<Integer, Integer>> moves = new ArrayList<>();

        int x = field.getX();
        int y = field.getY();

        int direction = board.isBlackOnTop() && field.getFigure().getColor() == FigureColor.WHITE
                || !board.isBlackOnTop() && field.getFigure().getColor() == FigureColor.BLACK
                ? -1 : 1;

        if (x + direction >= 0 && x + direction < 8) {
            if (board.getFields()[x + direction][y].isEmpty()) {
                if (moveIsValid(x, y, x + direction, y, field.getFigure().getColor(), board)) {
                    moves.add(new Pair<>(x + direction, y));
                }
            }

            if (y - 1 >= 0 && board.getFields()[x + direction][y - 1].isOppositeColor(field.getFigure().getColor())) {
                if (moveIsValid(x, y, x + direction, y - 1, field.getFigure().getColor(), board)) {
                    moves.add(new Pair<>(x + direction, y - 1));
                }
            }

            if (y + 1 < 8 && board.getFields()[x + direction][y + 1].isOppositeColor(field.getFigure().getColor())) {
                if (moveIsValid(x, y, x + direction, y + 1, field.getFigure().getColor(), board)) {
                    moves.add(new Pair<>(x + direction, y + 1));
                }
            }

            if ((x == 6 && direction == -1 || x == 1 && direction == 1) &&
                    board.getFields()[x + direction][y].isEmpty() && board.getFields()[x + (direction * 2)][y].isEmpty()) {
                if (moveIsValid(x, y, x + direction * 2, y, field.getFigure().getColor(), board)) {
                    moves.add(new Pair<>(x + direction * 2, y));
                }
            }

            if (y - 1 >= 0 && board.isEnPassantPossible(x, y - 1)) {
                Board newBoard = board.copy(x, y, x + direction, y - 1);
                newBoard.getFields()[x][y - 1].clearField();
                if (!newBoard.isCheck(field.getFigure().getColor())) {
                    moves.add(new Pair<>(x + direction, y - 1));
                }
            }

            if (y + 1 < 8 && board.isEnPassantPossible(x, y + 1)) {
                Board newBoard = board.copy(x, y, x + direction, y + 1);
                newBoard.getFields()[x][y + 1].clearField();
                if (!newBoard.isCheck(field.getFigure().getColor())) {
                    moves.add(new Pair<>(x + direction, y + 1));
                }
            }
        }

        return moves;
    }

    private static List<Pair<Integer, Integer>> findMovesForKing(Field field, Board board) {
        List<Pair<Integer, Integer>> moves = new ArrayList<>();

        int x = field.getX();
        int y = field.getY();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (isFieldInBoard(targetX, targetY) && (i != 0 || j != 0)
                        && (board.getFields()[targetX][targetY].isEmpty() || board.getFields()[targetX][targetY].isOppositeColor(field.getFigure().getColor()))) {
                    if (moveIsValid(x, y, targetX, targetY, field.getFigure().getColor(), board)) {
                        moves.add(new Pair<>(targetX, targetY));
                    }
                }
            }
        }

        if (board.isBlackOnTop()) {
            if (!field.getFigure().isMoved() && !board.isCheck(field.getFigure().getColor()) && !board.getFields()[x][0].getFigure().isMoved() && board.getFields()[x][0].getFigure().getType() == FigureType.ROOK
                    && board.getFields()[x][1].isEmpty() && board.getFields()[x][2].isEmpty() && board.getFields()[x][3].isEmpty()) {
                if (moveIsValid(x, y, x, y - 1, field.getFigure().getColor(), board)) {
                    if (moveIsValid(x, y, x, y - 2, field.getFigure().getColor(), board)) {
                        moves.add(new Pair<>(x, y - 2));
                    }
                }
            }

            if (!field.getFigure().isMoved() && !board.isCheck(field.getFigure().getColor()) && !board.getFields()[x][7].getFigure().isMoved() && board.getFields()[x][7].getFigure().getType() == FigureType.ROOK
                    && board.getFields()[x][5].isEmpty() && board.getFields()[x][6].isEmpty()) {
                if (moveIsValid(x, y, x, y + 1, field.getFigure().getColor(), board)) {
                    if (moveIsValid(x, y, x, y + 2, field.getFigure().getColor(), board)) {
                        moves.add(new Pair<>(x, y + 2));
                    }
                }
            }
        } else {
            if (!field.getFigure().isMoved() && !board.isCheck(field.getFigure().getColor()) && !board.getFields()[x][7].getFigure().isMoved() && board.getFields()[x][7].getFigure().getType() == FigureType.ROOK
                    && board.getFields()[x][6].isEmpty() && board.getFields()[x][5].isEmpty() && board.getFields()[x][4].isEmpty()) {
                if (moveIsValid(x, y, x, y + 1, field.getFigure().getColor(), board)) {
                    if (moveIsValid(x, y, x, y + 2, field.getFigure().getColor(), board)) {
                        moves.add(new Pair<>(x, y + 2));
                    }
                }
            }

            if (!field.getFigure().isMoved() && !board.isCheck(field.getFigure().getColor()) && !board.getFields()[x][0].getFigure().isMoved() && board.getFields()[x][0].getFigure().getType() == FigureType.ROOK
                    && board.getFields()[x][1].isEmpty() && board.getFields()[x][2].isEmpty()) {
                if (moveIsValid(x, y, x, y - 1, field.getFigure().getColor(), board)) {
                    if (moveIsValid(x, y, x, y - 2, field.getFigure().getColor(), board)) {
                        moves.add(new Pair<>(x, y - 2));
                    }
                }
            }
        }

        return moves;
    }

    private static List<Pair<Integer, Integer>> findMovesForQueen(Field field, Board board) {
        List<Pair<Integer, Integer>> moves = HorAndVertMovesGenerator.findHorAndVertMoves(field, board);
        moves.addAll(DiagonalMovesGenerator.findDiagonalMoves(field, board));
        return moves;
    }

    private static List<Pair<Integer, Integer>> findMovesForRook(Field field, Board board) {
        return HorAndVertMovesGenerator.findHorAndVertMoves(field, board);
    }

    private static List<Pair<Integer, Integer>> findMovesForBishop(Field field, Board board) {
        return DiagonalMovesGenerator.findDiagonalMoves(field, board);
    }

    private static List<Pair<Integer, Integer>> findMovesForKnight(Field field, Board board) {
        return KnightMovesGenerator.findMovesForKnight(field, board);
    }

    static boolean moveIsValid(int x, int y, int targetX, int targetY, FigureColor color, Board board) {
        Board newBoard = board.copy(x, y, targetX, targetY);
        return !newBoard.isCheck(color);
    }
}
