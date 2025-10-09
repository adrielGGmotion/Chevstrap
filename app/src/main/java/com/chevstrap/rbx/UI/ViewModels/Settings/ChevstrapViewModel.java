package com.chevstrap.rbx.UI.ViewModels.Settings;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.ConfigManager;
import com.chevstrap.rbx.Enums.RobloxAppType;
import com.chevstrap.rbx.Enums.ThemeRecreated;

import java.util.Map;
import java.util.Objects;

public class ChevstrapViewModel {
    private final ConfigManager manager;
    public ChevstrapViewModel() {
        this.manager = App.getConfig();
    }

    public String getapp_theme_in_app() {
        String value =  manager.getSettingValue("app_theme_in_app");
        for (Map.Entry<ThemeRecreated, String> entry : ConfigManager.ThemeRecreateds.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey().name();
            }
        }
        return ConfigManager.ThemeRecreateds.keySet().iterator().next().name();
    }

    public void setapp_theme_in_app(String modeName) {
        if (modeName == null) {
            return;
        }

        try {
            ThemeRecreated mode = ThemeRecreated.valueOf(modeName);
            manager.setSettingValue("app_theme_in_app", ConfigManager.ThemeRecreateds.get(mode));
        } catch (IllegalArgumentException e) {
            manager.setSettingValue("app_theme_in_app", "dark");
        }
    }


    public String get_preferred_roblox_app_type() {
        String value =  manager.getSettingValue("preferred_roblox_app_type");
        for (Map.Entry<RobloxAppType, String> entry : ConfigManager.RobloxAppTypes.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey().name();
            }
        }
        return ConfigManager.RobloxAppTypes.keySet().iterator().next().name();
    }

    public void set_preferred_roblox_app_type(String modeName) {
        if (modeName == null) {
            return;
        }

        try {
            RobloxAppType mode = RobloxAppType.valueOf(modeName);
            manager.setSettingValue("preferred_roblox_app_type", ConfigManager.RobloxAppTypes.get(mode));
        } catch (IllegalArgumentException e) {
            manager.setSettingValue("preferred_roblox_app_type", "global");
        }
    }

//    public String getAppLanguage() {
//        String value = manager.getSettingValue("locale");
//        for (Map.Entry<LanguageRecreateds, String> entry : ConfigManager.LanguagesRecreateds.entrySet()) {
//            if (Objects.equals(entry.getValue(), value)) {
//                return entry.getKey().name();
//            }
//        }
//
//        return ConfigManager.LanguagesRecreateds.keySet().iterator().next().name();
//    }
//
//    public void setAppLanguage(String languageName) {
//        if (languageName == null) {
//            manager.setSettingValue("locale", "en");
//            return;
//        }
//
//        try {
//            LanguageRecreateds language = LanguageRecreateds.valueOf(languageName);
//            manager.setSettingValue("locale", ConfigManager.LanguagesRecreateds.get(language));
//        } catch (IllegalArgumentException e) {
//            manager.setSettingValue("locale", "en");
//        }
//    }

//    public String getTheLocale() {
//        try {
//            return manager.getSettingValue("locale");
//        } catch (Exception e) {
//            return null;
//        }
//    }

    public void setTheLocale(String value) {
        manager.setSettingValue("locale", value);
    }
}