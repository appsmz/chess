package com.mz.chess.game;

public enum FigureColor {
    WHITE(1), BLACK(-1), NONE(0);

    private int colorMultiplier;

    FigureColor(int colorMultiplier) {
        this.colorMultiplier = colorMultiplier;
    }

    public int colorMultiplier() {
        return colorMultiplier;
    }

    public static FigureColor fromMultiplier(int colorMultiplier) {
        if (colorMultiplier > 0) {
            return WHITE;
        } else if (colorMultiplier < 0) {
            return BLACK;
        } else {
            return NONE;
        }
    }

    public FigureColor getOppositeColor() {
        if (this == WHITE) {
            return BLACK;
        } else if (this == BLACK) {
            return WHITE;
        } else {
            return NONE;
        }
    }
}
