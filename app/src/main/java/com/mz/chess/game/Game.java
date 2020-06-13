package com.mz.chess.game;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.mz.chess.ChessApplication;
import com.mz.chess.R;
import com.mz.chess.Utils;
import com.mz.chess.game.ai.CPUHint;
import com.mz.chess.game.ai.CPUMove;
import com.mz.chess.game.ai.CPUPlayer;
import com.mz.chess.game.ai.HintPlayer;
import com.mz.chess.menu.GameColor;
import com.mz.chess.menu.GameMode;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import lombok.SneakyThrows;

public class Game extends Activity {

    public static final long DEFAULT_TIME = 15 * 60 * 1000;
    public static final String IS_CONTINUE_KEY = "isContinue";
    public static final String NEW_GAME_MODE = "new_game_mode";
    public static final String NEW_GAME_DIFF = "new_game_diff";
    public static final String NEW_GAME_COLOR = "new_game_color";
    public static final String NEW_GAME_TIMER = "new_game_timer";
    public static final String NEW_GAME_MOVE_TIMER = "new_game_move_timer";
    public static final String NEW_GAME_STARTED = "new_game_started";
    public boolean hintInProgress = false;
    private GameView gameView;
    public Board board;

    public boolean gameFinished = false;
    public GameState gameState;
    private Field selectedField;
    private Handler mHandler = new Handler();
    private long lastTime;
    private SharedPreferences sharedPreferences;
    public GameStateHistory stateHistory;
    private Thread cpuThread;
    private FigureType selectedPromotion;
    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> cpuLastMove;
    public Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> hintMove;
    public GameOptions gameOptions;
    public FinishedReason finishedReason;
    public FigureColor winner;
    public boolean isDarkTheme;
    public ChessApplication application;
    private FigureColor cpuColor;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (ChessApplication) getApplication();

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        gameOptions = GameOptions.fromSharedPrefs(getSharedPreferences("newGameSettings", MODE_PRIVATE));
        isDarkTheme = getSharedPreferences("theme", MODE_PRIVATE).getBoolean("isDarkTheme", false);

        boolean keepScreenOn = sharedPreferences.getBoolean("screenOn", true);

        GameColor player1Color;
        if (getIntent().getBooleanExtra(IS_CONTINUE_KEY, false) ||
                getSharedPreferences("newGameSettings", MODE_PRIVATE).getBoolean(Game.NEW_GAME_STARTED, false)) {
            readState();
            player1Color = board.isBlackOnTop() ? GameColor.WHITE : GameColor.BLACK;
        } else {
            player1Color = initPlayerColor();

            getSharedPreferences("game", MODE_PRIVATE).edit().putString("board", "").apply();
            getSharedPreferences("newGameSettings", MODE_PRIVATE).edit().putBoolean(Game.NEW_GAME_STARTED, true).apply();
            initBoard(player1Color == GameColor.WHITE);
            gameState = new GameState(FigureColor.WHITE, gameOptions.getGameTime(), gameOptions.getGameTime(), gameOptions.getMoveTime());
        }

        initGame(player1Color);

        this.gameView = new GameView(this);
        gameView.setKeepScreenOn(keepScreenOn);

