package com.islamic.prayer_times.Fragments;

import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.islamic.prayer_times.R;
import com.islamic.prayer_times.activities.SettingsActivity;

public   class SettingsPreferencesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);
        Preference pref = findPreference(getString(R.string.settings_general_key));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingsActivity)getActivity()).openFragment(new GeneralPreferenceFragment());
                return true;
            }
        });
        pref = findPreference(getString(R.string.settings_location_key));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingsActivity)getActivity()).openFragment(new LocationsPreferenceFragment());
                return true;
            }
        });
        pref = findPreference(getString(R.string.settings_notifications_key));
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ((SettingsActivity)getActivity()).openFragment(new NotificationPreferenceFragment());
                return true;
            }
        });



    }

}

