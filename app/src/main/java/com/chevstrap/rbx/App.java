package com.chevstrap.rbx;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.view.Display;
import android.content.Context;
import android.os.Build;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import com.chevstrap.rbx.Integrations.ActivityWatcher;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class App extends Application {
    private static ConfigManager manager;
    public static final String ProjectName = "Chevstrap";
    public static final String ProjectOwner = "FrosSky";
    public static final String ProjectRepository = "FrosSky/Chevstrap";
    public static final String ProjectDownloadLink = "https://github.com/FrosSky/Chevstrap/releases/latest";
    public static final String ProjectHelpLink = "https://github.com/frossky/chevstrap/wiki";
    public static final String ProjectSupportLink = "https://github.com/frossky/chevstrap/issues/new";
    private static WeakReference<ActivityWatcher> activityWatcherRef = new WeakReference<>(null);
    private static Context appContext;
    private static FragmentActivity savedFragmentActivity;
    private static boolean IsAlreadyStartup = false;
    public static void setTempActivityWatcherClass(ActivityWatcher watcher) {
        if (watcher == null && activityWatcherRef.get() != null) {
            activityWatcherRef.get().Dispose();
        }
        activityWatcherRef = new WeakReference<>(watcher);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        manager = ConfigManager.getInstance();

        if (!getIsAlreadyStartup()) {
            onStartup();
        }
    }

    public static ConfigManager getConfig() {
        return manager;
    }

    public static Context getAppContext() {
        return appContext;
    }

    public static FragmentActivity getSavedFragmentActivity() {
        return savedFragmentActivity;
    }

    public static void setSavedFragmentActivity(FragmentActivity getFragmentActivityIs) {
        savedFragmentActivity = getFragmentActivityIs;
    }

    public static boolean getIsAlreadyStartup() {
        return IsAlreadyStartup;
    }

    public static void setIsAlreadyStartup(Boolean valueSet) {
        IsAlreadyStartup = valueSet;
    }

    private static void initializeLogger() {
        Logger logger = Logger.getInstance();
        logger.initializePersistent();
    }

    public static String getPackageTarget(Context context) {
        String preferred = App.getConfig().getSettingValue("preferred_roblox_app_type");
        if ("vn".equals(preferred)) {
            return "com.roblox.client.vnggames";
        } else if ("global".equals(preferred)) {
            return "com.roblox.client";
        } else {
            return "com.roblox.client";
        }
    }

//    public static boolean isNetworkAvailable(Context context) {
//        ConnectivityManager connectivityManager =
//                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        if (connectivityManager == null) {
//            return false;
//        }
//
//        Network network = connectivityManager.getActiveNetwork();
//        if (network == null) {
//            return false;
//        }
//
//        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
//        if (capabilities == null) {
//            return false;
//        }
//
//        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
//                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
//                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
//    }

//    public static boolean isEmulator() {
//        String brand = Build.BRAND;
//        String device = Build.DEVICE;
//        String product = Build.PRODUCT;
//        String model = Build.MODEL;
//        String hardware = Build.HARDWARE;
//        String manufacturer = Build.MANUFACTURER;
//        String fingerprint = Build.FINGERPRINT;
//
//        if (brand != null && brand.startsWith("generic")) return true;
//        if (device != null && device.startsWith("generic")) return true;
//        if (product != null && (product.contains("sdk") || product.contains("emulator") || product.contains("genymotion"))) return true;
//        if (model != null && (model.contains("google_sdk") || model.contains("Emulator") || model.contains("Android SDK built for x86"))) return true;
//        if (hardware != null && (hardware.contains("goldfish") || hardware.contains("ranchu") || hardware.contains("qemu"))) return true;
//        if (manufacturer != null && manufacturer.contains("Genymotion")) return true;
//        return fingerprint != null && fingerprint.startsWith("generic");
//    }

    public static String getLatestVersion() {
        try {
            URL url = new URL("https://api.github.com/repos/" + ProjectRepository + "/releases/latest");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Chevstrap-App");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "no";
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.disconnect();

            JSONObject json = new JSONObject(response.toString());

            if (!json.has("tag_name")) {
                return "no";
            }

            return json.getString("tag_name").replaceFirst("^v", "");

        } catch (Exception e) {
            return "no";
        }
    }

//    public static int getDeviceRefreshRate(Context context) {
//        if (context == null) return 0;
//
//        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        if (wm == null) return 0;
//
//        Display display = wm.getDefaultDisplay();
//        if (display == null) return 0;
//
//        return (int) display.getRefreshRate();
//    }

    public static void onStartup() {
        final String LOG_IDENT = "App::onStartup";
        initializeLogger();

        String versionName = Build.VERSION.RELEASE;
        int sdkInt = Build.VERSION.SDK_INT;
        String codename = Build.VERSION.CODENAME;

        getLogger().writeLine(LOG_IDENT, "Starting " + ProjectName + " 2.0");

        String osInfo = "OSVersion: Android " + versionName +
                " (SDK " + sdkInt + ", Codename: " + codename + ")";
        getLogger().writeLine(LOG_IDENT, osInfo);

        manager.load(false);
        setIsAlreadyStartup(true);
    }

    public static String getCurrentVersion(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "0.0.0";
        }
    }

    public static String getTextLocale(Context context, int resId) {
        return context.getString(resId);
    }

    public static Logger getLogger() {
        return Logger.getInstance();
    }
}
