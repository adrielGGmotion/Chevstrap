package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.PackageManager;

import java.util.Objects;

public class Packages {
    private static final String package_1 = "com.roblox.client";
    private static final String package_2 = "com.roblox.client.vnggames";

    public static String getPackageOfGlobalRoblox() {
        return package_1;
    }

    public static String getPackageOfRobloxVNG() {
        return package_2;
    }

    public static String getPackageTarget(Context context) {
        String preferredApp = App.getConfig().getSettingValue("preferred_roblox_app");

        if (Objects.equals(preferredApp, "VNG")) {
            return getPackageOfRobloxVNG();
        } else if (Objects.equals(preferredApp, "global")) {
            return getPackageOfGlobalRoblox();
        }

        String[] robloxPackages = {
                getPackageOfRobloxVNG(),
                getPackageOfGlobalRoblox()
        };

        for (String pkg : robloxPackages) {
            try {
                context.getPackageManager().getPackageInfo(pkg, 0);
                return pkg;
            } catch (PackageManager.NameNotFoundException ignored) {}
        }
        return null;
    }
}
