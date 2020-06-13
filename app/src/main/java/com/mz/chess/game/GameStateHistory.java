package com.mz.chess.game;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameStateHistory {
    private final boolean isAgainstCpu;
    private int currentState = -1;
    private List<Pair<GamePersistentState, Boolean>> states = new ArrayList<>();

    public GameStateHistory(boolean isAgainstCpu) {
        this.isAgainstCpu = isAgainstCpu;
    }

    public void addState(GamePersistentState gameState, boolean isCpuMove) {
        if (currentState < states.size() - 1) {
            clearStatesAhead();
        }
        states.add(new Pair<>(gameState, isCpuMove));
        currentState++;
    }

    private void clearStatesAhead() {
        states = states.subList(0, currentState + 1);
    }

    public boolean isRedoPossible() {
        return currentState < states.size() - 1;
    }

    public boolean isUndoPossible() {
        return currentState > 1 || currentState == 1 && !states.get(0).second ;
    }

    public GamePersistentState redo() {
        currentState = isRedoPossible() ? currentState + 1 : states.size() - 1;

        Pair<GamePersistentState, Boolean> state = getCurrentState();

        if (isAgainstCpu && !state.second) {
            currentState++;
            return getCurrentState().first;
        } else {
            return state.first;
        }
    }

    public GamePersistentState undo() {
        currentState = isUndoPossible() ? currentState - 1 : 0;

        Pair<GamePersistentState, Boolean> state = getCurrentState();

        if (isAgainstCpu && !state.second && currentState > 0) {
            currentState--;
            return getCurrentState().first;
        } else {
            return state.first;
        }
    }

    private Pair<GamePersistentState, Boolean> getCurrentState() {
        return states.get(currentState);
    }

    public boolean checkIfRepeatedThreeTimes() {
        int repeatedCount = 1;
        int[][] lastState = states.get(states.size() - 1).first.getBoard();

        for (int i = states.size() - 2; i >= 0; i--) {
            if (Arrays.deepEquals(states.get(i).first.getBoard(), lastState)) {
                repeatedCount++;
                if (repeatedCount == 3) {
                    return true;
                }
            }
        }

        return false;
    }
}
