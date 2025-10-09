package com.chevstrap.rbx.UI.ViewModels.Settings;

import com.chevstrap.rbx.Enums.FlagPresets.MSAAMode;
import com.chevstrap.rbx.Enums.FlagPresets.RenderingMode;
import com.chevstrap.rbx.Enums.FlagPresets.TextureMode;
import com.chevstrap.rbx.ConfigManager;
import com.chevstrap.rbx.App;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FastFlagsViewModel {
    private final ConfigManager manager;
    private Map<String, Object> preResetFlags;

    public FastFlagsViewModel() {
        this.manager = App.getConfig();
    }

    public boolean getUseFastFlagManager() {
        return Boolean.parseBoolean(manager.getSettingValue("use_fflags_manager"));
    }

    public void setUseFastFlagManager(boolean value) {
        manager.setSettingValue("use_fflags_manager", String.valueOf(value));
    }

    public String getSelectedMSAALevel() {
        String value =  manager.getPreset("Rendering.MSAA");
        for (Map.Entry<MSAAMode, String> entry : ConfigManager.MSAAModes.entrySet()) {
            if (Objects.equals(entry.getValue(), value)) {
                return entry.getKey().name();
            }
        }
        return MSAAMode.Automatic.name();
    }

    public void setSelectedMSAALevel(String modeName) {
        if (modeName == null) {
            manager.setPreset("Rendering.MSAA", null);
            return;
        }

        try {
            MSAAMode mode = MSAAMode.valueOf(modeName);
            manager.setPreset("Rendering.MSAA", ConfigManager.MSAAModes.get(mode));
        } catch (IllegalArgumentException e) {
            manager.setPreset("Rendering.MSAA", null);
        }
    }

    public String getSelectedRenderingMode() {
        RenderingMode mode =  manager.getPresetEnum(
                ConfigManager.RenderingModes,
                "Rendering.Mode",
                "True"
        );
        return mode != null ? mode.name() : ConfigManager.RenderingModes.keySet().iterator().next().name();
    }

    public void setSelectedRenderingMode(String modeName) {
        manager.setPresetEnum("Rendering.Mode", ConfigManager.RenderingModes.get(RenderingMode.valueOf(modeName)), "True");
    }

    public String getSelectedTextureQuality() {
        String current =  manager.getPreset("Rendering.TextureQuality.Level");
        for (Map.Entry<TextureMode, String> entry : ConfigManager.TextureModes.entrySet()) {
            if (Objects.equals(entry.getValue(), current)) return entry.getKey().name();
        }
        return ConfigManager.TextureModes.keySet().iterator().next().name();
    }

    public void setSelectedTextureQuality(String modeName) {
        TextureMode quality = TextureMode.valueOf(modeName);
        if (quality == TextureMode.Automatic) {
            manager.setPreset("Rendering.TextureQuality", null);
        } else {
            manager.setPreset("Rendering.TextureQuality.OverrideEnabled", "True");
            manager.setPreset("Rendering.TextureQuality.Level", ConfigManager.TextureModes.get(quality));
        }
    }

    public boolean isGraySky() {
        return "True".equals(App.getConfig().getPreset("Rendering.GraySky"));
    }

    public void setGraySky(boolean value) {
        App.getConfig().setPreset("Rendering.GraySky", value ? "True" : null);
    }

//    public boolean isResetConfiguration() {
//        return preResetFlags != null;
//    }
//
//    public void setResetConfiguration(boolean reset) {
//        if (reset) {
//            preResetFlags = new HashMap<>(manager.getProp());
//             manager.getProp().clear();
//        } else {
//            manager.setProp(preResetFlags);
//            preResetFlags = null;
//        }
//    }
}