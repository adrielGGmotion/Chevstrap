package com.chevstrap.rbx.Integrations;

import android.content.Context;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.ExtraPaths;

import com.chevstrap.rbx.UI.NotifyIconWrapper;
import com.chevstrap.rbx.Utility.FileTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityWatcher {
    private boolean stopMonitoring = false;
    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ActivityWatcher(Context ctx) {
        this.context = ctx;
    }

    public void runWatcher() {
        String LOG_IDENT = "ActivityWatcher::Start";
        App.getLogger().writeLine(LOG_IDENT, "Starting activity watcher session");
        executor.submit(this::Start);
    }

    private void Start() {
        final String LOG_IDENT = "ActivityWatcher::Initialize";

        File logLocation = new File(
                Objects.requireNonNull(
                        ExtraPaths.getRBXPathDir(App.getAppContext(), App.getPackageTarget(App.getAppContext()))
                ),
                "appData/logs"
        );

        if (!logLocation.exists()) {
            boolean created = logLocation.mkdirs();
            App.getLogger().writeLine(LOG_IDENT, "Log directory created=" + created + " at " + logLocation.getAbsolutePath());
        }
        App.getLogger().writeLine(LOG_IDENT, "Watching logs from " + logLocation.getAbsolutePath());

        BufferedReader reader = null;
        File ignoredFirstLog = null;
        File currentLog = null;

        try {
            File[] logsList = FileTool.listFiles(logLocation);
            if (logsList.length > 0) {
                Arrays.sort(logsList, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                ignoredFirstLog = logsList[0];
                App.getLogger().writeLine(LOG_IDENT, "Ignoring first log: " + ignoredFirstLog.getName());
            }

            while (!stopMonitoring) {
                logsList = FileTool.listFiles(logLocation);
                if (logsList.length == 0) {
                    synchronized (this) { wait(2000); }
                    continue;
                }

                Arrays.sort(logsList, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
                File latestLog = logsList[0];

                if (latestLog.equals(ignoredFirstLog)) {
                    synchronized (this) { wait(2000); }
                    continue;
                }

                if (reader == null || !latestLog.equals(currentLog)) {
                    if (reader != null) reader.close();
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(latestLog)));
                    currentLog = latestLog;
                    App.getLogger().writeLine(LOG_IDENT, "Now watching new log: " + currentLog.getName());
                }

                String line = reader.readLine();
                if (line != null) {
                    HandleLogEntry(line);
                } else {
                    synchronized (this) { wait(1000); }
                }
            }
        } catch (IOException | InterruptedException e) {
            App.getLogger().writeException(LOG_IDENT + " Failed to run activity watcher!", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {}
            }
            NotifyIconWrapper.hideConnectionNotification(context);
        }
    }

    private void HandleLogEntry(String line) {
        String LOG_IDENT = "ActivityWatcher::HandleLogEntry";

        final String joinGameStr = "[FLog::Output] ! Joining game";
        final String connectionStr = "Info [DFLog::NetworkClient] Connection accepted from";

        if (line.contains("[FLog::Network] NetworkClient:Remove")) {
            NotifyIconWrapper.hideConnectionNotification(context);
        } else if (line.contains(joinGameStr)) {
            int placeIdStart = line.indexOf("place ") + 6;
            int placeIdEnd = line.indexOf(" at", placeIdStart);
            int jobIdStart = line.indexOf("'") + 1;
            int jobIdEnd = line.indexOf("'", jobIdStart);

            if (placeIdStart > 5 && placeIdEnd > placeIdStart && jobIdStart > 0 && jobIdEnd > jobIdStart) {
                String lastPlaceId = line.substring(placeIdStart, placeIdEnd);
                String lastJobId = line.substring(jobIdStart, jobIdEnd);
                App.getLogger().writeLine(LOG_IDENT, "Joining game → placeId=" + lastPlaceId + ", jobId=" + lastJobId);
            } else {
                App.getLogger().writeLine(LOG_IDENT, "Failed to parse placeId/jobId from join string");
            }

        } else if (line.contains(connectionStr)) {
            String[] parts = line.trim().split(" ");
            if (parts.length == 0) return;

            String lastPart = parts[parts.length - 1];
            String[] ipSplit = lastPart.split("\\|");

            if (ipSplit.length >= 2) {
                String location = ipSplit[0];
                App.getLogger().writeLine(LOG_IDENT, "Connection string: " + line);
                NotifyIconWrapper.showConnectionNotification(App.getAppContext(), location);
            } else {
                App.getLogger().writeLine(LOG_IDENT, "Connection string did not contain expected IP format: " + lastPart);
            }
        }
    }

    public void Dispose() {
        String LOG_IDENT = "ActivityWatcher::Dispose";
        stopMonitoring = true;

        App.getLogger().writeLine(LOG_IDENT, "Disposed activity watcher session");
        NotifyIconWrapper.hideConnectionNotification(context);
    }
}