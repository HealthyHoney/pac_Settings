/*
 * Copyright (C) 2015 SlimRoms Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.pac;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.settings.DialogCreatable;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.pac.utils.PacSeekBarPreference;
import net.margaritov.preference.colorpicker.ColorPickerPreference;

public class RecentPanel extends SettingsPreferenceFragment implements DialogCreatable,
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "RecentPanelSettings";

    // Preferences
    private static final String RECENT_PANEL_SHOW_TOPMOST =
            "recent_panel_show_topmost";
    private static final String RECENT_PANEL_LEFTY_MODE =
            "recent_panel_lefty_mode";
    private static final String RECENT_PANEL_SCALE =
            "recent_panel_scale";
    private static final String RECENT_PANEL_EXPANDED_MODE =
            "recent_panel_expanded_mode";
    private static final String RECENT_PANEL_BG_COLOR =
            "recent_panel_bg_color";
    private static final String RECENT_CARD_BG_COLOR =
            "recent_card_bg_color";
    private static final String RECENT_CARD_TEXT_COLOR =
            "recent_card_text_color";

    private SwitchPreference mRecentsShowTopmost;
    private SwitchPreference mRecentPanelLeftyMode;
    private PacSeekBarPreference mRecentPanelScale;
    private ListPreference mRecentPanelExpandedMode;
    private ColorPickerPreference mRecentPanelBgColor;
    private ColorPickerPreference mRecentCardBgColor;
    private ColorPickerPreference mRecentCardTextColor;

    private static final int MENU_RESET = Menu.FIRST;
    private static final int DEFAULT_BACKGROUND_COLOR = 0x00ffffff;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.recent_panel_settings);
        initializeAllPreferences();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mRecentPanelScale) {
            int value = (Integer) newValue;
            Settings.PAC.putInt(getContentResolver(),
                    Settings.PAC.RECENT_PANEL_SCALE_FACTOR, value);
            return true;
        } else if (preference == mRecentPanelExpandedMode) {
            int value = Integer.parseInt((String) newValue);
            Settings.PAC.putInt(getContentResolver(),
                    Settings.PAC.RECENT_PANEL_EXPANDED_MODE, value);
            return true;
        } else if (preference == mRecentPanelBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.seekbar_default);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.PAC.putInt(getContentResolver(),
                    Settings.PAC.RECENT_PANEL_BG_COLOR,
                    intHex);
            return true;
        } else if (preference == mRecentCardBgColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.seekbar_default);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.PAC.putInt(getContentResolver(),
                    Settings.PAC.RECENT_CARD_BG_COLOR,
                    intHex);
            return true;
        } else if (preference == mRecentCardTextColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(newValue)));
            if (hex.equals("#00ffffff")) {
                preference.setSummary(R.string.seekbar_default);
            } else {
                preference.setSummary(hex);
            }
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.PAC.putInt(getContentResolver(),
                    Settings.PAC.RECENT_CARD_TEXT_COLOR,
                    intHex);
            return true;
        } else if (preference == mRecentPanelLeftyMode) {
            Settings.PAC.putInt(getContentResolver(),
                    Settings.PAC.RECENT_PANEL_GRAVITY,
                    ((Boolean) newValue) ? Gravity.LEFT : Gravity.RIGHT);
            return true;
        } else if (preference == mRecentsShowTopmost) {
            Settings.PAC.putInt(getContentResolver(),
                    Settings.PAC.RECENT_PANEL_SHOW_TOPMOST,
                    ((Boolean) newValue) ? 1 : 0);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecentPanelPreferences();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.add(0, MENU_RESET, 0, R.string.profile_reset_title)
                .setIcon(com.android.internal.R.drawable.ic_menu_refresh)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESET:
                resetToDefault();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void resetToDefault() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.profile_reset_title);
        alertDialog.setMessage(R.string.recent_panel_reset_message);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                resetValues();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.create().show();
    }

    private void resetValues() {
        Settings.PAC.putInt(getContentResolver(),
                Settings.PAC.RECENT_PANEL_BG_COLOR, DEFAULT_BACKGROUND_COLOR);
        mRecentPanelBgColor.setNewPreviewColor(DEFAULT_BACKGROUND_COLOR);
        mRecentPanelBgColor.setSummary(R.string.seekbar_default);
        Settings.PAC.putInt(getContentResolver(),
                Settings.PAC.RECENT_CARD_BG_COLOR, DEFAULT_BACKGROUND_COLOR);
        mRecentCardBgColor.setNewPreviewColor(DEFAULT_BACKGROUND_COLOR);
        mRecentCardBgColor.setSummary(R.string.seekbar_default);
        Settings.PAC.putInt(getContentResolver(),
                Settings.PAC.RECENT_CARD_TEXT_COLOR, DEFAULT_BACKGROUND_COLOR);
        mRecentCardTextColor.setNewPreviewColor(DEFAULT_BACKGROUND_COLOR);
        mRecentCardTextColor.setSummary(R.string.seekbar_default);
    }

    private void updateRecentPanelPreferences() {
        final boolean recentLeftyMode = Settings.PAC.getInt(getContentResolver(),
                Settings.PAC.RECENT_PANEL_GRAVITY, Gravity.RIGHT) == Gravity.LEFT;
        mRecentPanelLeftyMode.setChecked(recentLeftyMode);

        final int recentScale = Settings.PAC.getInt(getContentResolver(),
                Settings.PAC.RECENT_PANEL_SCALE_FACTOR, 100);
        mRecentPanelScale.setValue(recentScale);

        final int recentExpandedMode = Settings.PAC.getInt(getContentResolver(),
                Settings.PAC.RECENT_PANEL_EXPANDED_MODE, 0);
        mRecentPanelExpandedMode.setValue(recentExpandedMode + "");
    }

    private void initializeAllPreferences() {
        // Recent panel background color
        mRecentPanelBgColor =
                (ColorPickerPreference) findPreference(RECENT_PANEL_BG_COLOR);
        mRecentPanelBgColor.setOnPreferenceChangeListener(this);
        final int intColor = Settings.PAC.getInt(getContentResolver(),
                Settings.PAC.RECENT_PANEL_BG_COLOR, 0x00ffffff);
        String hexColor = String.format("#%08x", (0x00ffffff & intColor));
        if (hexColor.equals("#00ffffff")) {
            mRecentPanelBgColor.setSummary(R.string.seekbar_default);
        } else {
            mRecentPanelBgColor.setSummary(hexColor);
        }
        mRecentPanelBgColor.setNewPreviewColor(intColor);

        // Recent card background color
        mRecentCardBgColor =
                (ColorPickerPreference) findPreference(RECENT_CARD_BG_COLOR);
        mRecentCardBgColor.setOnPreferenceChangeListener(this);
        final int intColorCard = Settings.PAC.getInt(getContentResolver(),
                Settings.PAC.RECENT_CARD_BG_COLOR, 0x00ffffff);
        String hexColorCard = String.format("#%08x", (0x00ffffff & intColorCard));
        if (hexColorCard.equals("#00ffffff")) {
            mRecentCardBgColor.setSummary(R.string.seekbar_default);
        } else {
            mRecentCardBgColor.setSummary(hexColorCard);
        }
        mRecentCardBgColor.setNewPreviewColor(intColorCard);

        // Recent card text color
        mRecentCardTextColor =
                (ColorPickerPreference) findPreference(RECENT_CARD_TEXT_COLOR);
        mRecentCardTextColor.setOnPreferenceChangeListener(this);
        final int intColorText = Settings.PAC.getInt(getContentResolver(),
                Settings.PAC.RECENT_CARD_TEXT_COLOR, 0x00ffffff);
        String hexColorText = String.format("#%08x", (0x00ffffff & intColorText));
        if (hexColorText.equals("#00ffffff")) {
            mRecentCardTextColor.setSummary(R.string.seekbar_default);
        } else {
            mRecentCardTextColor.setSummary(hexColorText);
        }
        mRecentCardTextColor.setNewPreviewColor(intColorText);

        // Enable options menu for color reset
        setHasOptionsMenu(true);

        boolean enableRecentsShowTopmost = Settings.PAC.getInt(getContentResolver(),
                Settings.PAC.RECENT_PANEL_SHOW_TOPMOST, 0) == 1;
        mRecentsShowTopmost = (SwitchPreference) findPreference(RECENT_PANEL_SHOW_TOPMOST);
        mRecentsShowTopmost.setChecked(enableRecentsShowTopmost);
        mRecentsShowTopmost.setOnPreferenceChangeListener(this);

        mRecentPanelLeftyMode =
                (SwitchPreference) findPreference(RECENT_PANEL_LEFTY_MODE);
        mRecentPanelLeftyMode.setOnPreferenceChangeListener(this);

        mRecentPanelScale =
                (PacSeekBarPreference) findPreference(RECENT_PANEL_SCALE);
        mRecentPanelScale.setValue(100);
        mRecentPanelScale.setOnPreferenceChangeListener(this);

        mRecentPanelExpandedMode =
                (ListPreference) findPreference(RECENT_PANEL_EXPANDED_MODE);
        mRecentPanelExpandedMode.setOnPreferenceChangeListener(this);
    }
}
