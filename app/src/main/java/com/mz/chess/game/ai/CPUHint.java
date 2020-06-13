package com.mz.chess.game.ai;

public class CPUHint implements Runnable {

    private final HintPlayer hintPlayer;

    public CPUHint(HintPlayer hintPlayer) {
        this.hintPlayer = hintPlayer;
    }

    @Override
    public void run() {
        hintPlayer.getHint();
    }
}
