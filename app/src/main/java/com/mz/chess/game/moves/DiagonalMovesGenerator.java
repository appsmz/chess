package com.mz.chess.game.moves;

import android.util.Pair;

import com.mz.chess.game.Board;
import com.mz.chess.game.Field;

import java.util.ArrayList;
import java.util.List;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;
import static com.mz.chess.game.moves.PossibleMovesGenerator.moveIsValid;

public class DiagonalMovesGenerator {

    public static List<Pair<Integer, Integer>> findDiagonalMoves(Field field, Board board) {
        List<Pair<Integer, Integer>> moves = new ArrayList<>();

        int x = field.getX();
        int y = field.getY();

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x - i, y - i) || board.getFields()[x - i][y - i].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x - i, y - i, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x - i, y - i));
            }

            if (!board.getFields()[x - i][y - i].isEmpty()) {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x - i, y + i) || board.getFields()[x - i][y + i].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x - i, y + i, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x - i, y + i));
            }

            if (!board.getFields()[x - i][y + i].isEmpty()) {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x + i, y + i) || board.getFields()[x + i][y + i].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x + i, y + i, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x + i, y + i));
            }

            if (!board.getFields()[x + i][y + i].isEmpty()) {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x + i, y - i) || board.getFields()[x + i][y - i].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x + i, y - i, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x + i, y - i));
            }

            if (!board.getFields()[x + i][y - i].isEmpty()) {
                break;
            }
        }

        return moves;
    }
}
