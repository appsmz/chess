package com.mz.chess.menu;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.mz.chess.R;

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);

        Preference about = findPreference("aboutKey");
        about.setOnPreferenceClickListener(preference -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            PackageInfo pInfo = null;
            try {
                pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            } catch (NameNotFoundException e) {
                pInfo = new PackageInfo();
                pInfo.versionName = "";
            }
            builder.setMessage("Chess \nVersion: " + pInfo.versionName + "\nDeveloped by: appsmz \nEmail: appsmz@gmail.com")
                    .setTitle("About").setCancelable(false).setPositiveButton("Close", (dialog, id) -> {
                        // do nothing
                    });
            AlertDialog alert = builder.create();
            alert.show();
            return true;
        });

        Preference rate = findPreference("rateKey");
        rate.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.mz.chess"));
            startActivity(intent);
            return true;
        });

        Preference other = findPreference("otherKey");
        other.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=appsmz"));
            if (isIntentAvailable(getActivity(), intent))
                startActivity(intent);
            return true;
        });

        Preference privacyPolicy = findPreference("privacyPolicy");
        privacyPolicy.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity(), PrivacyPolicyActivity.class);
            startActivity(intent);
            return true;
        });

    }

    public boolean isIntentAvailable(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}