        setContentView(gameView);
    }

    private void initGame(GameColor player1Color) {
        board.generatePossibleMoves(gameState.getCurrentPlayer());

        initTimer();

        initGameStateHistory(player1Color);

        startCpuPlayer(player1Color);
    }

    private void startCpuPlayer(GameColor player1Color) {
        if (gameOptions.getGameMode() == GameMode.SINGLE) {
            cpuColor = player1Color == GameColor.WHITE ? FigureColor.BLACK : FigureColor.WHITE;
            if (cpuColor == FigureColor.WHITE && gameState.getCurrentPlayer() == FigureColor.WHITE) {
                makeCPUMove();
            }
        }
    }

    private void initGameStateHistory(GameColor player1Color) {
        stateHistory = new GameStateHistory(gameOptions.getGameMode() == GameMode.SINGLE);
        if (gameOptions.getGameMode() == GameMode.MULTI || gameOptions.getGameMode() == GameMode.SINGLE
                && (player1Color == GameColor.WHITE && gameState.getCurrentPlayer() == FigureColor.WHITE || player1Color == GameColor.BLACK && gameState.getCurrentPlayer() == FigureColor.BLACK )) {
            stateHistory.addState(new GamePersistentState(gameState, board), false);
        }
    }

    private void initTimer() {
        if (gameOptions.getGameTime() > 0) {
            lastTime = SystemClock.uptimeMillis();
            mHandler.removeCallbacks(mUpdateTimeTask);
            mHandler.postDelayed(mUpdateTimeTask, 100);
        }
    }

    @NonNull
    private GameColor initPlayerColor() {
        GameColor player1Color;
        if (gameOptions.getGameColor() == GameColor.WHITE) {
            player1Color = GameColor.WHITE;
        } else if (gameOptions.getGameColor() == GameColor.BLACK) {
            player1Color = GameColor.BLACK;
        } else {
            String lastPlayer1ColorName = getSharedPreferences("game", MODE_PRIVATE).getString("lastPlayer1Color", GameColor.BLACK.name());
            GameColor lastPlayer1Color = GameColor.valueOf(lastPlayer1ColorName);
            player1Color = lastPlayer1Color == GameColor.WHITE ? GameColor.BLACK : GameColor.WHITE;
            getSharedPreferences("game", MODE_PRIVATE).edit().putString("lastPlayer1Color", player1Color.name()).apply();
        }
        return player1Color;
    }

    private void readState() {
        GamePersistentState persistentState = GamePersistentState.readState(getSharedPreferences("game", MODE_PRIVATE));
        restoreState(persistentState);
    }

    private void restoreState(GamePersistentState persistentState) {
        this.board = new Board(persistentState);
        this.gameState = new GameState(persistentState, gameOptions.getMoveTime());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mUpdateTimeTask);
        gameState.setCurrentTime();

        if (cpuThread != null) {
            cpuThread.interrupt();
        }

        saveGameStateWithoutHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initTimer();
    }

    private GamePersistentState saveGameStateWithoutHistory() {
        GamePersistentState persistentState = new GamePersistentState(gameState, board);
        persistentState.saveState(getSharedPreferences("game", MODE_PRIVATE), gameFinished);
        return persistentState;
    }

    private boolean saveGameState(boolean isCpuMove) {
        GamePersistentState persistentState = saveGameStateWithoutHistory();
        stateHistory.addState(persistentState, isCpuMove);

        return stateHistory.checkIfRepeatedThreeTimes();
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long currentTime = SystemClock.uptimeMillis();
            boolean outOfTime = gameState.decreaseTempCurrentPlayerTime(currentTime - lastTime);

            if (outOfTime) {
                finishGame(gameState.getOppositePlayer(), FinishedReason.NO_TIME);
            } else {
                lastTime = currentTime;
                gameView.invalidate();
                mHandler.postAtTime(this, currentTime + 1000);
            }
        }
    };

    private void finishGame(FigureColor winner, FinishedReason finishedReason) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        getSharedPreferences("game", MODE_PRIVATE).edit().putString("board", "").apply();
        gameFinished = true;
        this.finishedReason = finishedReason;
        this.winner = winner;

        saveStats(winner);

        if (cpuThread != null) {
            cpuThread.interrupt();
        }

        runOnUiThread(() -> gameView.invalidate());
    }

    private void saveStats(FigureColor winner) {
        if (gameOptions.getGameMode() == GameMode.MULTI) {
            return;
        }

        String result = winner == FigureColor.NONE ? "draw" : winner == cpuColor ? "lose" : "win";

        String key = "count" + gameOptions.getDifficulty() + result;

        SharedPreferences.Editor editor = getSharedPreferences("stats", MODE_PRIVATE).edit();
        editor.putInt(key, getSharedPreferences("stats", MODE_PRIVATE).getInt(key, 0) + 1);
        editor.putInt("total_count", getSharedPreferences("stats", MODE_PRIVATE).getInt("total_count", 0) + 1);
        editor.apply();
    }

    private void initBoard(boolean isBlackOnTop) {
        this.board = new Board(isBlackOnTop);
    }

    public Field getSelectedField() {
        return selectedField;
    }

    public void moveFigure(int x, int y) {
        checkMoveChangesBoard(selectedField.getX(), selectedField.getY(), x, y);

        boolean isPawnPromotionAvailable = board.moveFigureAndCheckPromotion(selectedField.getX(), selectedField.getY(), x, y);
        if (isPawnPromotionAvailable) {
            promotePawn();
            return;
        }

        handleMove(false);
    }

    private void checkMoveChangesBoard(int fromX, int fromY, int toX, int toY) {
        if (board.getFields()[fromX][fromY].getFigure().getType() == FigureType.PAWN || !board.getFields()[toX][toY].isEmpty()) {
            gameState.resetMovesNotChanging();
        } else {
            gameState.increaseMovesNotChanging();
        }
    }

    private void promotePawn() {
        Pair<Integer, Integer> pawnToPromote = board.findPawnToPromote(gameState.getCurrentPlayer());

        showPromotionDialog(pawnToPromote);
    }

    private void showPromotionDialog(Pair<Integer, Integer> pawnToPromote) {
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.promotion_dialog_view, null);

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.promote_pawn_title)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    handlePawnPromotion(pawnToPromote);
                    dialog.dismiss();
                })
                .create();

        alertDialog.show();

        Button okButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setEnabled(false);

        List<Integer> promotionViews = Arrays.asList(R.id.promoteRook, R.id.promoteBishop,
                R.id.promoteQueen, R.id.promoteKnight);

        setListenerForPromotionButton(alertDialog, okButton, R.id.promoteBishop, FigureType.BISHOP, promotionViews);

        setListenerForPromotionButton(alertDialog, okButton, R.id.promoteKnight, FigureType.KNIGHT, promotionViews);

        setListenerForPromotionButton(alertDialog, okButton, R.id.promoteRook, FigureType.ROOK, promotionViews);

        setListenerForPromotionButton(alertDialog, okButton, R.id.promoteQueen, FigureType.QUEEN, promotionViews);
    }

    private void handlePawnPromotion(Pair<Integer, Integer> pawnToPromote) {
        board.promotePawn(pawnToPromote, selectedPromotion);
        selectedPromotion = null;
        handleMove(false);
    }

    private void setListenerForPromotionButton(AlertDialog alertDialog, Button okButton, int viewId, FigureType figureType, List<Integer> promotionViews) {
        View button = alertDialog.findViewById(viewId);
        button.setOnClickListener(v -> {
            v.setBackgroundColor(getResources().getColor(R.color.colorSelected));
            selectedPromotion = figureType;
            okButton.setEnabled(true);

            for (Integer promotionView : promotionViews) {
                if (!promotionView.equals(viewId)) {
                    ImageView view = v.getRootView().findViewById(promotionView);
                    view.setBackgroundColor(getResources().getColor(R.color.promotionButtonColor));
                }
            }
        });
    }

    @SneakyThrows
    private void makeCPUMove() {
        cpuThread = new Thread(new CPUMove(new CPUPlayer(cpuColor, gameOptions.getDifficulty(), this, board)));
        cpuThread.start();
    }

    public void handleCPUMove(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move, FigureType promotion, String boardString) {
        if (gameState.getCurrentPlayer() != cpuColor || !boardString.equals(board.getBoardString())) {
            return;
        }

        if (move == null) {
            if (board.isCheck(gameState.getCurrentPlayer())) {
                finishGame(gameState.getOppositePlayer(), FinishedReason.CHECKMATE);
            } else {
                drawGame(FinishedReason.NO_POSSIBLE_MOVES);
            }
            return;
        }

        Integer fromX = move.first.first;
        Integer fromY = move.first.second;
        Integer toX = move.second.first;
        Integer toY = move.second.second;

        checkMoveChangesBoard(fromX, fromY, toX, toY);

        boolean isPawnPromotionAvailable = board.moveFigureAndCheckPromotion(fromX, fromY, toX, toY);
        if (isPawnPromotionAvailable) {
            promoteCpuPawn(promotion);
        }

        cpuLastMove = move;

        if (gameOptions.getGameTime() > 0) {
            long currentTime = SystemClock.uptimeMillis();
            boolean outOfTime = gameState.decreaseTempCurrentPlayerTime(currentTime - lastTime);

            if (outOfTime) {
                finishGame(gameState.getOppositePlayer(), FinishedReason.NO_TIME);
            } else {
                lastTime = currentTime;
            }
        }

        handleMove(true);
    }

    private void promoteCpuPawn(FigureType selectedPromotion) {
        Pair<Integer, Integer> pawnToPromote = board.findPawnToPromote(cpuColor);
        board.promotePawn(pawnToPromote, selectedPromotion == FigureType.EMPTY ? FigureType.QUEEN : selectedPromotion);
    }

    private void handleMove(boolean isCpuMove) {
        selectedField = null;
        gameState.nextPlayer();

        int possibleMovesCount = generatePossibleMoves();

        if (possibleMovesCount == 0) {
            if (board.isCheck(gameState.getCurrentPlayer())) {
                finishGame(gameState.getOppositePlayer(), FinishedReason.CHECKMATE);
                runOnUiThread(() -> gameView.invalidate());
                return;
            } else {
                drawGame(FinishedReason.NO_POSSIBLE_MOVES);
                return;
            }
        }

        boolean stateRepeatedThreeTimes = saveGameState(isCpuMove);
        if (stateRepeatedThreeTimes) {
            drawGame(FinishedReason.REPEATED_3_TIMES);
            return;
        }

        if (gameState.getMovesNotChanging() == 50) {
            drawGame(FinishedReason.MOVES_50);
            return;
        }

        if (!board.checkMaterialIsSufficient()) {
            drawGame(FinishedReason.NO_MATERIAL);
            return;
        }

        runOnUiThread(() -> gameView.invalidate());
        if (gameOptions.getGameMode() == GameMode.SINGLE && gameState.getCurrentPlayer() == cpuColor) {
            makeCPUMove();
        }
    }

    private void drawGame(FinishedReason finishedReason) {
        finishGame(FigureColor.NONE, finishedReason);
        gameView.invalidate();
    }

    private int generatePossibleMoves() {
        return board.generatePossibleMoves(gameState.getCurrentPlayer());
    }

    public void handleTouch(Field field) {
        if (gameFinished || field == null) {
            return;
        }

        hintMove = null;
        cpuLastMove = null;

        if (field.getFigure().getColor() == gameState.getCurrentPlayer() && (gameOptions.getGameMode() == GameMode.MULTI || field.getFigure().getColor() != cpuColor)) {
            selectedField = field;
        } else if (selectedField != null) {
            if (possibleMove(field)) {
                moveFigure(field.getX(), field.getY());
            }
        }

        gameView.invalidate();
    }

    private boolean possibleMove(Field field) {
        for (Pair<Integer, Integer> pair : selectedField.getFigure().getPossibleMoves()) {
            if (pair.first == field.getX() && pair.second == field.getY()) {
                return true;
            }
        }

        return false;
    }

    public String getBlackPlayerTime() {
        return Utils.timeToString(gameState.getTempBlackPlayerTimeLeft());
    }

    public String getWhitePlayerTime() {
        return Utils.timeToString(gameState.getTempWhitePlayerTimeLeft());
    }

    public GameOptions getGameOptions() {
        return gameOptions;
    }

    public void redo() {
        if (stateHistory.isRedoPossible()) {
            restoreState(stateHistory.redo());
            handleUndoRedo();
        }
    }

    public void undo() {
        if (stateHistory.isUndoPossible()) {
            restoreState(stateHistory.undo());
            handleUndoRedo();
        }
    }

    private void handleUndoRedo() {
        if (cpuThread != null) {
            cpuThread.interrupt();
        }

        generatePossibleMoves();
        saveGameStateWithoutHistory();
        cpuLastMove = null;
        selectedField = null;
        hintMove = null;
        gameView.invalidate();
    }

    public void returnToMainMenu() {
        finish();
    }

    public void startNewGame() {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.new_game_title)
                .setMessage(R.string.new_game_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    handleStartNewGame();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .create();

        alertDialog.show();
    }

    private void handleStartNewGame() {
        GameColor player1Color = initPlayerColor();

        getSharedPreferences("game", MODE_PRIVATE).edit().putString("board", "").apply();
        initBoard(player1Color == GameColor.WHITE);
        gameState = new GameState(FigureColor.WHITE, gameOptions.getGameTime(), gameOptions.getGameTime(), gameOptions.getMoveTime());

        initGame(player1Color);

        cpuLastMove = null;
        gameFinished = false;
        gameView.reset();
        gameView.invalidate();
    }

    public void hint() {
        if (gameOptions.getGameMode() == GameMode.SINGLE && isHintEnabled()) {
            hintInProgress = true;
            new Thread(new CPUHint(new HintPlayer(gameState.getCurrentPlayer(), this, board))).run();
        }
    }

    public boolean isHintEnabled() {
        return gameState.getCurrentPlayer() != cpuColor;
    }

    public void handleHint(Pair<Pair<Integer, Integer>, Pair<Integer, Integer>> move, String boardString) {
        hintInProgress = false;
        if (gameState.getCurrentPlayer() == cpuColor || !boardString.equals(board.getBoardString())) {
            return;
        }

        hintMove = move;
        runOnUiThread(() -> gameView.invalidate());
    }
}
