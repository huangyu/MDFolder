package com.huangyu.mdfolder.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.huangyu.mdfolder.R;
import com.huangyu.mdfolder.ui.activity.SettingsActivity;
import com.huangyu.mdfolder.utils.AlertUtils;
import com.huangyu.mdfolder.utils.KeyboardUtils;
import com.huangyu.mdfolder.utils.LanguageUtils;
import com.huangyu.mdfolder.utils.SPUtils;
import com.huangyu.mdfolder.utils.scan.ScanTask;

import java.util.Locale;

import butterknife.ButterKnife;

/**
 * Created by huangyu on 2017-6-19.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements ScanTask.ScanCallBack {

    private ProgressDialog mProgressDialog;
    private ScanTask mScanTask;

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

        final Preference prefShowAppMode = findPreference("pref_show_apps_mode");
        prefShowAppMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean isShowSystem = (Boolean) newValue;
                if (isShowSystem) {
                    prefShowAppMode.setSummary(getString(R.string.pref_show_all));
                } else {
                    prefShowAppMode.setSummary(getString(R.string.pref_not_show_system));
                }
                return true;
            }
        });

        if (SPUtils.isShowAllApps()) {
            prefShowAppMode.setSummary(getString(R.string.pref_show_all));
        } else {
            prefShowAppMode.setSummary(getString(R.string.pref_not_show_system));
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

        final Preference prefScan = findPreference("pref_scan");
        prefScan.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_scan, new LinearLayout(getContext()), false);
                final AppCompatEditText aetSize = ButterKnife.findById(view, R.id.et_size);
                aetSize.setText("100");
                aetSize.setSelection(0, aetSize.getText().length());
                getActivity().getWindow().getDecorView().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        KeyboardUtils.showSoftInput(aetSize);
                    }
                }, 200);
                AlertUtils.showCustomAlert(getContext(), "", view, new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialog) {
                        Button positionButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        positionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String size = aetSize.getText().toString();
                                if (TextUtils.isEmpty(size)) {
                                    AlertUtils.showToast(getContext(), getString(R.string.tips_file_name_empty));
                                    return;
                                }
                                scanAllFile(Integer.valueOf(size));
                                dialog.dismiss();
                            }
                        });
                        Button negativeButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        negativeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                return false;
            }
        });

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

    @Override
    public void onDialogShow() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle(getString(R.string.tips_calculating));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mScanTask != null) {
                    mScanTask.cancel(true);
                }
            }
        });
        mProgressDialog.show();
    }

    @Override
    public void onProgressStart(final int total) {
        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                mProgressDialog = new ProgressDialog(getActivity());
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setTitle(getString(R.string.tips_scanning));
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setCancelable(true);
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mScanTask != null) {
                            mScanTask.cancel(true);
                        }
                    }
                });
                mProgressDialog.setMax(total);
                mProgressDialog.show();
            }
        });
    }

    @Override
    public void onProgressUpdate(Integer progress, int total) {
        mProgressDialog.setProgress(progress);
    }

    @Override
    public void onDialogDismiss() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        if (isAdded() && isVisible()) {
            AlertUtils.showToast(getContext(), getString(R.string.tips_scan_complete));
        }
    }

    /**
     * 每次全盘扫描一次sd卡文件，防止部分第三方发送的文件信息件没能更快被写入媒体数据库
     */
    private void scanAllFile(int fileSize) {
        mScanTask = new ScanTask(this, fileSize);
        mScanTask.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        if (mScanTask != null) {
            mScanTask.cancel(true);
        }
        super.onDestroy();
    }

}
