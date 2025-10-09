package com.chevstrap.rbx.UI.Elements.Settings.Pages;

import android.content.Context;

import com.chevstrap.rbx.Enums.FlagPresets.MSAAMode;
import com.chevstrap.rbx.Enums.FlagPresets.RenderingMode;
import com.chevstrap.rbx.Enums.FlagPresets.TextureMode;
import com.chevstrap.rbx.Models.MethodPair;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.UI.ViewModels.Settings.FastFlagsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class FFlagsSettings {
    public static void addEveryPresets(Context context, FFlagsSettingsFragment uiHelpers) {
        uiHelpers.addButton(getTextLocale(context, R.string.menu_fastflags_help_title), getTextLocale(context, R.string.menu_fastflags_help_description), "https://github.com/FrosSky/Chevstrap/wiki/Fast-Flags-Guide-for-Android", "link", -1);

        try {
            Method getMethod = FastFlagsViewModel.class.getMethod("getUseFastFlagManager");
            Method setMethod = FastFlagsViewModel.class.getMethod("setUseFastFlagManager", boolean.class);

            MethodPair methods = new MethodPair(getMethod, setMethod);
            uiHelpers.addToggle(getTextLocale(context, R.string.menu_fastflags_allowmanagefflags_title), getTextLocale(context, R.string.menu_fastflags_allowmanagefflags_description), methods, -1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

//        try {
//            Method getMethod = FastFlagsViewModel.class.getMethod("isResetConfiguration");
//            Method setMethod = FastFlagsViewModel.class.getMethod("setResetConfiguration", boolean.class);
//
//            MethodPair methods = new MethodPair(getMethod, setMethod);
//            uiHelpers.addToggle(getTextLocale(context, R.string.menu_fastflags_resetconfiguration_title), "", methods, -1);
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }

        uiHelpers.addDivider();
        uiHelpers.addSection(getTextLocale(context, R.string.menu_fastflags_section_presets));

        uiHelpers.addAccordionMenu(getTextLocale(context, R.string.menu_fastflags_accordion_renderinggraphics), "", () -> addR(context, uiHelpers),1);
    }

    public static void addR(Context context, FFlagsSettingsFragment uiHelpers) {
        try {
            JSONArray array = new JSONArray();

            for (MSAAMode mode : MSAAMode.values()) {
                JSONObject obj = new JSONObject()
                        .put("label", mode.getDisplayName())
                        .put("value", mode.name());
                array.put(obj);
            }
            Method getMethod = FastFlagsViewModel.class.getMethod("getSelectedMSAALevel");
            Method setMethod = FastFlagsViewModel.class.getMethod("setSelectedMSAALevel", String.class);

            MethodPair methods = new MethodPair(getMethod, setMethod);
            uiHelpers.addDropdown(getTextLocale(context, R.string.menu_fastflags_msaa_title), "", array, methods, 1);

        } catch (JSONException e) {
//            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            Method getMethod = FastFlagsViewModel.class.getMethod("isGraySky");
            Method setMethod = FastFlagsViewModel.class.getMethod("setGraySky", boolean.class);

            MethodPair methods = new MethodPair(getMethod, setMethod);
            uiHelpers.addToggle(getTextLocale(context, R.string.menu_fastflags_completelygraysky_title), "", methods, 1);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray array = new JSONArray();

            for (RenderingMode mode : RenderingMode.values()) {
                JSONObject obj = new JSONObject()
                        .put("label", mode.getDisplayName())
                        .put("value", mode.name());
                array.put(obj);
            }

            Method getMethod = FastFlagsViewModel.class.getMethod("getSelectedRenderingMode");
            Method setMethod = FastFlagsViewModel.class.getMethod("setSelectedRenderingMode", String.class);

            MethodPair methods = new MethodPair(getMethod, setMethod);
            uiHelpers.addDropdown(getTextLocale(context, R.string.menu_fastflags_renderingmode_title), "", array, methods, 1);

        } catch (JSONException e) {
//            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            JSONArray array = new JSONArray();

            for (TextureMode mode : TextureMode.values()) {
                JSONObject obj = new JSONObject()
                        .put("label", mode.getDisplayName())
                        .put("value", mode.name());
                array.put(obj);
            }

            Method getMethod = FastFlagsViewModel.class.getMethod("getSelectedTextureQuality");
            Method setMethod = FastFlagsViewModel.class.getMethod("setSelectedTextureQuality", String.class);

            MethodPair methods = new MethodPair(getMethod, setMethod);
            uiHelpers.addDropdown(getTextLocale(context, R.string.menu_fastflags_texturequality_title), "", array, methods, 1);

        } catch (JSONException e) {
//            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getTextLocale(Context context, int resId) {
        return context.getString(resId);
    }
}
