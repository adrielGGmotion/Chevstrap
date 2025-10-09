package com.chevstrap.rbx.UI.Elements.Settings.Pages;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.Extensions.CustomUIComponents;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.AddNewFFlagFragment;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.EditFlagFragment;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.FFlagsBackupsFragment;
import com.chevstrap.rbx.UI.Elements.CustomDialogs.MessageboxFragment;
import com.chevstrap.rbx.UI.Frontend;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class FFlagsEditorFragment extends Fragment implements AddNewFFlagFragment.OnJsonImportedListener, EditFlagFragment.onEditFlagListener, FFlagsBackupsFragment.onBackupFlagListener {
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static String searchQuery = "";
    private static Boolean hidePreset = true;
    private static String targetFlagClicked;
    private static Boolean isAllowedDeleteSelected = false;
    private final ArrayList<HashMap<String, Object>> list_map_fflag = new ArrayList<>();
    private ListviewFlagAdapter adapter;
    private ListView listview_flag;
    private TextView textview4;
    private LinearLayout linear3;

    private static void runOnUiThread(Runnable action) {
        mainHandler.post(action);
    }

    private static Boolean isShowPreset(String flag) {
        return App.getConfig().getPresetFlags().containsValue(flag);
    }

    private static String getTextLocale(Context context, int resId) {
        return context.getString(resId);
    }

    public void onJsonImported(String json) {
        importJson(json);
    }

    public void onBackupFlag(String json) {
        try {
            deleteAllFlags();
            importJson(json);
        } catch (Exception e) {
            App.getLogger().writeException("FFlagsEditor::OnBackupFlag", e);
        }
    }

    public void onEditFlag(String key, String value, String oldkeyis) {
        int indexToUpdate = -1;

        if (oldkeyis != null) {
            for (int i = 0; i < list_map_fflag.size(); i++) {
                if (oldkeyis.equals(list_map_fflag.get(i).get("name"))) {
                    indexToUpdate = i;
                    break;
                }
            }
        } else {
            for (int i = 0; i < list_map_fflag.size(); i++) {
                if (key.equals(list_map_fflag.get(i).get("name"))) {
                    indexToUpdate = i;
                    break;
                }
            }
        }

        if (indexToUpdate != -1) {
            HashMap<String, Object> map = list_map_fflag.get(indexToUpdate);
            App.getConfig().setFFlagValue((String) map.get("name"), null);

            map.put("name", key);
            map.put("value", value);

            App.getConfig().setFFlagValue(key, String.valueOf(value));
        } else if (key != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("name", key);
            map.put("value", value);

            list_map_fflag.add(map);
            App.getConfig().setFFlagValue(key, String.valueOf(value));
        }

        listview_flag.setAdapter(new ListviewFlagAdapter(list_map_fflag, hidePreset));
        ((BaseAdapter) listview_flag.getAdapter()).notifyDataSetChanged();
    }

    public void openAddFFlagDialog() {
        try {
            AddNewFFlagFragment dialog = new AddNewFFlagFragment();
            dialog.setOnJsonImportedListener(this);
            dialog.show(getChildFragmentManager(), "AddFastFlagDialog");
        } catch (Exception e) {
            App.getLogger().writeLine("FFlagsEditor::OpenAddFFlagDialog", "error");
            App.getLogger().writeException("FFlagsEditor::OpenAddFFlagDialog", e);
        }
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fastflags_editor_fragment, container, false);
        initialize(view);
        initializeLogic();

        App.setSavedFragmentActivity(this.requireActivity());
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout linear_for_searchbar = view.findViewById(R.id.linear_for_searchbar);
        CustomUIComponents.TextboxOnlyResult searchBarObjects = createSearchBar(getTextLocale(App.getAppContext(), R.string.menu_fastflags_search), linear_for_searchbar, R.drawable.ic_search_white);

        searchBarObjects.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.filter(editable.toString());
            }
        });
    }

    private void initialize(View view) {
        listview_flag = view.findViewById(R.id.listview_flag);
        linear3 = view.findViewById(R.id.linear3);

        LinearLayout linear_hehe = view.findViewById(R.id.linear_hehe);
        LinearLayout linear4 = view.findViewById(R.id.linear4);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(20);
        drawable.setColor(Color.parseColor("#070707"));
        linear_hehe.setBackground(drawable);

        GradientDrawable drawable1 = new GradientDrawable();
        drawable1.setCornerRadii(new float[]{
                20f, 20f,
                20f, 20f,
                0f, 0f,
                0f, 0f
        });

        drawable1.setColor(Color.parseColor("#1B1B1B"));
        linear4.setBackground(drawable1);

        try {
            FFlagsEditor.addEveryPresets(this);
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (!"true".equals(App.getConfig().getSettingValue("do_not_show_fastflags_editor_warning"))) {
            Frontend.ShowMessageBoxWithRunnable(
                    requireActivity(),
                    App.getTextLocale(requireContext(), R.string.menu_fastflageditor_warning_text), true,
                    () -> App.getConfig().setSettingValue("do_not_show_fastflags_editor_warning", "true"),
                    null
            );
        }
    }

    private void initializeLogic() {
        new Thread(() -> {
            try {
                JSONObject rootObject = new JSONObject(App.getConfig().getProp());

                if (rootObject.has("fflags")) {
                    JSONObject fflagsObject = rootObject.getJSONObject("fflags");

                    Iterator<String> keys = fflagsObject.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        Object value = fflagsObject.get(key);

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("name", key);
                        map.put("value", value);

                        list_map_fflag.add(map);
                    }
                }
            } catch (Exception e) {
                App.getLogger().writeException("FFlagsEditor::Initialize", e);
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                adapter = new ListviewFlagAdapter(list_map_fflag, hidePreset);
                listview_flag.setAdapter(adapter);
            });
        }).start();
    }

    public void askDeleteAllFlags() {
        Frontend.ShowMessageBoxWithRunnable(
                App.getSavedFragmentActivity(),
                App.getTextLocale(App.getAppContext(), R.string.dialog_delete_all_fflags_warning),
                true, this::deleteAllFlags, null);
    }
    public void deleteAllFlags() {
        runOnUiThread(() -> {
            Map<String, Object> prop = App.getConfig().getProp();
            Object fflagsObj = prop.get("fflags");
            if (fflagsObj instanceof Map) {
                Map<?, ?> fflagsMap = (Map<?, ?>) fflagsObj;
                fflagsMap.clear();
            }

            list_map_fflag.clear();
            listview_flag.setAdapter(new ListviewFlagAdapter(list_map_fflag, hidePreset));
            ((BaseAdapter) listview_flag.getAdapter()).notifyDataSetChanged();
        });
    }

