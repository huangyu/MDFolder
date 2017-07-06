package com.huangyu.mdfolder.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.ui.activity.SettingsActivity;
import com.huangyu.mdfolder.utils.LanguageUtils;
import com.huangyu.mdfolder.utils.SPUtils;

import java.util.Locale;

/**
 * Created by huangyu on 2017-6-19.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);
        final ListPreference prefThemes = (ListPreference) findPreference("pref_themes");
        prefThemes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String[] themesArray = getResources().getStringArray(R.array.array_themes);
                String themeMode = (String) newValue;
                prefThemes.setSummary(themesArray[Integer.valueOf(themeMode)]);
                ((SettingsActivity) getActivity()).recreateActivity();
                return true;
            }
        });

        String[] themesArray = getResources().getStringArray(R.array.array_themes);
        String[] themesValueArray = getResources().getStringArray(R.array.array_themes_value);
        String themeMode = ((SettingsActivity) getActivity()).getThemeMode();
        prefThemes.setSummary(themesArray[Integer.valueOf(themeMode)]);
        prefThemes.setValue(themesValueArray[Integer.valueOf(themeMode)]);

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

        final Preference prefOpenMode = findPreference("pref_open_mode");
        prefOpenMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isBuildIn = (Boolean) newValue;
                if (isBuildIn) {
                    prefOpenMode.setSummary(getString(R.string.pref_build_in_mode));
                } else {
                    prefOpenMode.setSummary(getString(R.string.pref_external_mode));
                }
                return true;
            }
        });

        if (SPUtils.isBuildInMode()) {
            prefOpenMode.setSummary(getString(R.string.pref_build_in_mode));
        } else {
            prefOpenMode.setSummary(getString(R.string.pref_external_mode));
        }

        final Preference prefSearchMode = findPreference("pref_search_mode");
        prefSearchMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isGlobally = (Boolean) newValue;
                if (isGlobally) {
                    prefSearchMode.setSummary(getString(R.string.pref_search_global));
                } else {
                    prefSearchMode.setSummary(getString(R.string.pref_search_single));
                }
                return true;
            }
        });

        if (SPUtils.isSearchGlobally()) {
            prefSearchMode.setSummary(getString(R.string.pref_search_global));
        } else {
            prefSearchMode.setSummary(getString(R.string.pref_search_single));
        }

        final ListPreference prefLanguage = (ListPreference) findPreference("pref_language");
        prefLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String[] languageArray = getResources().getStringArray(R.array.array_languages);
                String simplifiedChinese = languageArray[0];
                String english = languageArray[1];
                String[] languageValueArray = getResources().getStringArray(R.array.array_languages_value);
                String simplifiedChineseValue = languageValueArray[0];
                String englishValue = languageValueArray[1];

                if (newValue.equals(simplifiedChineseValue)) {
                    LanguageUtils.changeLanguage(LanguageUtils.SIMPLIFIED_CHINESE);
                    prefLanguage.setSummary(simplifiedChinese);
                } else if (newValue.equals(englishValue)) {
                    LanguageUtils.changeLanguage(LanguageUtils.ENGLISH);
                    prefLanguage.setSummary(english);
                }

                ((SettingsActivity) getActivity()).recreateActivity();
                return true;
            }
        });

        String[] languageArray = getResources().getStringArray(R.array.array_languages);
        String simplifiedChinese = languageArray[0];
        String english = languageArray[1];
        String[] languageValueArray = getResources().getStringArray(R.array.array_languages_value);
        String simplifiedChineseValue = languageValueArray[0];
        String englishValue = languageValueArray[1];

        Locale locale = LanguageUtils.getLocale();
        if (locale.equals(LanguageUtils.SIMPLIFIED_CHINESE)) {
            prefLanguage.setSummary(simplifiedChinese);
            prefLanguage.setValue(simplifiedChineseValue);
        } else if (locale.equals(LanguageUtils.ENGLISH)) {
            prefLanguage.setSummary(english);
            prefLanguage.setValue(englishValue);
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
