package com.mz.chess.game.check;

import com.mz.chess.game.Field;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.check.validator.DiagonalCheckValidator;
import com.mz.chess.game.check.validator.HorAndVertCheckValidator;
import com.mz.chess.game.check.validator.KingCheckValidator;
import com.mz.chess.game.check.validator.KnightCheckValidator;
import com.mz.chess.game.check.validator.PawnCheckValidator;

public class CheckValidator {

    private DiagonalCheckValidator diagonalCheckValidator = new DiagonalCheckValidator();
    private HorAndVertCheckValidator horAndVertCheckValidator = new HorAndVertCheckValidator();
    private KingCheckValidator kingCheckValidator = new KingCheckValidator();
    private KnightCheckValidator knightCheckValidator = new KnightCheckValidator();
    private PawnCheckValidator pawnCheckValidator = new PawnCheckValidator();

    public boolean isCheck(int kingX, int kingY, FigureColor kingColor, Field[][] fields, boolean blackOnTop) {
        if (pawnCheckValidator.findAttackingPawns(kingX, kingY, kingColor, fields, blackOnTop)) {
            return true;
        }

        if (knightCheckValidator.findAttackingKnights(kingX, kingY, kingColor, fields)) {
            return true;
        }

        if (diagonalCheckValidator.findAttackingDiagonally(kingX, kingY, kingColor, fields)) {
            return true;
        }

        if (horAndVertCheckValidator.findAttackingHorAndVert(kingX, kingY, kingColor, fields)) {
            return true;
        }

        if (kingCheckValidator.findAttackingKing(kingX, kingY, fields)) {
            return true;
        }

        return false;
    }
}
