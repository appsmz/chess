package com.mz.chess.game;

import android.graphics.Rect;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
class GameDimensions {
    private int fieldWidth;
    private int figureWidth;
    private int sideMargin;
    private int topMargin;

    public Rect getFieldDstRect(int x, int y) {
        int left = sideMargin + fieldWidth * y;
        int top = topMargin + x * fieldWidth;
        return new Rect(left, top, left + fieldWidth, top + fieldWidth);
    }

    public Rect getSmallerFieldDstRect(int x, int y) {
        int left = sideMargin + fieldWidth * y;
        int top = topMargin + x * fieldWidth;
        return new Rect((int) (left + fieldWidth * 0.05), (int) (top + fieldWidth * 0.05),
                (int) (left + fieldWidth - fieldWidth * 0.05), (int)(top + fieldWidth - fieldWidth * 0.05));
    }
}
