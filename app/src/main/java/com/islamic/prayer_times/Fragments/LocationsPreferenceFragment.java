package com.islamic.prayer_times.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.islamic.prayer_times.PrayerTimesManager;
import com.islamic.prayer_times.R;
import com.islamic.prayer_times.helpers.UserSettings;

import org.arabeyes.prayertime.Method;

import timber.log.Timber;

import static com.islamic.prayer_times.activities.SettingsActivity.REQUEST_SEARCH_CITY;
import static com.islamic.prayer_times.activities.SettingsActivity.setListPrefSummary;

public   class LocationsPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_locations);

        // Set city summary to current user setting
        Preference pref = findPreference(getString(R.string.locations_search_city_key));
        Context context = pref.getContext();
        pref.setSummary(UserSettings.getCityName(context));

        // bind on click listener to start SearchCityActivity
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Timber.d("onPrefClick");
                startActivityForResult(preference.getIntent(), REQUEST_SEARCH_CITY);
                return true;
            }
        });


        // Set method summary to current user setting
        pref = findPreference(getString(R.string.locations_method_key));
        int method = UserSettings.getCalculationMethod(context);
        setListPrefSummary(pref, String.valueOf(method));
        // Bind to onchange listener
        pref.setOnPreferenceChangeListener(sMethodChangeListener);


        // Bind to change listener
        pref = findPreference(getString(R.string.locations_rounding_key));
        pref.setOnPreferenceChangeListener(sMethodChangeListener);


        // Bind mathhab pref to its change listener
        pref = findPreference(getString(R.string.locations_mathhab_hanafi_key));
        pref.setOnPreferenceChangeListener(sMethodChangeListener);
        // Mathhab hanafi pref. only for Karachi method.
        if (method == Method.V2_KARACHI) {
            pref.setEnabled(true);
        } else {
            pref.setEnabled(false);
        }
    }



    private final /*static*/ Preference.OnPreferenceChangeListener sMethodChangeListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();
                    Context context = preference.getContext();
                    if(preference.getKey().equals(getString(R.string.locations_method_key)))
                    {
                        // Set the summary to reflect the new value.
                        int index = setListPrefSummary(preference, stringValue);

                        // Trigger new calc if value change
                        index += Method.V2_MWL;
                        int oldMethodIdx = UserSettings.getCalculationMethod(context);
                        if (oldMethodIdx != index) {
                            Timber.d("New calc method: " + index);

                            // Mathhab hanafi pref. only for Karachi method.
                            Preference mathhabPref = findPreference(getString(R.string.locations_mathhab_hanafi_key));
                            if (index == Method.V2_KARACHI) {
                                mathhabPref.setEnabled(true);
                            } else {
                                mathhabPref.setEnabled(false);
                            }

                            PrayerTimesManager.handleLocationChange(context, index, -1, -1);
                        }
                    }
                    else
                    if(preference.getKey().equals(getString(R.string.locations_rounding_key)))
                    {
                        // Trigger new calc if value change
                        int oldRound = UserSettings.getRounding(context);
                        int newRound = stringValue.equals("true") ? 1 : 0;
                        if (oldRound != newRound) {
                            PrayerTimesManager.handleLocationChange(context, -1, newRound, -1);
                        }
                    }
                    else
                    if(preference.getKey().equals(getString(R.string.locations_mathhab_hanafi_key)))
                    {
                        // Trigger new calc if value change
                        boolean oldMathhab = UserSettings.isMathhabHanafi(context);
                        boolean newMathhab = stringValue.equals("true");
                        if (oldMathhab != newMathhab) {
                            PrayerTimesManager.handleLocationChange(context, -1, -1, newMathhab ? 2 : 1);
                        }
                    }
                    return true;
                }
            };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d("onActivityResult");
        if (requestCode == REQUEST_SEARCH_CITY) {
            if(resultCode == Activity.RESULT_OK){
                Preference pref = findPreference(getString(R.string.locations_search_city_key));
                pref.setSummary(data.getStringExtra("name"));

                // Main UI will be refreshed automatically by it's OnResume

                PrayerTimesManager.handleLocationChange(getActivity(), -1, -1, -1);
            }
            //else if (resultCode == Activity.RESULT_CANCELED) {
            //}
        }
    }
}