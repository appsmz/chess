package com.mz.chess.game.moves;

import android.util.Pair;

import com.mz.chess.game.Board;
import com.mz.chess.game.Field;

import java.util.ArrayList;
import java.util.List;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;
import static com.mz.chess.game.moves.PossibleMovesGenerator.moveIsValid;

public class KnightMovesGenerator {

    public static List<Pair<Integer, Integer>> findMovesForKnight(Field field, Board board) {
        List<Pair<Integer, Integer>> moves = new ArrayList<>();

        checkXAndY(field, board, moves, field.getX() - 2, field.getY() + 1);
        checkXAndY(field, board, moves, field.getX() - 2, field.getY() - 1);
        checkXAndY(field, board, moves, field.getX() + 1, field.getY() - 2);
        checkXAndY(field, board, moves, field.getX() + 1, field.getY() + 2);
        checkXAndY(field, board, moves, field.getX() - 1, field.getY() - 2);
        checkXAndY(field, board, moves, field.getX() - 1, field.getY() + 2);
        checkXAndY(field, board, moves, field.getX() + 2, field.getY() + 1);
        checkXAndY(field, board, moves, field.getX() + 2, field.getY() - 1);

        return moves;
    }

    private static void checkXAndY(Field field, Board board, List<Pair<Integer, Integer>> moves, int x, int y) {
        if (isFieldInBoard(x, y) && board.getFields()[x][y].getFigure().getColor() != field.getFigure().getColor()) {
            if (moveIsValid(field.getX(), field.getY(), x, y, field.getFigure().getColor(), board)) {
                moves.add(new Pair<>(x, y));
            }
        }
    }
}
