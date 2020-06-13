package com.mz.chess.game.check.validator;

import com.mz.chess.game.Field;
import com.mz.chess.game.FigureColor;
import com.mz.chess.game.FigureType;

import static com.mz.chess.game.BoardUtils.isFieldInBoard;

public class HorAndVertCheckValidator {
    public boolean findAttackingHorAndVert(int kingX, int kingY, FigureColor kingColor, Field[][] fields) {
        for (int i = 1; i < 8; i++) {
            if (isFieldInBoard(kingX + i, kingY)) {
                if (isAttackingHorOrVert(kingX + i, kingY, kingColor, fields)) {
                    return true;
                } else if (fields[kingX + i][kingY].getFigure().getType() != FigureType.EMPTY) {
                    break;
                }
            } else {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (isFieldInBoard(kingX - i, kingY)) {
                if (isAttackingHorOrVert(kingX - i, kingY, kingColor, fields)) {
                    return true;
                } else if (fields[kingX - i][kingY].getFigure().getType() != FigureType.EMPTY) {
                    break;
                }
            } else {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (isFieldInBoard(kingX, kingY - i)) {
                if (isAttackingHorOrVert(kingX, kingY - i, kingColor, fields)) {
                    return true;
                } else if (fields[kingX][kingY - i].getFigure().getType() != FigureType.EMPTY) {
                    break;
                }
            } else {
                break;
            }
        }

        for (int i = 1; i < 8; i++) {
            if (isFieldInBoard(kingX, kingY + i)) {
                if (isAttackingHorOrVert(kingX, kingY + i, kingColor, fields)) {
                    return true;
                } else if (fields[kingX][kingY + i].getFigure().getType() != FigureType.EMPTY) {
                    break;
                }
            } else {
                break;
            }
        }

        return false;
    }

    private boolean isAttackingHorOrVert(int x, int y, FigureColor kingColor, Field[][] fields) {
        return (fields[x][y].getFigure().getType() == FigureType.ROOK
                || fields[x][y].getFigure().getType() == FigureType.QUEEN)
                && fields[x][y].getFigure().getColor() != kingColor;
    }
}
