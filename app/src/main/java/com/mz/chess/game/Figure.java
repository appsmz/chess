package com.mz.chess.game;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class Figure {
    private static Figure EMPTY = new Figure(FigureColor.NONE, FigureType.EMPTY);

    public Figure(FigureColor color, FigureType type) {
        this.color = color;
        this.type = type;
    }

    private final FigureColor color;
    private FigureType type;
    private boolean moved = false;
    private List<Pair<Integer, Integer>> possibleMoves = new ArrayList<>();

    public static Figure empty() {
        return EMPTY;
    }

    public void setMoved() {
        this.moved = true;
    }

    public void setPossibleMoves(List<Pair<Integer, Integer>> possibleMoves) {
        this.possibleMoves = possibleMoves;
    }

    public void promote(FigureType figureToPromote) {
        type = figureToPromote;
    }
}
