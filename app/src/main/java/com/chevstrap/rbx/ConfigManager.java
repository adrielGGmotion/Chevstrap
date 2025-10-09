package com.chevstrap.rbx;

import android.util.Log;

import com.chevstrap.rbx.Enums.FlagPresets.*;
import com.chevstrap.rbx.Enums.RobloxAppType;
import com.chevstrap.rbx.Enums.ThemeRecreated;
import com.chevstrap.rbx.Models.Persistable.ChevstrapSettings;

import java.io.*;
import java.util.*;

public class ConfigManager extends JsonManager<Map<String, Object>> {
    private static ConfigManager instance;

    private ConfigManager() {
        this.prop = new LinkedHashMap<>();
        this.originalProp = new LinkedHashMap<>();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public Map<String, Object> getProp() {
        return prop;
    }
    public void setProp(Map<String, Object> prop) {
        this.prop = prop;
    }

    public static final Map<String, String> PresetFlags;
    static {
        Map<String, String> flags = new LinkedHashMap<>();
        flags.put("Rendering.MSAA", "FIntDebugForceMSAASamples");
        flags.put("Rendering.GraySky", "FFlagDebugSkyGray");

        flags.put("Rendering.Mode.Vulkan", "FFlagDebugGraphicsPreferVulkan");
        flags.put("Rendering.Mode.OpenGL", "FFlagDebugGraphicsPreferOpenGL");

        flags.put("Rendering.TextureQuality.OverrideEnabled", "DFFlagTextureQualityOverrideEnabled");
        flags.put("Rendering.TextureQuality.Level", "DFIntTextureQualityOverride");

        flags.put("UI.Hide", "DFIntCanHideGuiGroupId");

        PresetFlags = Collections.unmodifiableMap(flags);
    }

    public Map<String, String> getPresetFlags() {
        return PresetFlags;
    }

    public static final Map<MSAAMode, String> MSAAModes;
    static {
        Map<MSAAMode, String> map = new EnumMap<>(MSAAMode.class);
        map.put(MSAAMode.Automatic, null);
        map.put(MSAAMode.x1, "1");
        map.put(MSAAMode.x2, "2");
        map.put(MSAAMode.x4, "4");
        MSAAModes = Collections.unmodifiableMap(map);
    }

    public static final Map<TextureMode, String> TextureModes;
    static {
        Map<TextureMode, String> map = new EnumMap<>(TextureMode.class);
        map.put(TextureMode.Automatic, null);
        map.put(TextureMode.x0, "0");
        map.put(TextureMode.x1, "1");
        map.put(TextureMode.x2, "2");
        map.put(TextureMode.x3, "3");
        TextureModes = Collections.unmodifiableMap(map);
    }

    public static final Map<RenderingMode, String> RenderingModes;
    static {
        Map<RenderingMode, String> map = new EnumMap<>(RenderingMode.class);
        map.put(RenderingMode.Automatic, null);
        map.put(RenderingMode.OpenGL, "OpenGL");
        map.put(RenderingMode.Vulkan, "Vulkan");
        RenderingModes = Collections.unmodifiableMap(map);
    }

//    public static final Map<LanguageRecreated, String> LanguagesRecreated;
//    static {
//        Map<LanguageRecreated, String> map = new EnumMap<>(LanguageRecreated.class);
//        map.put(LanguageRecreated.Automatic, null);
//        map.put(LanguageRecreated.Arabic, "ar");
//        map.put(LanguageRecreated.Filipino, "fil");
//        map.put(LanguageRecreated.Hindi, "hi");
//        map.put(LanguageRecreated.Indonesian, "id");
//        map.put(LanguageRecreated.Korean, "ko");
//        map.put(LanguageRecreated.Malay, "ms");
//        map.put(LanguageRecreated.Portuguese, "pt");
//        map.put(LanguageRecreated.Portuguese_Brazilian, "pt-br");
//        map.put(LanguageRecreated.Russian, "ru");
//        map.put(LanguageRecreated.Spanish, "es");
//        map.put(LanguageRecreated.Thai, "th");
//        map.put(LanguageRecreated.Vietnamese, "vi");
//
//        LanguagesRecreateds = Collections.unmodifiableMap(map);
//    }

    public static final Map<ThemeRecreated, String> ThemeRecreateds;
    static {
        Map<ThemeRecreated, String> map = new EnumMap<>(ThemeRecreated.class);
        map.put(ThemeRecreated.dark, "dark");
        map.put(ThemeRecreated.light, "light");
        ThemeRecreateds = Collections.unmodifiableMap(map);
    }

    public static final Map<RobloxAppType, String> RobloxAppTypes;
    static {
        Map<RobloxAppType, String> map = new EnumMap<>(RobloxAppType.class);
        map.put(RobloxAppType.global, "global");
        map.put(RobloxAppType.vn, "vn");
        RobloxAppTypes = Collections.unmodifiableMap(map);
    }

    @Override
    public String getClassName() {
        return "ConfigManager";
    }

    @Override
    public File getFileLocation() {
        File dir = new File(Paths.getLocalAppData());
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                Log.w("Config", "Could not create directory: " + dir.getAbsolutePath());
            }
        }
        return new File(dir, "Config.json");
    }

    public void setSettingValue(String key, String value) {
        String LOG_IDENT = "AppSettings::SetValue";

        if (value == null) {
            if (prop.containsKey(key)) {
                try {
                    App.getLogger().writeLine(LOG_IDENT, "Deletion of " + key + " is pending");
                    prop.remove(key);
                } catch (Exception e) {
                    App.getLogger().writeLine(LOG_IDENT, "Error removing key of " + key);
                    App.getLogger().writeException(LOG_IDENT, e);
                }
            }
        } else {
            if (getProp().containsKey(key)) {
                App.getLogger().writeLine(LOG_IDENT, "Changing of " + key + " from " + prop.get(key) + " to " + value + " is pending");
            } else {
                App.getLogger().writeLine(LOG_IDENT, "Setting of " + key + " to " + value + " is pending");
            }
            prop.put(key, value);
        }
    }

    public void setFFlagValue(String key, String value) {
        final String LOG_IDENT = "ConfigManager::SetValue";

        Object fflagsRaw = prop.get("fflags");
        Map<String, Object> fflags;

        fflags = new LinkedHashMap<>();
        if (fflagsRaw instanceof Map) {
            Map<?, ?> rawMap = (Map<?, ?>) fflagsRaw;
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                if (entry.getKey() instanceof String) {
                    fflags.put((String) entry.getKey(), entry.getValue());
                }
            }
        }

        if (value == null) {
            App.getLogger().writeLine(LOG_IDENT, "Deletion of " + key + " is pending");
            fflags.remove(key);
        } else {
            if (getSettingValue(key) != null) {
                App.getLogger().writeLine(LOG_IDENT, "Changing of " + key + " from " + prop.get(key) + " to " + value + " is pending");
            } else {
                App.getLogger().writeLine(LOG_IDENT, "Setting of " + key + " to " + value + " is pending");
            }
            fflags.put(key, value);
        }
        prop.put("fflags", fflags);
    }

    public String getFFlagValue(String key) {
        Object fflagsRaw = prop.get("fflags");
        if (fflagsRaw instanceof Map<?, ?>) {
            Map<?, ?> fflags = (Map<?, ?>) fflagsRaw;
            Object value = fflags.get(key);
            return value != null ? value.toString() : null;
        }
        return null;
    }

    public String getSettingValue(String key) {
        Object value = prop.get(key);
        return value != null ? value.toString() : null;
    }

    public String getPreset(String name) {
        String LOG_IDENT = "ConfigManager::GetPreset";

        if (!PresetFlags.containsKey(name)) {
            App.getLogger().writeLine(LOG_IDENT, "Could not find preset " + name);
        }

        String flag = PresetFlags.get(name);
        return flag != null ? getFFlagValue(flag) : null;
    }

    public void setPreset(String prefix, String value) {
        for (Map.Entry<String, String> entry : PresetFlags.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                setFFlagValue(entry.getValue(), value);
            }
        }
    }

    public void setPresetEnum(String prefix, String target, String value) {
        for (Map.Entry<String, String> entry : PresetFlags.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                if (entry.getKey().startsWith(prefix + "." + target)) {
                    setFFlagValue(entry.getValue(), value);
                } else {
                    setFFlagValue(entry.getValue(), null);
                }
            }
        }
    }

    public <T extends Enum<T>> T getPresetEnum(Map<T, String> mapping, String prefix, String value) {
        for (Map.Entry<T, String> entry : mapping.entrySet()) {
            String name = entry.getValue();
            if (name == null || name.equals("None")) continue;

            String preset = getPreset(prefix + "." + name);
            if (value.equals(preset)) return entry.getKey();
        }

        if (!mapping.isEmpty()) {
            return mapping.keySet().iterator().next();
        }

        return null;
    }

    @Override
    public void save() {
        super.save();
        originalProp = new LinkedHashMap<>(prop);
    }

    @Override
    public void load(boolean alertFailure) {
        super.load(alertFailure);
        originalProp = new LinkedHashMap<>(prop);

        ConfigManager config = App.getConfig();
        ChevstrapSettings defaults = new ChevstrapSettings(config);
        defaults.applyDefaults();
    }

    @Override
    protected Map<String, Object> createEmptyMap() {
        return new LinkedHashMap<>();
    }
}
