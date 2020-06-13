package com.mz.chess.game;

public class BoardUtils {

    public static boolean isFieldInBoard(int x, int y) {
        return x >= 0 && y >= 0 && x < 8 && y < 8;
    }
}
