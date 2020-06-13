package com.mz.chess.game.check.validator;

import com.mz.chess.game.Field;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.FigureType;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;

public class PawnCheckValidator {

    public boolean findAttackingPawns(int kingX, int kingY, FigureColor kingColor, Field[][] fields, boolean blackOnTop) {
        if ((blackOnTop && kingColor == FigureColor.WHITE) || (!blackOnTop && kingColor == FigureColor.BLACK)) {
            if (isFieldInBoard(kingX - 1, kingY - 1)
                    && fields[kingX - 1][kingY - 1].getFigure().getType() == FigureType.PAWN
                    && fields[kingX - 1][kingY - 1].getFigure().getColor() != kingColor) {
                return true;
            }

            if (isFieldInBoard(kingX - 1, kingY + 1)
                    && fields[kingX - 1][kingY + 1].getFigure().getType() == FigureType.PAWN
                    && fields[kingX - 1][kingY + 1].getFigure().getColor() != kingColor) {
                return true;
            }
        } else {
            if (isFieldInBoard(kingX + 1, kingY - 1)
                    && fields[kingX + 1][kingY - 1].getFigure().getType() == FigureType.PAWN
                    && fields[kingX + 1][kingY - 1].getFigure().getColor() != kingColor) {
                return true;
            }

            if (isFieldInBoard(kingX + 1, kingY + 1)
                    && fields[kingX + 1][kingY + 1].getFigure().getType() == FigureType.PAWN
                    && fields[kingX + 1][kingY + 1].getFigure().getColor() != kingColor) {
                return true;
            }
        }

        return false;
    }
}
