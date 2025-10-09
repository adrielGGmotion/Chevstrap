package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.PackageManager;

public class ExtraPaths {
    private static String getDataDir(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationInfo(packageName, 0).dataDir;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getRBXPathDir(Context context, String packageName) {
        String dir = getDataDir(context, packageName);
        if (dir != null) {
            return dir + "/files/";
        }
        return context.getFilesDir().getAbsolutePath()
                .replace("/" + context.getPackageName() + "/", "/" + packageName + "/") + "/";
    }

    public static String getRBXPathCache(Context context, String packageName) {
        String dir = getDataDir(context, packageName);
        if (dir != null) {
            return dir + "/cache/";
        }
        return context.getCacheDir().getAbsolutePath()
                .replace("/" + context.getPackageName() + "/", "/" + packageName + "/") + "/";
    }
}
