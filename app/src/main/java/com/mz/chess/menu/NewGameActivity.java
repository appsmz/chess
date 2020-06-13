package com.mz.chess.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mz.chess.ChessApplication;
import com.mz.chess.R;
import com.mz.chess.game.Game;

import java.util.Arrays;
import java.util.List;

public class NewGameActivity extends Activity {

    private GameMode gameMode = GameMode.SINGLE;
    private int difficulty = 1;
    private GameColor gameColor = GameColor.WHITE;
    private int gameTime = 0;
    private int moveTime = 0;
    private TextView difficultyTV;
    private TextView gameTimerTV;
    private TextView moveTimerTV;
    private TextView singleMultiTV;
    private TextView cpuDiffTitleTV;
    private TextView playerColorTV;
    private TextView gameTimerLabelTV;
    private TextView moveTimerLabelTV;
    private Button startButton;
    private ImageButton singlePlayer;
    private ImageButton multiPlayer;
    private TextView mainTV;
    private View mainLayout;
    private Button diffPlus;
    private Button diffMinus;
    private Button moveTimePlus;
    private Button moveTimeMinus;
    private ImageButton changing;
    private ImageButton white;
    private ImageButton black;
    private Button gameTimerPlus;
    private Button gameTimerMinus;
    private int normalColor;
    private int selectedColor;
    private int textViewColor;
    private int buttonTextColor;
    private int accentColor;
    private ChessApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (ChessApplication) getApplication();

        setContentView(R.layout.new_game_menu);

        initLayoutHeight();

        init();

        setListeners();

        initTextViews();

        initTheme();

