package com.chevstrap.rbx.UI.Elements.Settings.Pages;

import android.content.Context;
import android.widget.LinearLayout;

import com.chevstrap.rbx.Enums.RobloxAppType;
import com.chevstrap.rbx.Enums.ThemeRecreated;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.UI.ViewModels.Settings.ChevstrapViewModel;
import com.chevstrap.rbx.Models.MethodPair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class Chevstrap {

    public static void addEveryPresets(Context context, LinearLayout parentLayout, ChevstrapFragment fragment) {
        try {
            JSONArray array = new JSONArray();

            for (ThemeRecreated mode : ThemeRecreated.values()) {
                JSONObject obj = new JSONObject()
                        .put("label", mode.getDisplayName())
                        .put("value", mode.name());
                array.put(obj);
            }

            Method getMethod = ChevstrapViewModel.class.getMethod("getapp_theme_in_app");
            Method setMethod = ChevstrapViewModel.class.getMethod("setapp_theme_in_app", String.class);

            MethodPair methods = new MethodPair(getMethod, setMethod);
            fragment.addDropdown(getTextLocale(context, R.string.menu_settings_color_theme_in_app_title), getTextLocale(context, R.string.menu_settings_color_theme_in_app_description), parentLayout, array, methods);
        } catch (JSONException e) {
//            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray array = new JSONArray();

            for (RobloxAppType mode : RobloxAppType.values()) {
                JSONObject obj = new JSONObject()
                        .put("label", mode.getDisplayName())
                        .put("value", mode.name());
                array.put(obj);
            }

            Method getMethod = ChevstrapViewModel.class.getMethod("get_preferred_roblox_app_type");
            Method setMethod = ChevstrapViewModel.class.getMethod("set_preferred_roblox_app_type", String.class);

            MethodPair methods = new MethodPair(getMethod, setMethod);
            fragment.addDropdown(getTextLocale(context, R.string.menu_preferred_roblox_app_type_title), getTextLocale(context, R.string.menu_preferred_roblox_app_type_description), parentLayout, array, methods);
        } catch (JSONException e) {
//            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

//        try {
//            JSONArray array = new JSONArray();
//
//            for (LanguageRecreateds mode : LanguageRecreateds.values()) {
//                JSONObject obj = new JSONObject()
//                        .put("label", mode.getDisplayName())
//                        .put("value", mode.name());
//                array.put(obj);
//            }
//
//            Method getMethod = SettingsViewModel.class.getMethod("getAppLanguage");
//            Method setMethod = SettingsViewModel.class.getMethod("setAppLanguage", String.class);
//
//            MethodPair methods = new MethodPair(getMethod, setMethod);
//            fragment.addDropdown(getTextLocale(context, R.string.menu_settings_language_title), getTextLocale(context, R.string.menu_settings_language_description), parentLayout, array, methods);
//        } catch (JSONException ignored) {
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static String getTextLocale(Context context, int resId) {
        return context.getString(resId);
    }
}