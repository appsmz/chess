package com.mz.chess;

import com.mz.chess.game.Field;

import java.util.List;

public class Utils {
    public static String timeToString(long time) {
        int seconds = (int) (time / 1000);
        int hours = seconds / 3600;
        int minutes = (seconds - hours * 3600) / 60;
        seconds = seconds % 60;

        StringBuilder timeSB = new StringBuilder();

        if (hours > 0) {
            timeSB.append(hours).append(minutes < 10 ? ":0" : ":");
        }
        timeSB.append(minutes);
        timeSB.append(seconds < 10 ? ":0" : ":").append(seconds);

        return timeSB.toString();
    }

    public static int[][] stringToArray(String src) {
        int[][] diagram = new int[8][8];

        String[] split = src.split(",");
        int index = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                diagram[i][j] = Integer.parseInt(split[index++]);
            }
        }

        return diagram;
    }

    public static String arrayToString(int[][] array) {
        StringBuilder builder = new StringBuilder();

        for (int[] ints : array) {
            for (int num : ints) {
                builder.append(num).append(",");
            }
        }
        return builder.toString();
    }

    public static String listToString(List<Integer> beatenByWhite) {
        StringBuilder sb = new StringBuilder();

        for (Integer val : beatenByWhite) {
            sb.append(val);
        }

        return sb.toString();
    }

    public static int[][] mapBoardToIntArray(Field[][] fields) {
        int[][] boardInt = new int[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardInt[i][j] = (fields[i][j].getFigure().getType().ordinal() + 1) * fields[i][j].getFigure().getColor().colorMultiplier();
            }
        }

        return boardInt;
    }
}