        readLastOptions();
    }

    private void initLayoutHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int buttonSize = (int) (displayMetrics.widthPixels * 0.15);

        List<Integer> layouts = Arrays.asList(R.id.singleMultiLayout, R.id.diffLayout, R.id.playerColorLayout, R.id.gameTimerLayout,
                R.id.moveTimerLayout, R.id.diffValueLayout, R.id.moveTimerValueLayout, R.id.gameTimerValueLayout);

        for (Integer layoutId : layouts) {
            LinearLayout layout = findViewById(layoutId);
            android.view.ViewGroup.LayoutParams params = layout.getLayoutParams();
            params.height = buttonSize;
            layout.setLayoutParams(params);
        }
    }

    private void readLastOptions() {
        SharedPreferences sp = getSharedPreferences("newGameSettings", MODE_PRIVATE);

        String gameModeString = sp.getString(Game.NEW_GAME_MODE, GameMode.SINGLE.name());
        gameMode = GameMode.valueOf(gameModeString);
        if (gameMode == GameMode.SINGLE) {
            singlePlayer.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            setDifficultyClickable();
        } else {
            multiPlayer.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            setDifficultyNotClickable();
        }

        difficulty = sp.getInt(Game.NEW_GAME_DIFF, 2);
        setDiffText();

        String gameColorString = sp.getString(Game.NEW_GAME_COLOR, GameColor.WHITE.name());
        gameColor = GameColor.valueOf(gameColorString);
        if (gameColor == GameColor.WHITE) {
            white.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
        } else if (gameColor == GameColor.BLACK) {
            black.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
        } else {
            changing.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
        }

        gameTime = sp.getInt(Game.NEW_GAME_TIMER, 0);
        checkGameTimer();

        moveTime = sp.getInt(Game.NEW_GAME_MOVE_TIMER, 0);
        checkMoveTimer();
    }

    private void init() {
        mainTV = findViewById(R.id.ng_title_tv);
        mainLayout = findViewById(R.id.ng_main_layout);

        startButton = findViewById(R.id.ng_start_button);
        startButton.setOnClickListener(v -> startNewGame());

        diffPlus = findViewById(R.id.ng_cpu_diff_plus);
        diffMinus = findViewById(R.id.ng_cpu_diff_minus);

        moveTimePlus = findViewById(R.id.ng_move_timer_plus);
        moveTimeMinus = findViewById(R.id.ng_move_timer_minus);

        difficultyTV = findViewById(R.id.ng_diff_tv);
        gameTimerTV = findViewById(R.id.ng_game_timer_tv);
        moveTimerTV = findViewById(R.id.ng_move_time_tv);

        singlePlayer = findViewById(R.id.ng_single_player);
        multiPlayer = findViewById(R.id.ng_multi_player);

        changing = findViewById(R.id.ng_color_changing);
        white = findViewById(R.id.ng_color_white);
        black = findViewById(R.id.ng_color_black);
        gameTimerPlus = findViewById(R.id.ng_game_timer_plus);
        gameTimerMinus = findViewById(R.id.ng_game_timer_minus);

        moveTimePlus = findViewById(R.id.ng_move_timer_plus);
        moveTimeMinus = findViewById(R.id.ng_move_timer_minus);

        singleMultiTV = findViewById(R.id.ng_single_multi_tv);
        cpuDiffTitleTV = findViewById(R.id.ng_cpu_difficulty);
        playerColorTV = findViewById(R.id.ng_player_color);
        gameTimerLabelTV = findViewById(R.id.ng_game_timer_txt);
        moveTimerLabelTV = findViewById(R.id.ng_move_timer_txt);

        initTypeface();
    }

    private void initTypeface() {
        List<TextView> views = Arrays.asList(startButton, mainTV, gameTimerTV, moveTimerTV, singleMultiTV, cpuDiffTitleTV, playerColorTV, gameTimerLabelTV, moveTimerLabelTV, difficultyTV);

        for (TextView view : views) {
            view.setTypeface(application.getTypeface(), Typeface.NORMAL);
            view.setLetterSpacing(0.04f);
        }
    }

    private void initTextViews() {
        setDiffText();

        if (gameTime == 0) {
            gameTimerTV.setText(R.string.no_timer);
        } else {
            gameTimerTV.setText(gameTime + " min");
        }

        if (moveTime == 0) {
            moveTimerTV.setText(R.string.no_timer);
        } else {
            moveTimerTV.setText(moveTime + " min");
        }

    }

    private void setListeners() {
        setListenersForSingleMulti();

        setListenersForDifficulty();

        setListenersForColor();

        setListenersForGameTimer();

        setListenersForMoveTimer();
    }

    private void startNewGame() {
        SharedPreferences.Editor editor = getSharedPreferences("newGameSettings", MODE_PRIVATE).edit();
        editor.putString(Game.NEW_GAME_MODE, gameMode.name());
        editor.putInt(Game.NEW_GAME_DIFF, difficulty);
        editor.putString(Game.NEW_GAME_COLOR, gameColor.name());
        editor.putInt(Game.NEW_GAME_TIMER, gameTime);
        editor.putInt(Game.NEW_GAME_MOVE_TIMER, moveTime);
        editor.putBoolean(Game.NEW_GAME_STARTED, false);
        editor.commit();

        Intent intent = new Intent(NewGameActivity.this, Game.class);
        startActivity(intent);
        finish();
    }

    private void setListenersForSingleMulti() {
        singlePlayer.setOnClickListener(v -> {
            singlePlayer.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            multiPlayer.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            setDifficultyClickable();
            gameMode = GameMode.SINGLE;
        });

        multiPlayer.setOnClickListener(v -> {
            singlePlayer.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            multiPlayer.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            setDifficultyNotClickable();
            gameMode = GameMode.MULTI;
        });
    }

    private void setDifficultyClickable() {
        diffPlus.setClickable(true);
        diffPlus.setAlpha(1);
        diffMinus.setClickable(true);
        diffMinus.setAlpha(1);

        difficultyTV.setAlpha(1);
    }

    private void setDifficultyNotClickable() {
        diffPlus.setClickable(false);
        diffPlus.setAlpha(0.5f);
        diffMinus.setClickable(false);
        diffMinus.setAlpha(0.5f);

        difficultyTV.setAlpha(0.5f);
    }

    private void setMoveTimeButtonClickable() {
        moveTimePlus.setClickable(true);
        moveTimePlus.setAlpha(1);
        moveTimeMinus.setClickable(true);
        moveTimeMinus.setAlpha(1);

        moveTimerTV.setAlpha(1);
    }

    private void setMoveTimeButtonNotClickable() {
        moveTimePlus.setClickable(false);
        moveTimePlus.setAlpha(0.5f);
        moveTimeMinus.setClickable(false);
        moveTimeMinus.setAlpha(0.5f);

        moveTimerTV.setAlpha(0.5f);
    }

    private void setListenersForDifficulty() {
        diffPlus.setOnClickListener(v -> {
            if (difficulty < 13) {
                difficulty++;
                setDiffText();
            }
        });

        diffMinus.setOnClickListener(v -> {
            if (difficulty > 0) {
                difficulty--;
                setDiffText();
            }
        });
    }

    private void setDiffText() {
        difficultyTV.setText(String.valueOf(difficulty));
    }

    private void setListenersForColor() {
        switch (gameColor) {
            case CHANGING:
                changing.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
                break;
            case WHITE:
                white.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
                break;
            case BLACK:
                black.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
                break;
        }

        changing.setOnClickListener(v -> {
            changing.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            white.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            black.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            gameColor = GameColor.CHANGING;
        });

        white.setOnClickListener(v -> {
            changing.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            white.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            black.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            gameColor = GameColor.WHITE;
        });

        black.setOnClickListener(v -> {
            changing.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            white.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
            black.getBackground().setColorFilter(selectedColor, PorterDuff.Mode.MULTIPLY);
            gameColor = GameColor.BLACK;
        });
    }

    private void setListenersForGameTimer() {
        gameTimerPlus.setOnClickListener(v -> {

            switch (gameTime) {
                case 0:
                    gameTime = 5;
                    break;
                case 5:
                    gameTime = 10;
                    break;
                case 10:
                    gameTime = 15;
                    break;
                case 15:
                    gameTime = 30;
                    break;
                case 30:
                    gameTime = 60;
                    break;
            }

            gameTimerTV.setText(gameTime + " min");
            setMoveTimeButtonClickable();
        });

        gameTimerMinus.setOnClickListener(v -> {
            switch (gameTime) {
                case 5:
                    gameTime = 0;
                    break;
                case 10:
                    gameTime = 5;
                    break;
                case 15:
                    gameTime = 10;
                    break;
                case 30:
                    gameTime = 15;
                    break;
                case 60:
                    gameTime = 30;
                    break;
            }

            checkGameTimer();
        });
    }

    private void checkGameTimer() {
        if (gameTime == 0) {
            gameTimerTV.setText(R.string.no_timer);
            setMoveTimeButtonNotClickable();
        } else {
            gameTimerTV.setText(gameTime + " min");
            setMoveTimeButtonClickable();
        }
    }


    private void setListenersForMoveTimer() {
        moveTimePlus.setOnClickListener(v -> {

            switch (moveTime) {
                case 0:
                    moveTime = 5;
                    break;
                case 5:
                    moveTime = 10;
                    break;
                case 10:
                    moveTime = 15;
                    break;
                case 15:
                    moveTime = 30;
                    break;
                case 30:
                    moveTime = 60;
                    break;
            }

            moveTimerTV.setText(moveTime + " s");
        });

        moveTimeMinus.setOnClickListener(v -> {
            switch (moveTime) {
                case 5:
                    moveTime = 0;
                    break;
                case 10:
                    moveTime = 5;
                    break;
                case 15:
                    moveTime = 10;
                    break;
                case 30:
                    moveTime = 15;
                    break;
                case 60:
                    moveTime = 30;
                    break;
            }

            checkMoveTimer();
        });
    }

    private void checkMoveTimer() {
        if (moveTime == 0) {
            moveTimerTV.setText(R.string.no_timer);
        } else {
            moveTimerTV.setText(moveTime + " s");
        }
    }

    private void initTheme() {
        boolean isDarkTheme = getSharedPreferences("theme", MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            setDarkTheme();
            mainLayout.setBackgroundResource(R.drawable.bg_gradient_dark);
        } else {
            setThemeDefault();
            mainLayout.setBackgroundResource(R.drawable.bg_gradient);
        }
        applyTheme();
    }

    private void setDarkTheme() {
        normalColor = getResources().getColor(R.color.colorPrimaryDark);
        selectedColor = getResources().getColor(R.color.colorSelectedDark);
        textViewColor = getResources().getColor(R.color.darkTextColor);
        buttonTextColor = getResources().getColor(R.color.darkTextColor);
        accentColor = getResources().getColor(R.color.colorAccentDark);
    }

    private void setThemeDefault() {
        normalColor = getResources().getColor(R.color.colorPrimary);
        selectedColor = getResources().getColor(R.color.colorSelected);
        textViewColor = Color.BLACK;
        buttonTextColor = Color.WHITE;
        accentColor = getResources().getColor(R.color.colorAccent);
    }

    private void applyTheme() {
        mainTV.setTextColor(textViewColor);

        difficultyTV.setTextColor(textViewColor);
        gameTimerTV.setTextColor(textViewColor);
        moveTimerTV.setTextColor(textViewColor);
        singleMultiTV.setTextColor(textViewColor);
        cpuDiffTitleTV.setTextColor(textViewColor);
        playerColorTV.setTextColor(textViewColor);
        gameTimerLabelTV.setTextColor(textViewColor);
        moveTimerLabelTV.setTextColor(textViewColor);
        startButton.getBackground().setColorFilter(accentColor, PorterDuff.Mode.MULTIPLY);
        startButton.setTextColor(buttonTextColor);
        singlePlayer.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        multiPlayer.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        diffPlus.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        diffPlus.setTextColor(buttonTextColor);
        diffMinus.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        diffMinus.setTextColor(buttonTextColor);
        moveTimePlus.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        moveTimePlus.setTextColor(buttonTextColor);
        moveTimeMinus.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        moveTimeMinus.setTextColor(buttonTextColor);
        changing.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        white.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        black.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        gameTimerPlus.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        gameTimerPlus.setTextColor(buttonTextColor);
        gameTimerMinus.getBackground().setColorFilter(normalColor, PorterDuff.Mode.MULTIPLY);
        gameTimerMinus.setTextColor(buttonTextColor);
    }
}
