package com.mz.chess.menu;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;

import com.mz.chess.R;

public class MoreGamesActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.more);

        View v = findViewById(R.id.more_main_layout);
        v.setOnClickListener(v1 -> openGP());
    }

    private void openGP() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=mz.sudoku.classic"));
        startActivity(intent);
    }
}