//    public void share(String path, String type) {
//        File file = new File(path);
//        Uri uri;
//        uri = Uri.parse(file.getPath());
//        Intent intent;
//        try {
//            intent = new Intent(Intent.ACTION_SEND);
//            intent.setType(type);
//            intent.putExtra(Intent.EXTRA_STREAM, uri);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        startActivity(Intent.createChooser(intent, "Share Fast Flags"));
//    }

    public void importJson(String json) {
        if (json == null) {
            Frontend.ShowMessageBox(requireActivity(), "Input JSON is null");
            return;
        }

        json = json.trim();

        if (!json.startsWith("{")) {
            json = "{" + json;
        }

        if (!json.endsWith("}")) {
            int lastIndex = json.lastIndexOf('}');
            if (lastIndex == -1) {
                json += "}";
            } else {
                json = json.substring(0, lastIndex + 1);
            }
        }

        try {
            JSONObject jsonObject = new JSONObject(json);
            HashMap<String, Object> tempMap = new HashMap<>();

            boolean isConflict = false;
            StringBuilder listFlags = new StringBuilder();

            HashSet<String> existingNames = new HashSet<>();
            for (HashMap<String, Object> existing : list_map_fflag) {
                Object nameObj = existing.get("name");
                if (nameObj instanceof String) {
                    existingNames.add((String) nameObj);
                }
            }

            Iterator<String> keysIter = jsonObject.keys();
            while (keysIter.hasNext()) {
                String key = keysIter.next();
                Object value = jsonObject.get(key);

                tempMap.put(key, value);

                if (existingNames.contains(key)) {
                    if (listFlags.length() > 0) {
                        listFlags.append(", ");
                    }
                    listFlags.append(key);
                    isConflict = true;
                }
            }

            if (isConflict) {
                MessageboxFragment dialog = new MessageboxFragment();
                dialog.setMessageText(getTextLocale(App.getAppContext(), R.string.menu_fastflageditor_conflictingimport)
                        + listFlags);
                dialog.setMessageboxListener(new MessageboxFragment.MessageboxListener() {
                    @Override
                    public void onOkClicked() {
                        dialog.dismiss();
                        applyImportedFlags(tempMap, true);
                    }

                    @Override
                    public void onCancelClicked() {
                        dialog.dismiss();
                        applyImportedFlags(tempMap, false);
                    }
                });

                dialog.show(requireActivity().getSupportFragmentManager(), "messagebox");
            } else {
                applyImportedFlags(tempMap, true);
            }

        } catch (Exception e) {
            Frontend.ShowExceptionDialog(requireActivity(),
                    getTextLocale(App.getAppContext(), R.string.menu_fastflageditor_invalidjson), e);
        }
    }

    private void applyImportedFlags(HashMap<String, Object> flags, boolean overwrite) {
        HashMap<String, HashMap<String, Object>> existingMapByName = new HashMap<>();
        for (HashMap<String, Object> map : list_map_fflag) {
            Object nameObj = map.get("name");
            if (nameObj instanceof String) {
                existingMapByName.put((String) nameObj, map);
            }
        }

        for (Map.Entry<String, Object> entry : flags.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            boolean exists = existingMapByName.containsKey(key);

            if (overwrite || !exists) {
                if (exists) {
                    list_map_fflag.remove(existingMapByName.get(key));
                }

                HashMap<String, Object> map = new HashMap<>();
                map.put("name", key);
                map.put("value", value);

                list_map_fflag.add(map);
                App.getConfig().setFFlagValue(key, String.valueOf(value));
            }
        }

        listview_flag.setAdapter(new ListviewFlagAdapter(list_map_fflag, hidePreset));
        ((BaseAdapter) listview_flag.getAdapter()).notifyDataSetChanged();
    }

    public void addSmallButton(Context context, int id, int imageResId, String name, View.OnClickListener clickListener) {
        CustomUIComponents.SmallButtonResult buttonResult =
                CustomUIComponents.addSmallButton(getContext(), name, linear3);

        View buttonView = buttonResult.buttonView;
        LinearLayout container = buttonResult.buttonOne;
        TextView nameView = buttonResult.nameTextView;
        ImageView THEimageView = buttonResult.AnImageView;

        nameView.setText(name);
        final boolean[] isAlreadyClicked = {false};

        if (id == 5) {
            isAlreadyClicked[0] = !hidePreset;
        }

        if (nameView.getText().toString().isEmpty()) {
            nameView.setVisibility(View.GONE);
        }

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            THEimageView.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            THEimageView.clearColorFilter();
        }

        if (imageResId != 0) {
            int sizeInPx = (int) (15 * context.getResources().getDisplayMetrics().density);
            ViewGroup.LayoutParams params = THEimageView.getLayoutParams();
            params.width = sizeInPx;
            params.height = sizeInPx;
            THEimageView.setLayoutParams(params);
            THEimageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            THEimageView.setImageResource(imageResId);
            THEimageView.setVisibility(View.VISIBLE);
        } else {
            THEimageView.setVisibility(View.GONE);
        }

        Runnable applyBackground = () -> {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setCornerRadius(30);

            if (id == 5 && isAlreadyClicked[0]) {
                drawable.setColor(Color.parseColor("#38a181"));
                drawable.setStroke(1, Color.parseColor("#C6C6C6"));
            } else {
                if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
                    drawable.setColor(Color.parseColor("#1B1B1B"));
                    drawable.setStroke(1, Color.parseColor("#101010"));
                } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
                    drawable.setColor(Color.parseColor("#FFFFFF"));
                    drawable.setStroke(1, Color.parseColor("#C6C6C6"));
                }
            }
            container.setBackground(drawable);
        };

        applyBackground.run();

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            nameView.setTextColor(Color.parseColor("#000000"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            nameView.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (clickListener != null) {
            if (id == 5) {
                container.setOnClickListener(v -> {
                    isAlreadyClicked[0] = !isAlreadyClicked[0];
                    applyBackground.run();

                    clickListener.onClick(v);
                });
            } else {
                container.setOnClickListener(clickListener);
            }

            container.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(@NonNull View v) {
                }

                @Override
                public void onViewDetachedFromWindow(@NonNull View v) {
                    container.setOnClickListener(null);
                    container.removeOnAttachStateChangeListener(this);
                }
            });
        }
        linear3.addView(buttonView);
    }

    public void deleteSelected() {
        if (isAllowedDeleteSelected && targetFlagClicked != null) {
            int indexToRemove = -1;
            for (int i = 0; i < list_map_fflag.size(); i++) {
                if (targetFlagClicked.equals(list_map_fflag.get(i).get("name"))) {
                    indexToRemove = i;
                    break;
                }
            }

            if (indexToRemove != -1) {
                HashMap<String, Object> removed = list_map_fflag.remove(indexToRemove);
                App.getConfig().setFFlagValue((String) removed.get("name"), null);
            }

            targetFlagClicked = null;
            isAllowedDeleteSelected = false;

            listview_flag.setAdapter(new ListviewFlagAdapter(list_map_fflag, hidePreset));
            ((BaseAdapter) listview_flag.getAdapter()).notifyDataSetChanged();
        }
    }

    public void copyAll() {
        try {
            JSONObject jsonObject = new JSONObject();
            for (HashMap<String, Object> map : list_map_fflag) {
                String name = (String) map.get("name");
                Object value = map.get("value");
                assert name != null;
                jsonObject.put(name, value);
            }

            String jsonString = jsonObject.toString(4);
            ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("FFlags JSON", jsonString);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            App.getLogger().writeLine("FFlagsEditor::CopyAll", "Copied to clipboard");
        } catch (Exception e) {
            App.getLogger().writeException("FFlagsEditor::CopyAll", e);
        }
    }

    public void openBackupsMenu() {
        try {
            FFlagsBackupsFragment dialog = new FFlagsBackupsFragment();
            dialog.setonBackupFlagListener(this);
            dialog.show(requireActivity().getSupportFragmentManager(), "FlagBackupsDialog");
        } catch (Exception ignored) {
//            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.filter("");
            adapter.setHidePreset(false);
        }
    }


    public void DoShowPreset() {
        hidePreset = !hidePreset;
        adapter.setHidePreset(hidePreset);
    }

    public void showEditFlagMenu(String flagName) {
        try {
            EditFlagFragment dialog = new EditFlagFragment();
            dialog.setEditFlagListener(this);
            dialog.setTargetFlagClickedToEdit(flagName);
            dialog.show(getChildFragmentManager(),  "EditFlagDialog");
        } catch (Exception e) {
            Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public CustomUIComponents.TextboxOnlyResult createSearchBar(String name, LinearLayout parent, int imageResId) {
        CustomUIComponents.TextboxOnlyResult getCustomTextbox = CustomUIComponents.addTextboxOnly(name, parent, imageResId);
        parent.addView(getCustomTextbox.textboxView);

        return getCustomTextbox;
    }

    public class ListviewFlagAdapter extends BaseAdapter {
        private final ArrayList<HashMap<String, Object>> originalData;
        private final ArrayList<HashMap<String, Object>> filteredData = new ArrayList<>();
        private boolean hidePreset;

        public ListviewFlagAdapter(ArrayList<HashMap<String, Object>> originalData, boolean hidePreset) {
            this.originalData = originalData;
            this.hidePreset = hidePreset;
            applyFilter();
        }

        public void setHidePreset(boolean hidePreset) {
            this.hidePreset = hidePreset;
            applyFilter();
        }

        public void filter(String query) {
            searchQuery = query != null ? query.toLowerCase().trim() : "";
            applyFilter();
        }

        private void applyFilter() {
            filteredData.clear();
            for (HashMap<String, Object> item : originalData) {
                if (item == null) continue;

                String flagName = Objects.toString(item.get("name")).trim();
                if (flagName.isEmpty()) {
                    return;
                }

                boolean isPreset = !isShowPreset(flagName);

                if ((!hidePreset || isPreset) &&
                        (searchQuery.isEmpty() || flagName.toLowerCase().contains(searchQuery))) {
                    filteredData.add(item);
                }
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return filteredData.size();
        }

        @Override
        public HashMap<String, Object> getItem(int position) {
            return filteredData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fastflag_editor_item, parent, false);

                holder = new ViewHolder();
                holder.linear1 = convertView.findViewById(R.id.linear1);
                holder.textview_name = convertView.findViewById(R.id.textview_name);
                holder.textview_value = convertView.findViewById(R.id.textview_value);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textview_name.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
            holder.textview_value.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));

            Map<String, Object> item = filteredData.get(position);
            String flagName = Objects.toString(item.get("name"));
            String flagValue = Objects.toString(item.get("value"));

            holder.textview_name.setText(flagName);
            holder.textview_value.setText(flagValue);

            int desiredColor = (targetFlagClicked != null && targetFlagClicked.equals(flagName))
                    ? Color.parseColor("#3B4550")
                    : Color.TRANSPARENT;
            holder.linear1.setBackgroundColor(desiredColor);
            holder.linear1.setOnClickListener(v -> {
                if (!flagName.equals(targetFlagClicked)) {
                    targetFlagClicked = flagName;
                    isAllowedDeleteSelected = true;
                }
                notifyDataSetChanged();
            });

            holder.linear1.setOnLongClickListener(v -> {
                if (flagName.equals(targetFlagClicked)) {
                    showEditFlagMenu(flagName);
                }
                return true;
            });

            return convertView;
        }

        private class ViewHolder {
            LinearLayout linear1;
            TextView textview_name, textview_value;
        }
    }

    private void showToast(final String message) {
        if (isAdded()) {
            requireActivity().runOnUiThread(() ->
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
            );
        }
    }

}