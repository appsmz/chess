package com.mz.chess.game.check.validator;

import com.mz.chess.game.Field;
import com.mz.chess.game.FigureType;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;

public class KingCheckValidator {

    public boolean findAttackingKing(int kingX, int kingY, Field[][] fields) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (isFieldInBoard(kingX + i, kingY + j) && (i != 0 || j != 0)
                        && fields[kingX + i][kingY + j].getFigure().getType() == FigureType.KING) {
                    return true;
                }
            }
        }

        return false;
    }
}
