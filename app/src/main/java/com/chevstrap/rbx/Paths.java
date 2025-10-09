package com.chevstrap.rbx;

import java.io.File;

public class Paths {
    private static String base = "";
    private static String backups = "";
    private static String logs = "";
    private static String integrations = "";
    private static String modifications = "";
    private static String customThemes = "";

    public static String getTemp() {
        return new File(App.getAppContext().getCacheDir(), "").getAbsolutePath();
    }

    public static String getLocalAppData() {
        return App.getAppContext().getFilesDir().getAbsolutePath();
    }

    public static String getBackups() {
        ensureInitialized();
        return backups;
    }

    public static String getLogs() {
        ensureInitialized();
        return logs;
    }

    public static String getIntegrations() {
        ensureInitialized();
        return integrations;
    }

    public static String getModifications() {
        ensureInitialized();
        return modifications;
    }

    public static String getCustomThemes() {
        ensureInitialized();
        return customThemes;
    }

    public static String getCustomFont() {
        ensureInitialized();
        return new File(modifications, "content/fonts/CustomFont.ttf").getAbsolutePath();
    }

    public static boolean isInitialized() {
        return !base.isEmpty();
    }

    public static void initialize(String baseDirectory) {
        base = baseDirectory;
        backups = new File(base, "Backups").getAbsolutePath();
        logs = new File(base, "Logs").getAbsolutePath();
        integrations = new File(base, "Integrations").getAbsolutePath();
        modifications = new File(base, "Modifications").getAbsolutePath();
        customThemes = new File(base, "CustomThemes").getAbsolutePath();
    }

    private static void ensureInitialized() {
        if (!isInitialized()) {
            initialize(getLocalAppData());
        }
    }
}
