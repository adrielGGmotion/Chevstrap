package com.chevstrap.rbx.Enums;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.R;

public enum RobloxAppType {
    global(App.getTextLocale(App.getAppContext(), R.string.enums_flagpresets_roblox_global)),
    vn(App.getTextLocale(App.getAppContext(), R.string.enums_flagpresets_roblox_vn));

    private final String displayName;

    RobloxAppType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}