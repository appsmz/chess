package com.mz.chess.game;

import android.content.SharedPreferences;

import com.mz.chess.menu.GameColor;
import com.mz.chess.menu.GameMode;

public class GameOptions {
    private GameMode gameMode;
    private int difficulty;
    private GameColor gameColor;
    private int gameTime;
    private int moveTime;

    public GameOptions(GameMode gameMode, int difficulty, GameColor gameColor, int gameTime, int moveTime) {
        this.gameMode = gameMode;
        this.difficulty = difficulty;
        this.gameColor = gameColor;
        this.gameTime = gameTime;
        this.moveTime = moveTime;
    }

    public static GameOptions fromSharedPrefs(SharedPreferences sp) {
        return new GameOptions(GameMode.valueOf(sp.getString(Game.NEW_GAME_MODE, GameMode.SINGLE.name())), sp.getInt(Game.NEW_GAME_DIFF, 1),
                GameColor.valueOf(sp.getString(Game.NEW_GAME_COLOR, GameColor.WHITE.name())), sp.getInt(Game.NEW_GAME_TIMER, -1),
                sp.getInt(Game.NEW_GAME_MOVE_TIMER, -1));
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public GameColor getGameColor() {
        return gameColor;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getMoveTime() {
        return moveTime;
    }
}
