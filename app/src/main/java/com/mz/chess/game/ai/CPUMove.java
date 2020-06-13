package com.mz.chess.game.ai;

public class CPUMove implements Runnable {

    private final CPUPlayer cpuPlayer;

    public CPUMove(CPUPlayer cpuPlayer) {
        this.cpuPlayer = cpuPlayer;
    }

    @Override
    public void run() {
        cpuPlayer.makeMove();
    }
}
