/*
 *  Copyright © 2017 Djalel Chefrour
 *
 *  This file is part of Bilal.
 *
 *  Bilal is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Bilal is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Bilal.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.islamic.prayer_times.activities;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.MenuItem;

import com.islamic.prayer_times.Fragments.SettingsPreferencesFragment;
import com.islamic.prayer_times.PrayerTimesManager;
import com.islamic.prayer_times.helpers.FragmentHelper;
import com.islamic.prayer_times.helpers.UserSettings;
import com.islamic.prayer_times.helpers.WakeLocker;
import com.islamic.prayer_times.services.AthanService;
import com.islamic.prayer_times.R;


import org.arabeyes.prayertime.Method;

import timber.log.Timber;


public class SettingsActivity extends AppCompatActivity implements FragmentHelper.ActivityInterface {
    public static final int REQUEST_SEARCH_CITY = 1;

    FragmentHelper fragmentHelper ;
    public FragmentHelper getFragmentHelper() {
        return fragmentHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setTitle(getString(R.string.app_name));

        fragmentHelper = new FragmentHelper(getSupportFragmentManager()
                , android.R.id.content, this);

        openFragment(new SettingsPreferencesFragment());

    }


    public void openFragment(PreferenceFragmentCompat  preferenceFragmentCompat) {
        fragmentHelper.showFragment(preferenceFragmentCompat,false);
    }

    @Override
    public void switchActivityUIByFragment(Fragment fragment) {

    }

    @Override
    public void noBackFragments() {
        finish();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentHelper().getCurrentFragment()!=null)
        {
            getFragmentHelper().backFragmentsTag();
        }
        else
        {
            finish();
        }

    }


    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }





    public static int setListPrefSummary(Preference pref, String value) {
        ListPreference listPref = (ListPreference) pref;
        int index = listPref.findIndexOfValue(value);
        listPref.setSummary(index >= 0 ? listPref.getEntries()[index] : null);
        return index;
    }










}
