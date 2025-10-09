package com.chevstrap.rbx.Models.Persistable;

import com.chevstrap.rbx.ConfigManager;
import java.util.Map;

public class ChevstrapSettings {
    private final ConfigManager config;

    public ChevstrapSettings(ConfigManager config) {
        this.config = config;
    }

    public void applyDefaults() {
        Map<String, Object> props = config.getProp();

        if (props.get("app_theme_in_app") == null) {
            config.setSettingValue("app_theme_in_app", "dark");
        }

        if (props.get("preferred_roblox_app_type") == null) {
            config.setSettingValue("preferred_roblox_app_type", "global");
        }

        if (props.get("use_fflags_manager") == null) {
            config.setSettingValue("use_fflags_manager", "true");
        }
    }
}