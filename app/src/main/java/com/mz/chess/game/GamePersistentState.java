package com.mz.chess.game;

import android.content.SharedPreferences;
import android.util.Pair;

import com.mz.chess.Utils;

import java.util.ArrayList;
import java.util.List;

class GamePersistentState {

    private int[][] board;
    private List<Integer> beatenByWhite;
    private List<Integer> beatenByBlack;
    private boolean blackOnTop;
    private Pair<Integer, Integer> enPassantCandidate;
    private FigureColor currentPlayer;
    private long whitePlayerTimeLeft;
    private long blackPlayerTimeLeft;
    private int movesNotChanging;
    private int fullMoveCount;

    public GamePersistentState(int[][] board, List<Integer> beatenByWhite, List<Integer> beatenByBlack, boolean blackOnTop, Pair<Integer, Integer> enPassantCandidate, FigureColor currentPlayer, long whitePlayerTimeLeft, long blackPlayerTimeLeft, int movesNotChanging, int fullMoveCount) {
        this.board = board;
        this.beatenByWhite = beatenByWhite;
        this.beatenByBlack = beatenByBlack;
        this.blackOnTop = blackOnTop;
        this.enPassantCandidate = enPassantCandidate;
        this.currentPlayer = currentPlayer;
        this.whitePlayerTimeLeft = whitePlayerTimeLeft;
        this.blackPlayerTimeLeft = blackPlayerTimeLeft;
        this.movesNotChanging = movesNotChanging;
        this.fullMoveCount = fullMoveCount;
    }

    public GamePersistentState(GameState gameState, Board board) {
        this.board = Utils.mapBoardToIntArray(board.getFields());
        this.beatenByBlack = mapBeaten(board.getBeatenByBlack());
        this.beatenByWhite = mapBeaten(board.getBeatenByWhite());
        this.blackOnTop = board.isBlackOnTop();
        this.enPassantCandidate = board.getEnPassantCandidate();
        this.currentPlayer = gameState.getCurrentPlayer();
        this.whitePlayerTimeLeft = gameState.getWhitePlayerTimeLeft();
        this.blackPlayerTimeLeft = gameState.getBlackPlayerTimeLeft();
        this.movesNotChanging = gameState.getMovesNotChanging();
        this.fullMoveCount = gameState.getFullMoveCount();
    }

    private List<Integer> mapBeaten(List<Figure> beaten) {
        List<Integer> list = new ArrayList<>();
        for (Figure figure : beaten) {
            list.add(figure.getType().ordinal());
        }
        return list;
    }

    public void saveState(SharedPreferences preferences, boolean gameFinished) {
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("board", gameFinished ? "" : Utils.arrayToString(board));
        editor.putString("beatenByWhite", Utils.listToString(beatenByWhite));
        editor.putString("beatenByBlack", Utils.listToString(beatenByBlack));
        editor.putBoolean("blackOnTop", blackOnTop);
        editor.putString("enPassantCandidate", enPassantCandidate == null ? "" : enPassantCandidate.first + "," + enPassantCandidate.second);
        editor.putInt("currentPlayer", currentPlayer.colorMultiplier());
        editor.putLong("whitePlayerTimeLeft", whitePlayerTimeLeft);
        editor.putLong("blackPlayerTimeLeft", blackPlayerTimeLeft);
        editor.putInt("movesNotChanging", movesNotChanging);
        editor.putInt("fullMoveCount", fullMoveCount);

        editor.apply();
    }

    public static GamePersistentState readState(SharedPreferences preferences) {
        String boardString = preferences.getString("board", "");
        boolean blackOnTop = preferences.getBoolean("blackOnTop", true);

        if ("".equals(boardString)) {
            return GamePersistentState.createDefault(blackOnTop);
        } else {
            int[][] board = Utils.stringToArray(boardString);
            List<Integer> beatenByWhite = mapBeatenFromString(preferences.getString("beatenByWhite", ""));
            List<Integer> beatenByBlack = mapBeatenFromString(preferences.getString("beatenByBlack", ""));
            Pair<Integer, Integer> enPassantCandidate = mapEnPassantCandidate(preferences.getString("enPassantCandidate", ""));
            FigureColor currentPlayer = FigureColor.fromMultiplier(preferences.getInt("currentPlayer", 1));
            long whitePlayerTimeLeft = preferences.getLong("whitePlayerTimeLeft", Game.DEFAULT_TIME);
            long blackPlayerTimeLeft = preferences.getLong("blackPlayerTimeLeft", Game.DEFAULT_TIME);
            int movesNotChanging = preferences.getInt("movesNotChanging", 0);
            int fullMoveCount = preferences.getInt("fullMoveCount", 0);
            return new GamePersistentState(board, beatenByWhite, beatenByBlack, blackOnTop, enPassantCandidate, currentPlayer, whitePlayerTimeLeft, blackPlayerTimeLeft, movesNotChanging, fullMoveCount);
        }
    }

    private static List<Integer> mapBeatenFromString(String beatenByWhite) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < beatenByWhite.length(); i++) {
            list.add(beatenByWhite.charAt(i) - '0');
        }
        return list;
    }

    private static Pair<Integer, Integer> mapEnPassantCandidate(String enPassantCandidate) {
        if (enPassantCandidate == null || enPassantCandidate.equals("")) {
            return null;
        } else {
            String[] split = enPassantCandidate.split(",");
            return new Pair<>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        }
    }

    private static GamePersistentState createDefault(boolean blackOnTop) {
        String boardString = Board.getDefaultBoardString(blackOnTop);
        return new GamePersistentState(Utils.stringToArray(boardString), new ArrayList<>(), new ArrayList<>(), blackOnTop,
                null, FigureColor.WHITE, Game.DEFAULT_TIME, Game.DEFAULT_TIME, 0, 0);
    }

    public int[][] getBoard() {
        return board;
    }

    public List<Integer> getBeatenByWhite() {
        return beatenByWhite;
    }

    public List<Integer> getBeatenByBlack() {
        return beatenByBlack;
    }

    public boolean isBlackOnTop() {
        return blackOnTop;
    }

    public Pair<Integer, Integer> getEnPassantCandidate() {
        return enPassantCandidate;
    }

    public FigureColor getCurrentPlayer() {
        return currentPlayer;
    }

    public long getWhitePlayerTimeLeft() {
        return whitePlayerTimeLeft;
    }

    public long getBlackPlayerTimeLeft() {
        return blackPlayerTimeLeft;
    }

    public int getMovesNotChanging() {
        return movesNotChanging;
    }

    public int getFullMoveCount() {
        return fullMoveCount;
    }
}
