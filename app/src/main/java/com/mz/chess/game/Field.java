package com.mz.chess.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Field {
    private Figure figure;
    private int x;
    private int y;

    public void setFigure(Figure figure) {
        this.figure = figure;
    }

    public void clearField() {
        this.figure = Figure.empty();
    }

    public boolean isEmpty() {
        return figure.getType() == FigureType.EMPTY;
    }

    public boolean isOppositeColor(FigureColor color) {
        return color != figure.getColor() && figure.getColor() != FigureColor.NONE;
    }
}
