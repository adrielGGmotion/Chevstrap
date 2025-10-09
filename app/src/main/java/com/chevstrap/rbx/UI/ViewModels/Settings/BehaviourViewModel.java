package com.chevstrap.rbx.UI.ViewModels.Settings;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.ConfigManager;

public class BehaviourViewModel {
    private final ConfigManager manager;
    public BehaviourViewModel() {
        this.manager = App.getConfig();
    }

    public boolean isEnableBringToLatestUpdate() {
        return Boolean.parseBoolean(manager.getSettingValue("bring_update_chevstrap"));
    }

    public void setEnableBringToLatestUpdate(boolean value) {
        manager.setSettingValue("bring_update_chevstrap", String.valueOf(value));
    }

//    public boolean isEnableCleanRobloxCache() {
//        return Boolean.parseBoolean(manager.getSettingValue("clear_roblox_caches"));
//    }
//
//    public void setEnableCleanRobloxCache(boolean value) {
//        manager.setSettingValue("clear_roblox_caches", String.valueOf(value));
//    }
//
//    public boolean isEnableCleanRobloxLogs() {
//        return Boolean.parseBoolean(manager.getSettingValue("clear_roblox_logs"));
//    }
//
//    public void setEnableCleanRobloxLogs(boolean value) {
//        manager.setSettingValue("clear_roblox_logs", String.valueOf(value));
//    }
}