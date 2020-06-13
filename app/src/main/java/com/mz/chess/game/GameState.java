package com.mz.chess.game;

import lombok.Getter;

@Getter
public
class GameState {

    private FigureColor currentPlayer;
    private long whitePlayerTimeLeft;
    private long blackPlayerTimeLeft;
    private long tempWhitePlayerTimeLeft;
    private long tempBlackPlayerTimeLeft;
    private int additionalMoveTimeInMilliSeconds;
    private int movesNotChanging;
    private int fullMoveCount;

    public GameState(FigureColor currentPlayer, int whitePlayerTimeLeftInMins, int blackPlayerTimeLeftInMins, int additionalMoveTimeInSec) {
        this.currentPlayer = currentPlayer;
        this.whitePlayerTimeLeft = whitePlayerTimeLeftInMins * 60 * 1000;
        this.blackPlayerTimeLeft = blackPlayerTimeLeftInMins * 60 * 1000;
        this.tempWhitePlayerTimeLeft = whitePlayerTimeLeft;
        this.tempBlackPlayerTimeLeft = blackPlayerTimeLeft;
        this.additionalMoveTimeInMilliSeconds = additionalMoveTimeInSec * 1000;
        this.movesNotChanging = 0;
        this.fullMoveCount = 0;
    }

    public GameState(GamePersistentState persistentState, int additionalMoveTimeInMilliSeconds) {
        this.currentPlayer = persistentState.getCurrentPlayer();
        this.whitePlayerTimeLeft = persistentState.getWhitePlayerTimeLeft();
        this.blackPlayerTimeLeft = persistentState.getBlackPlayerTimeLeft();
        this.tempWhitePlayerTimeLeft = persistentState.getWhitePlayerTimeLeft();
        this.tempBlackPlayerTimeLeft = persistentState.getBlackPlayerTimeLeft();
        this.additionalMoveTimeInMilliSeconds = additionalMoveTimeInMilliSeconds;
        this.movesNotChanging = persistentState.getMovesNotChanging();
        this.fullMoveCount  = persistentState.getFullMoveCount();
    }

    public void nextPlayer() {
        if (currentPlayer == FigureColor.BLACK) {
            fullMoveCount++;
        }
        updateCurrentPlayerTime();
        currentPlayer = currentPlayer == FigureColor.WHITE ? FigureColor.BLACK : FigureColor.WHITE;
    }

    public boolean decreaseTempCurrentPlayerTime(long time) {
        if (currentPlayer == FigureColor.WHITE) {
            tempWhitePlayerTimeLeft -= time;
            return tempWhitePlayerTimeLeft <= 0;
        } else {
            tempBlackPlayerTimeLeft -= time;
            return tempBlackPlayerTimeLeft <= 0;
        }
    }

    private void updateCurrentPlayerTime() {
        long additionalTime = 0;

        if (currentPlayer == FigureColor.WHITE) {
            long timeSpent = whitePlayerTimeLeft - tempWhitePlayerTimeLeft;
            if (timeSpent < additionalMoveTimeInMilliSeconds) {
                additionalTime = additionalMoveTimeInMilliSeconds - timeSpent;
            }
            tempWhitePlayerTimeLeft += additionalTime;
            whitePlayerTimeLeft = tempWhitePlayerTimeLeft;
        } else {
            long timeSpent = blackPlayerTimeLeft - tempBlackPlayerTimeLeft;
            if (timeSpent < additionalMoveTimeInMilliSeconds) {
                additionalTime = additionalMoveTimeInMilliSeconds - timeSpent;
            }
            tempBlackPlayerTimeLeft += additionalTime;
            blackPlayerTimeLeft = tempBlackPlayerTimeLeft;
        }
    }

    public FigureColor getOppositePlayer() {
        return currentPlayer == FigureColor.WHITE ? FigureColor.BLACK : FigureColor.WHITE;
    }

    public void setCurrentTime() {
        whitePlayerTimeLeft = tempWhitePlayerTimeLeft;
        blackPlayerTimeLeft = tempBlackPlayerTimeLeft;
    }

    public void increaseMovesNotChanging() {
        movesNotChanging++;
    }

    public void resetMovesNotChanging() {
        movesNotChanging = 0;
    }

    public int getMovesNotChanging() {
        return movesNotChanging;
    }

    public int getFullMoveCount() {
        return fullMoveCount;
    }
}
