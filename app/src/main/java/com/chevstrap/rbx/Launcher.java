package com.chevstrap.rbx;

import static com.chevstrap.rbx.App.ProjectDownloadLink;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Xml;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.chevstrap.rbx.Integrations.ActivityWatcher;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.LoadingFragment;
import com.chevstrap.rbx.UI.Frontend;
import com.chevstrap.rbx.UI.ViewModels.GlobalViewModel;
import com.chevstrap.rbx.Utility.FileTool;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Launcher {
    private FragmentManager fragmentManager;
    private volatile boolean isCancelled = false;
    private String packageName;
    private ActivityWatcher watcher;

    private static String getTextLocale(Context context, int resId) {
        return context.getString(resId);
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void Run() {
        String LOG_IDENT = "Launcher::Run";
        Logger logger = App.getLogger();
        logger.writeLine(LOG_IDENT, "Running launcher");

        App.getConfig().load(false);
        StartRoblox();
    }

    public Boolean ApplyChanges(Context context) throws Exception {
        String LOG_IDENT = "Launcher::ApplyChanges";
        Logger logger = App.getLogger();
        logger.writeLine(LOG_IDENT, "Checking file changes");

        String fallbackDirPath = ExtraPaths.getRBXPathDir(context, App.getPackageTarget(context)) + "exe/ClientSettings";
        File fallbackDir = new File(fallbackDirPath);
        File fallbackFile = new File(fallbackDir, "ClientAppSettings.json");

        String globalSettingPath = ExtraPaths.getRBXPathDir(context, App.getPackageTarget(context)) + "appData";
        File globalSettingFile = new File(globalSettingPath, "GlobalBasicSettings_13.xml");

        String value_usefflagmanager = (String) App.getConfig().getProp().get("use_fflags_manager");
        int value_set_framerate_limit = -1;
        Object framerateObj = App.getConfig().getProp().get("set_framerate_limit");

        if (framerateObj != null) {
            try {
                value_set_framerate_limit = Integer.parseInt(framerateObj.toString());
            } catch (NumberFormatException e) {
                App.getLogger().writeLine(LOG_IDENT, "Failed to parse framerate limit");
                App.getLogger().writeException(LOG_IDENT, e);
                throw new Exception(e.getMessage());
            }
        }

        boolean useFlagManager = value_usefflagmanager != null && Boolean.parseBoolean(value_usefflagmanager);
        if (!useFlagManager) {
            logger.writeLine(LOG_IDENT, "FFlag manager disabled or not set, cancelling ApplyChanges");
            return true;
        }

        if (fallbackDir.mkdirs()) {
            String reason = "Failed to create directory: " + fallbackDir.getAbsolutePath();
            logger.writeLine(LOG_IDENT, reason);
            throw new Exception(reason);
        }

            File file = new File(String.valueOf(App.getConfig().getFileLocation()));
            String jsonStr = FileTool.read(file);
            JSONObject json = new JSONObject(jsonStr);

            if (json.has("fflags")) {
                JSONObject FFlagsObject = json.getJSONObject("fflags");
                String contents = FFlagsObject.toString(4);
                logger.writeLine(LOG_IDENT, "Applying FFlags");
                FileTool.write(fallbackFile, contents);
            }

//            if (value_set_framerate_limit != -1) {
//                if (FileTool.isExist(String.valueOf(globalSettingFile))) {
//                    FileTool.deleteFile(globalSettingFile);
//                }
//                FileTool.write(globalSettingFile, setXmlSetting(FileTool.read(globalSettingFile), "FramerateCap", String.valueOf(value_set_framerate_limit)));
//            }
            return true;
    }

    public void StartRoblox() {
        Context context = App.getAppContext();
        if (context == null || fragmentManager == null) {
            throw new IllegalStateException("Context or FragmentManager not set.");
        }

        this.packageName = App.getPackageTarget(context);
        isCancelled = false;

        LoadingFragment fragment = createLoadingDialog();
        fragment.setCancelable(false);

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(() -> {
            fragment.setMessageText(getTextLocale(App.getAppContext(), R.string.bootstrapper_status_connecting));
            fragment.setMessageStatus("0%");
            try {
                fragment.show(fragmentManager, "Messagebox");
            } catch (IllegalStateException ignored) {
            }
        });

        if (IsRobloxModified(context)) {
            handler.post(() -> Frontend.ShowMessageBox(App.getSavedFragmentActivity(),
                    getTextLocale(App.getAppContext(), R.string.dialog_cheater_warning)));
            isCancelled = true;
            return;
        }

        animateProgress(fragment, 0, 50, () -> {
            if (isCancelled) return;
            handler.post(() -> fragment.setMessageText(
                    getTextLocale(App.getAppContext(), R.string.bootstrapper_status_applying_modifications)));

            animateProgress(fragment, 50, 70, () -> {
                if (isCancelled) return;

                boolean dontcancel;
                try {
                    dontcancel = ApplyChanges(context);
                } catch (Exception e) {
                    String LOG_IDENT = "Launcher::ApplyChanges";
                    String reason = "Failed to apply FFlags";
                    App.getLogger().writeLine(LOG_IDENT, reason);
                    App.getLogger().writeException(LOG_IDENT, e);
                    dontcancel = false;
                }

                if (!dontcancel) {
                    isCancelled = true;
                    handler.post(() -> {
                            if (fragment.isAdded()) fragment.dismissAllowingStateLoss();
                            Frontend.ShowMessageBox(App.getSavedFragmentActivity(),
                                getTextLocale(App.getAppContext(), R.string.dialog_failed_to_apply_changes));
                    });
                    return;
                }

                animateProgress(fragment, 70, 80, () -> {
                    if (isCancelled) return;

//                    if (Boolean.parseBoolean(App.getConfig().getSettingValue("clear_roblox_caches"))) {
//                        handler.post(() -> {
//                            if (Boolean.parseBoolean(App.getConfig().getSettingValue("clear_roblox_caches"))) {
//                                fragment.setMessageText(getTextLocale(App.getAppContext(),
//                                        R.string.bootstrapper_status_cleaning_caches_on_roblox));
//                            }
//                        });
//                        clearRobloxCache(context);
//                    }
//
//                    if (Boolean.parseBoolean(App.getConfig().getSettingValue("clear_roblox_logs"))) {
//                        handler.post(() -> {
//                            if (Boolean.parseBoolean(App.getConfig().getSettingValue("clear_roblox_logs"))) {
//                                fragment.setMessageText(getTextLocale(App.getAppContext(),
//                                        R.string.bootstrapper_status_cleaning_logs_on_roblox));
//                            }
//                        });
//                        clearRobloxLogs(context);
//                    }

                    animateProgress(fragment, 80, 100, () -> {
                        if (isCancelled) return;
                        App.setTempActivityWatcherClass(null);

                        String currentVersion = null;
                        try {
                            currentVersion = context.getPackageManager()
                                    .getPackageInfo(context.getPackageName(), 0).versionName;
                        } catch (Exception ignored) {
                        }

                        if (Boolean.parseBoolean(App.getConfig().getSettingValue("bring_update_chevstrap"))) {
                            if (currentVersion != null &&
                                    !App.getLatestVersion().equals(currentVersion) &&
                                    !App.getLatestVersion().equals("no")) {
                                handler.post(() -> fragment.setMessageText(
                                        getTextLocale(App.getAppContext(), R.string.bootstrapper_status_upgrading_chevstrap)));

                                try {
                                    if (fragment.isAdded()) fragment.dismissAllowingStateLoss();
                                } catch (Exception e) {
                                    Frontend.ShowExceptionDialog(App.getSavedFragmentActivity(),
                                            getTextLocale(App.getAppContext(), R.string.dialog_failed_to_launch), e);
                                }
                                GlobalViewModel.openWebpage(App.getAppContext(), ProjectDownloadLink);
                            }
                        } else {
                            handler.post(() -> fragment.setMessageText("Starting Roblox"));
                            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                            if (launchIntent != null) {
                                handler.post(() -> {
                                    try {
                                        if (fragment.isAdded()) fragment.dismissAllowingStateLoss();

                                        if (!(context instanceof Activity)) {
                                            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        }

                                        if (Boolean.parseBoolean(App.getConfig().getSettingValue("server_location_indicator_enabled"))) {
                                            watcher = new ActivityWatcher(App.getAppContext());
                                            watcher.runWatcher();
                                            App.setTempActivityWatcherClass(watcher);
                                        }

                                        LaunchRoblox(context, launchIntent);
                                    } catch (Exception e) {
                                        Frontend.ShowExceptionDialog(App.getSavedFragmentActivity(), getTextLocale(App.getAppContext(), R.string.dialog_failed_to_launch), e);
                                    }
                                });
                            } else {
                                handler.post(() -> {
                                    Frontend.ShowMessageBox(App.getSavedFragmentActivity(),
                                            getTextLocale(App.getAppContext(), R.string.dialog_not_installed));
                                });
                            }
                        }
                    });
                });
            });
        });
    }

//    public void clearRobloxLogs(Context context) {
//        try {
//            File dir = new File(Objects.requireNonNull(ExtraPaths.getRBXPathDir(context, App.getPackageTarget(context))));
//            FileTool.deleteDir(new File(dir + "/appData/logs"), false);
//        } catch (Exception ignored) {
//        }
//    }
//
//    public void clearRobloxCache(Context context) {
//        try {
//            File dir = new File(Objects.requireNonNull(ExtraPaths.getRBXPathCache(context, App.getPackageTarget(context))));
//            FileTool.deleteDir(dir, false);
//        } catch (Exception ignored) {
//        }
//    }

    private void LaunchRoblox(Context context, Intent launchIntent) {
        try {
            context.startActivity(launchIntent);
        } catch (Exception e) {
            Frontend.ShowPlayerErrorDialog(App.getSavedFragmentActivity(), e);
            App.getLogger().writeLine("Launcher::LaunchRoblox", "Failed to launch Roblox");
            App.getLogger().writeException("Launcher::LaunchRoblox", e);
        }
    }

    private void animateProgress(LoadingFragment fragment, int start, int end, Runnable onComplete) {
        Handler handler = new Handler(Looper.getMainLooper());
        int steps = 20;
        int delay = 3500 / steps;
        float increment = (float) (end - start) / steps;

        final float[] progress = {start};

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isCancelled) return;

                if (progress[0] < end) {
                    fragment.setMessageStatus(((int) progress[0]) + "%");
                    progress[0] += increment;
                    handler.postDelayed(this, delay);
                } else {
                    fragment.setMessageStatus(end + "%");
                    if (onComplete != null) onComplete.run();
                }
            }
        };
        handler.post(runnable);
    }

    private boolean IsRobloxModified(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(packageName, 0);
            try (ZipFile zipFile = new ZipFile(ai.sourceDir)) {
                for (ZipEntry entry : Collections.list(zipFile.entries())) {
                    if (entry.getName().startsWith("lib/")) {
                        App.getLogger().writeLine("Launcher::IsRobloxModified", "Roblox is modified detected");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            App.getLogger().writeLine("Launcher::IsRobloxModified", "Failed to check if Roblox is modified");
            App.getLogger().writeException("Launcher::IsRobloxModified", e);
        }
        return false;
    }

    @NonNull
    private LoadingFragment createLoadingDialog() {
        LoadingFragment fragment = new LoadingFragment();
        fragment.setMessageboxListener(() -> {
            isCancelled = true;
            if (fragment.isAdded() && fragmentManager != null) {
                fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
        });
        return fragment;
    }

//    public static String setXmlSetting(String xml, String settingName, String newValue) {
//        try {
//            StringWriter writer = new StringWriter();
//            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
//            factory.setNamespaceAware(true);
//            XmlPullParser parser = factory.newPullParser();
//            parser.setInput(new ByteArrayInputStream(xml.getBytes()), "UTF-8");
//
//            XmlSerializer serializer = Xml.newSerializer();
//            serializer.setOutput(writer);
//            serializer.startDocument("UTF-8", true);
//
//            int eventType = parser.getEventType();
//            boolean insideTarget = false;
//
//            while (eventType != XmlPullParser.END_DOCUMENT) {
//                switch (eventType) {
//                    case XmlPullParser.START_TAG:
//                        serializer.startTag(null, parser.getName());
//                        for (int i = 0; i < parser.getAttributeCount(); i++) {
//                            String attrName = parser.getAttributeName(i);
//                            String attrValue = parser.getAttributeValue(i);
//                            serializer.attribute(null, attrName, attrValue);
//
//                            if ("name".equals(attrName) && settingName.equals(attrValue)) {
//                                insideTarget = true;
//                            }
//                        }
//                        break;
//
//                    case XmlPullParser.TEXT:
//                        if (insideTarget) {
//                            serializer.text(newValue);
//                            insideTarget = false;
//                        } else {
//                            serializer.text(parser.getText());
//                        }
//                        break;
//
//                    case XmlPullParser.END_TAG:
//                        serializer.endTag(null, parser.getName());
//                        break;
//                }
//                eventType = parser.next();
//            }
//
//            serializer.endDocument();
//            return writer.toString();
//
//        } catch (Exception e) {
//            App.getLogger().writeLine("RobloxSettingsEditor", "Error updating XML: " + e.getMessage());
//            return xml;
//        }
//    }
}
