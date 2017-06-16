package com.huangyu.mdfolder.ui.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.huangyu.mdfolder.R;

public class SettingsActivity extends ThematicSettingsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        addPreferencesFromResource(R.xml.pref_general);

        final SwitchPreference prefTheme = (SwitchPreference) findPreference("pref_theme");
        prefTheme.setOnPreferenceChangeListener(new SwitchPreference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isLight = (Boolean) newValue;
                if (isLight) {
                    prefTheme.setSummary(getString(R.string.pref_light));
                } else {
                    prefTheme.setSummary(getString(R.string.pref_dark));
                }
                recreate();
                return true;
            }
        });

        if (isLightMode()) {
            prefTheme.setSummary(getString(R.string.pref_light));
        } else {
            prefTheme.setSummary(getString(R.string.pref_dark));
        }

        final Preference prefAbout = findPreference("pref_about");
        prefAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(prefAbout.getSummary().toString()));
                startActivity(intent);
                return true;
            }
        });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
