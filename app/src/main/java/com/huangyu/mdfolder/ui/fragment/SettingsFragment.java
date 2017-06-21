package com.huangyu.mdfolder.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.ui.activity.SettingsActivity;
import com.huangyu.mdfolder.utils.SPUtils;

/**
 * Created by huangyu on 2017-6-19.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);
        final Preference prefTheme = findPreference("pref_theme");
        prefTheme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isLight = (Boolean) newValue;
                if (isLight) {
                    prefTheme.setSummary(getString(R.string.pref_light));
                } else {
                    prefTheme.setSummary(getString(R.string.pref_dark));
                }
                ((SettingsActivity) getActivity()).recreateActivity();
                return true;
            }
        });

        if (((SettingsActivity) getActivity()).isLightMode()) {
            prefTheme.setSummary(getString(R.string.pref_light));
        } else {
            prefTheme.setSummary(getString(R.string.pref_dark));
        }

        final Preference prefShowHide = findPreference("pref_show_hidden");
        prefShowHide.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isShow = (Boolean) newValue;
                if (isShow) {
                    prefShowHide.setSummary(getString(R.string.pref_show_hidden));
                } else {
                    prefShowHide.setSummary(getString(R.string.pref_hide_hidden));
                }
                return true;
            }
        });

        if (SPUtils.isShowHiddenFiles()) {
            prefShowHide.setSummary(getString(R.string.pref_show_hidden));
        } else {
            prefShowHide.setSummary(getString(R.string.pref_hide_hidden));
        }

        final Preference prefAbout = findPreference("pref_about");
        prefAbout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse(prefAbout.getSummary().toString()));
                startActivity(intent);
                return false;
            }
        });
    }

}
