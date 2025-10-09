package com.chevstrap.rbx;

import com.chevstrap.rbx.Utility.FileTool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public abstract class JsonManager<T extends Map<String, Object>> {
    protected T prop;
    protected T originalProp;
    protected boolean loaded = false;

    public abstract String getClassName();
    public abstract File getFileLocation();
    protected abstract T createEmptyMap();

    public boolean isLoaded() {
        return loaded;
    }

    public void save() {
        File file = getFileLocation();

        JSONObject json = new JSONObject(prop);
        try {
            FileTool.write(file, json.toString(4));
        } catch (IOException | JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void load(boolean alertFailure) {
        File file = getFileLocation();

        if (!file.exists()) {
            prop = createEmptyMap();
            loaded = false;
            return;
        }

        JSONObject jsonTemp = new JSONObject();
        try {
            String jsonBuilder = FileTool.read(file);
            jsonTemp = new JSONObject(jsonBuilder);
        } catch (JSONException ignored) {} catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            prop = convertJsonObjectToMap(jsonTemp);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        loaded = true;
    }

    private T convertJsonObjectToMap(JSONObject jsonObject) throws JSONException {
        T map = createEmptyMap();

        JSONArray keys = jsonObject.names();
        if (keys == null) return map;

        for (int i = 0; i < keys.length(); i++) {
            String key = keys.getString(i);
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                value = convertJsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = convertJsonArrayToList((JSONArray) value);
            }

            map.put(key, value);
        }

        return map;
    }

    private List<Object> convertJsonArrayToList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);

            if (value instanceof JSONObject) {
                value = convertJsonObjectToMap((JSONObject) value);
            } else if (value instanceof JSONArray) {
                value = convertJsonArrayToList((JSONArray) value);
            }

            list.add(value);
        }

        return list;
    }
}
