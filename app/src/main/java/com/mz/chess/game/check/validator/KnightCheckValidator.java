package com.mz.chess.game.check.validator;

import com.mz.chess.game.Field;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.FigureType;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;

public class KnightCheckValidator {

    public boolean findAttackingKnights(int kingX, int kingY, FigureColor kingColor, Field[][] fields) {
        int knightX = kingX - 2;
        int knightY = kingY + 1;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        knightX = kingX - 2;
        knightY = kingY - 1;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        knightX = kingX + 1;
        knightY = kingY - 2;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        knightX = kingX + 1;
        knightY = kingY + 2;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        knightX = kingX - 1;
        knightY = kingY - 2;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        knightX = kingX - 1;
        knightY = kingY + 2;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        knightX = kingX + 2;
        knightY = kingY - 1;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        knightX = kingX + 2;
        knightY = kingY + 1;
        if (isKnightAttacking(knightX, knightY, kingColor, fields)) {
            return true;
        }

        return false;
    }

    public boolean isKnightAttacking(int knightX, int knightY, FigureColor kingColor, Field[][] fields) {
        return isFieldInBoard(knightX, knightY)
                && fields[knightX][knightY].getFigure().getType() == FigureType.KNIGHT
                && fields[knightX][knightY].getFigure().getColor() != kingColor;
    }
}
