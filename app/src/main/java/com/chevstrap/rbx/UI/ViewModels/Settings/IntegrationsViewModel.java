package com.chevstrap.rbx.UI.ViewModels.Settings;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.ConfigManager;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.MessageboxFragment;
import com.chevstrap.rbx.UI.Elements.Settings.Pages.FFlagsSettings;
import com.chevstrap.rbx.UI.Frontend;

import java.lang.reflect.Method;

public class IntegrationsViewModel {
    private final ConfigManager manager;
    public IntegrationsViewModel() {
        this.manager = App.getConfig();
    }

    public String getFramerateLimit() {
        try {
            if (manager.getSettingValue("set_in_game_framerate_limit") == null) {
                return "0";
            } else {
                return manager.getSettingValue("set_in_game_framerate_limit");
            }
        } catch (Exception e) {
            return "0";
        }
    }

    public void setFramerateLimit(String val) {
        int value = Integer.parseInt(val);
        boolean remove_it = val.isEmpty() || value <= 0;

        if (remove_it) {
            manager.setSettingValue("set_in_game_framerate_limit", null);
        } else {
            if (Integer.parseInt(val) == 0) {
                manager.setSettingValue("set_in_game_framerate_limit", String.valueOf(-1));
            } else {
                manager.setSettingValue("set_in_game_framerate_limit", String.valueOf(value));
            }
        }

        if (value > 240) {
            Frontend.ShowMessageBox(App.getSavedFragmentActivity(), App.getTextLocale(App.getAppContext(), R.string.menu_fastflags_frameratelimit_240_warning));
        }
    }

    public boolean isQueryServerLocation() {
        return Boolean.parseBoolean(manager.getSettingValue("server_location_indicator_enabled"));
    }

    public void setQueryServerLocation(boolean value) {
        if (!isQueryServerLocation()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (App.getSavedFragmentActivity().checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    App.getSavedFragmentActivity().requestPermissions(
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            1001
                    );
                }
            }
        }

        manager.setSettingValue("server_location_indicator_enabled", String.valueOf(value));
    }
}