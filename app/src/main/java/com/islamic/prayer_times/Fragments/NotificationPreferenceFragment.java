package com.islamic.prayer_times.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.islamic.prayer_times.PrayerTimesManager;
import com.islamic.prayer_times.R;
import com.islamic.prayer_times.helpers.UserSettings;
import com.islamic.prayer_times.helpers.WakeLocker;
import com.islamic.prayer_times.services.AthanService;

import timber.log.Timber;

import static com.islamic.prayer_times.activities.SettingsActivity.setListPrefSummary;

public   class NotificationPreferenceFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_notifications);

        // Bind prayer time pref to its change listener
        Preference pref = findPreference(getString(R.string.notifications_prayer_time_key));
        pref.setOnPreferenceChangeListener(sNotifPrayerTimeListener);

        // Bind muezzin to its change listener
        pref = findPreference(getString(R.string.notifications_muezzin_key));
        pref.setOnPreferenceChangeListener(sMuezzinChangeListener);

        // Set summary to current value
        setListPrefSummary(pref, UserSettings.getMuezzin(pref.getContext()));
    }



    private static final Preference.OnPreferenceChangeListener sNotifPrayerTimeListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Timber.d("sNotifPrayerTimeListener: " + newValue.toString());
                    if (newValue.toString().equals("true")) {
                        PrayerTimesManager.enableAlarm(preference.getContext());
                    }
                    else {
                        PrayerTimesManager.disableAlarm(preference.getContext());
                    }
                    return true;
                }
            };

    private static final Preference.OnPreferenceChangeListener sMuezzinChangeListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    final String stringValue = newValue.toString();
                    final Context context = preference.getContext();
                    final ListPreference listPref = (ListPreference) preference;
                    int index = listPref.findIndexOfValue(stringValue);
                    final String name = index >= 0 ? listPref.getEntries()[index].toString() : "";

                    Timber.d("sMuezzinChangeListener: " + name);
                    playAthan(context, stringValue);

                    // Use the Builder class for convenient dialog construction
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.select_muezzin, name))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    listPref.setValue(stringValue);
                                    listPref.setSummary(name);
                                    UserSettings.setMuezzin(context, stringValue);
                                    stopAthan(context);
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    stopAthan(context);
                                }
                            })
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    stopAthan(context);
                                }
                            })
                            .create()
                            .show();

                    return false;
                }

                private void playAthan(Context context, String stringValue) {
                    // start Athan Audio
                    WakeLocker.acquire(context);
                    Intent playIntent = new Intent(context, AthanService.class);
                    playIntent.setAction(AthanService.ACTION_PLAY_ATHAN);
                    playIntent.putExtra(AthanService.EXTRA_PRAYER, 2);
                    playIntent.putExtra(AthanService.EXTRA_MUEZZIN, stringValue);
                    context.startService(playIntent);
                }

                private void stopAthan(Context context) {
                    AthanService.stopAthanAction(context);
                }
            };
}