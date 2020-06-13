package com.mz.chess.menu;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * User: Marcin
 * Date: 06.10.14
 * Time: 23:11
 */
public class PreferencesActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PreferencesFragment()).commit();

    }
}
