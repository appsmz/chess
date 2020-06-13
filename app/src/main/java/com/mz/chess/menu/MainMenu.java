package com.mz.chess.menu;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mz.chess.ChessApplication;
import com.mz.chess.R;
import com.mz.chess.game.Game;

import androidx.annotation.Nullable;

public class MainMenu extends Activity {

    public boolean continueButton;
    private Button newGameButton;
    private Button resumeGameButton;
    private Button moreGamesButton;
    private ImageButton settingsButton;
    private ImageButton themeButton;
    private TextView mainTitleTV;
    private View mainLayout;
    private ChessApplication application;

    static {
        System.loadLibrary("stockfish");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = (ChessApplication) getApplication();

        this.continueButton = readContinueButton();

        setContentView(R.layout.main_menu);

        init();

        initTheme();
    }

    private boolean readContinueButton() {
        return !getSharedPreferences("game", MODE_PRIVATE).getString("board", "").equals("");
    }

    private void init() {
        newGameButton = findViewById(R.id.mm_new_game);
        newGameButton.setOnClickListener(v -> startActivity(NewGameActivity.class));
        newGameButton.setTypeface(application.getTypeface(), Typeface.NORMAL);
        newGameButton.setLetterSpacing(0.05f);

        resumeGameButton = findViewById(R.id.mm_resume);
        resumeGameButton.setTypeface(application.getTypeface(), Typeface.NORMAL);
        resumeGameButton.setLetterSpacing(0.05f);
        if (continueButton) {
            resumeGameButton.setOnClickListener(v -> resumeGame());
        } else {
            resumeGameButton.setClickable(false);
        }

        moreGamesButton = findViewById(R.id.mm_more_games);
        moreGamesButton.setOnClickListener(v -> startActivity(MoreGamesActivity.class));
        moreGamesButton.setTypeface(application.getTypeface(), Typeface.NORMAL);
        moreGamesButton.setLetterSpacing(0.05f);

        settingsButton = findViewById(R.id.mm_settings);
        settingsButton.setOnClickListener(v -> startActivity(PreferencesActivity.class));

        themeButton = findViewById(R.id.mm_theme);
        themeButton.setOnClickListener(v -> changeTheme());

        mainLayout = findViewById(R.id.main_layout);
        mainTitleTV = findViewById(R.id.mainTitleTV);
        mainTitleTV.setTypeface(application.getTypeface(), Typeface.NORMAL);
        mainTitleTV.setLetterSpacing(0.03f);
    }

    private void initTheme() {
        boolean isDarkTheme = getSharedPreferences("theme", MODE_PRIVATE).getBoolean("isDarkTheme", false);
        if (isDarkTheme) {
            applyDarkTheme();
        } else {
            applyThemeDefault();
        }
    }

    private void applyThemeDefault() {
        mainLayout.setBackgroundResource(R.drawable.bg_gradient);
        mainTitleTV.setTextColor(Color.BLACK);

        newGameButton.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        newGameButton.setTextColor(Color.WHITE);

        resumeGameButton.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        resumeGameButton.setTextColor(Color.WHITE);
        resumeGameButton.setAlpha(continueButton ? 1 : 0.5f);

        moreGamesButton.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
        moreGamesButton.setTextColor(Color.WHITE);

        settingsButton.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        settingsButton.setImageResource(R.drawable.settings);

        themeButton.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        themeButton.setImageResource(R.drawable.theme_default_active);
    }

    private void applyDarkTheme() {
//        mainLayout.setBackgroundColor(getResources().getColor(R.color.darkBackground));
        mainLayout.setBackgroundResource(R.drawable.bg_gradient_dark);
        mainTitleTV.setTextColor(getResources().getColor(R.color.darkTextColor));

        newGameButton.getBackground().setColorFilter(getResources().getColor(R.color.colorAccentDark), PorterDuff.Mode.MULTIPLY);
        newGameButton.setTextColor(getResources().getColor(R.color.darkTextColor));

        resumeGameButton.getBackground().setColorFilter(getResources().getColor(R.color.colorAccentDark), PorterDuff.Mode.MULTIPLY);
        resumeGameButton.setTextColor(getResources().getColor(R.color.darkTextColor));
        resumeGameButton.setAlpha(continueButton ? 1 : 0.5f);

        moreGamesButton.getBackground().setColorFilter(getResources().getColor(R.color.colorAccentDark), PorterDuff.Mode.MULTIPLY);
        moreGamesButton.setTextColor(getResources().getColor(R.color.darkTextColor));

        settingsButton.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        settingsButton.setImageResource(R.drawable.settings_dark);

        themeButton.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.MULTIPLY);
        themeButton.setImageResource(R.drawable.theme_dark_active);
    }

    private void changeTheme() {
        SharedPreferences preferences = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean("isDarkTheme", false);
        preferences.edit().putBoolean("isDarkTheme", !isDarkTheme).apply();
        initTheme();
    }

    private void resumeGame() {
        Intent intent = new Intent(MainMenu.this, Game.class);
        intent.putExtra(Game.IS_CONTINUE_KEY, true);
        startActivity(intent);
    }

    private void startActivity(Class clazz) {
        Intent intent = new Intent(MainMenu.this, clazz);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.continueButton = readContinueButton();
        if (continueButton) {
            resumeGameButton.setOnClickListener(v -> resumeGame());
        } else {
            resumeGameButton.setClickable(false);
        }
        resumeGameButton.setAlpha(continueButton ? 1 : 0.5f);
    }
}
