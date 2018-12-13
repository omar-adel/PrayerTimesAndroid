package com.islamic.prayer_times.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.islamic.prayer_times.R;
import com.islamic.prayer_times.activities.MainActivity;
import com.islamic.prayer_times.helpers.UserSettings;

import timber.log.Timber;

import static com.islamic.prayer_times.activities.SettingsActivity.setListPrefSummary;

public   class GeneralPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_general);

        // Set language summary to current user setting
        Preference pref = findPreference(getString(R.string.general_language_key));
        String language = UserSettings.getPrefLanguage(pref.getContext());
        setListPrefSummary(pref, language);

        // bind it to change listener
        pref.setOnPreferenceChangeListener(sGeneralPrefsListener);


        // Set numerals summary to current user setting
        pref = findPreference(getString(R.string.general_numerals_key));
        setListPrefSummary(pref, UserSettings.getNumerals(pref.getContext()));

        // bind it to change listener
        pref.setOnPreferenceChangeListener(sGeneralPrefsListener);
        // Numerals pref. available only when language is arabic.
        if (UserSettings.languageIsArabic(getActivity(), language)) {
            pref.setEnabled(true);
        } else {
            pref.setEnabled(false);
        }
    }


    private final /*static*/ Preference.OnPreferenceChangeListener sGeneralPrefsListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();
                    String key = preference.getKey();
                    Context context = preference.getContext();
                     if(key.equals(getString(R.string.general_language_key)))
                     {
                         // Set the summary to reflect the new value.
                         setListPrefSummary(preference, stringValue);

                         // Change locale?
                         if (!stringValue.equals(UserSettings.getPrefLanguage(context))) {
                             Timber.d("New language: " + stringValue);
                             UserSettings.setLocale(context, stringValue, null);
                             // numerals pref. only ON for arabic.
                             Preference numeralsPref = findPreference(getString(R.string.general_numerals_key));
                             if (UserSettings.languageIsArabic(getActivity(), stringValue)) {
                                 numeralsPref.setEnabled(true);
                             }
                             else {
                                 numeralsPref.setEnabled(false);
                             }

                             refreshUI(context);
                         }
                     }
                     else
                     if(key.equals(getString(R.string.general_numerals_key)))
                     {
                         // Set the summary to reflect the new value.
                         setListPrefSummary(preference, stringValue);

                         // Change locale?
                         if (!stringValue.equals(UserSettings.getNumerals(context))) {
                             Timber.d("New numerals: " + stringValue);
                             UserSettings.setLocale(context, null, stringValue);
                             refreshUI(context);
                         }
                     }


                    return true;
                }

                private void refreshUI(Context context) {
                    // refresh UI (framework part) with new Locale
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            };
}

