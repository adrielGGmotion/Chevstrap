package com.chevstrap.rbx.UI.Elements.Settings.Pages;

import android.content.Context;
import android.widget.LinearLayout;

import com.chevstrap.rbx.Models.MethodPair;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.UI.ViewModels.Settings.BehaviourViewModel;

import java.lang.reflect.Method;

public class Behaviour {

    public static void addEveryPresets(Context context, LinearLayout parentLayout, BehaviourFragment fragment) {
        try {
            Method getMethod = BehaviourViewModel.class.getMethod("isEnableBringToLatestUpdate");
            Method setMethod = BehaviourViewModel.class.getMethod("setEnableBringToLatestUpdate", boolean.class);
            MethodPair methods = new MethodPair(getMethod, setMethod);
            fragment.addToggle(
                    getTextLocale(context, R.string.menu_behaviour_bring_latest_release_title),
                    getTextLocale(context, R.string.menu_behaviour_bring_latest_release_description),
                    parentLayout,
                    methods
            );
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

//        try {
//            Method getMethod = BehaviourViewModel.class.getMethod("isEnableCleanRobloxCache");
//            Method setMethod = BehaviourViewModel.class.getMethod("setEnableCleanRobloxCache", boolean.class);
//            MethodPair methods = new MethodPair(getMethod, setMethod);
//            fragment.addToggle(
//                    getTextLocale(context, R.string.menu_behaviour_clear_roblox_caches_title),
//                    getTextLocale(context, R.string.menu_behaviour_clear_roblox_caches_description),
//                    parentLayout,
//                    methods
//            );
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//
//        try {
//            Method getMethod = BehaviourViewModel.class.getMethod("isEnableCleanRobloxLogs");
//            Method setMethod = BehaviourViewModel.class.getMethod("setEnableCleanRobloxLogs", boolean.class);
//            MethodPair methods = new MethodPair(getMethod, setMethod);
//            fragment.addToggle(
//                    getTextLocale(context, R.string.menu_behaviour_clear_roblox_logs_title),
//                    getTextLocale(context, R.string.menu_behaviour_clear_roblox_logs_description),
//                    parentLayout,
//                    methods
//            );
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static String getTextLocale(Context context, int resId) {
        return context.getString(resId);
    }
}
