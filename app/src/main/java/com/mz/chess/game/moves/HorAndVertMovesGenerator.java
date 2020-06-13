package com.mz.chess.game.moves;

import android.util.Pair;

import com.mz.chess.game.Board;
import com.mz.chess.game.Field;

import java.util.ArrayList;
import java.util.List;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;
import static com.mz.chess.game.moves.PossibleMovesGenerator.moveIsValid;

public class HorAndVertMovesGenerator {

    public static List<Pair<Integer, Integer>> findHorAndVertMoves(Field field, Board board) {
        List<Pair<Integer, Integer>> moves = new ArrayList<>();

        int x = field.getX();
        int y = field.getY();

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x - i, y) || board.getFields()[x - i][y].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x - i, y, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x - i, y));
            }

            if (!board.getFields()[x - i][y].isEmpty()) {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x + i, y) || board.getFields()[x + i][y].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x + i, y, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x + i, y));
            }

            if (!board.getFields()[x + i][y].isEmpty()) {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x, y - i) || board.getFields()[x][y - i].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x, y - i, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x, y - i));
            }

            if (!board.getFields()[x][y - i].isEmpty()) {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (!isFieldInBoard(x, y + i) || board.getFields()[x][y + i].getFigure().getColor() == field.getFigure().getColor()) {
                break;
            }

            if (moveIsValid(x, y, x, y + i, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x, y + i));
            }

            if (!board.getFields()[x][y + i].isEmpty()) {
                break;
            }
        }

        return moves;
    }
}
