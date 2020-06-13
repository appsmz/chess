package com.mz.chess.game;

public enum FigureType {
    KING(1000, "k"), QUEEN(100, "q"), ROOK(50, "r"),
    BISHOP(30, "b"), KNIGHT(30, "n"), PAWN(10, "p"), EMPTY(0, "1");

    public static FigureType[] NEW_BOARD_FIGURE_LINE = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};
    public static FigureType[] NEW_BOARD_FIGURE_LINE_WHITE_ON_TOP = {ROOK, KNIGHT, BISHOP, KING, QUEEN, BISHOP, KNIGHT, ROOK};

    FigureType(int materialValue, String fenCode) {
        this.materialValue = materialValue;
        this.fenCode = fenCode;
    }

    private int materialValue;

    private String fenCode;

    public int getMaterialValue() {
        return materialValue;
    }

    public String getFenCode() {
        return fenCode;
    }
}
