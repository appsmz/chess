package com.mz.chess;

import android.app.Application;
import android.graphics.Typeface;

public class ChessApplication extends Application {

    private Typeface typeface;

    @Override
    public void onCreate() {
        super.onCreate();

        this.typeface = Typeface.createFromAsset(getAssets(), "fonts/pistara.otf");
    }

    public Typeface getTypeface() {
        return typeface;
    }
}
