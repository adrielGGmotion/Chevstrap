package com.chevstrap.rbx.UI.Elements.CustomDialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.Extensions.CustomUIComponents;
import com.chevstrap.rbx.Paths;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.Utility.FileTool;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FFlagsBackupsFragment extends DialogFragment {
	private static String targetFlagClicked;
	private final ArrayList<HashMap<String, Object>> list_map_backups = new ArrayList<>();
	private LinearLayout linear1;
	private TextView textview3;

	public interface onBackupFlagListener {
		void onBackupFlag(String json);
	}
	private onBackupFlagListener listener;
	public void setonBackupFlagListener(onBackupFlagListener listener) {
		this.listener = listener;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fflagsbackups_fragment, container, false);
		try {
			initialize(view);
			initializeLogic();
			return view;
		} catch (Exception e) {
			return null;
		}
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);

		if (dialog.getWindow() != null) {
			dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		}
		return dialog;

	}

	private void initialize(View view) {
		ListView listviewFlagsList = view.findViewById(R.id.listview);
		linear1 = view.findViewById(R.id.linear1);
		LinearLayout linear_up_list = view.findViewById(R.id.linear_up_list);
		LinearLayout linear_center_list = view.findViewById(R.id.linear_center_list);
		LinearLayout linear_bottom_list = view.findViewById(R.id.linear_bottom_list);

		View button_load_selected_backup = addButton(App.getTextLocale(requireContext(), R.string.backups_load_selected_backup), linear_up_list);
		View button_delete_selected = addButton(App.getTextLocale(requireContext(), R.string.backups_delete_selected_backup), linear_up_list);
		View button_save_a_backup = addButton(App.getTextLocale(requireContext(), R.string.backups_save_backup), linear_bottom_list);
		View button_close = addButton(App.getTextLocale(requireContext(), R.string.common_close), linear_bottom_list);
		EditText edittext_nameit = addOnlyTextbox("", linear_center_list);

		TextView textview_iswhatda = view.findViewById(R.id.textview_iswhatda);

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			textview_iswhatda.setTextColor(Color.parseColor("#000000"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			textview_iswhatda.setTextColor(Color.parseColor("#FFFFFF"));
		}

		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(20);
		drawable.setColor(Color.parseColor("#070707"));
		listviewFlagsList.setBackground(drawable);

		button_close.setOnClickListener(v -> {
			dismiss();
			targetFlagClicked = null;
		});

		File profilesDirectory = new File(Paths.getBackups());
		if (!profilesDirectory.exists()) {
			boolean created = profilesDirectory.mkdirs();
			if (!created) {
				return;
			}
		}
		File[] profiles = null;

		try {
			profiles = FileTool.listFiles(profilesDirectory);
		} catch (Exception ignored) {}

		if (profiles != null && profiles.length > 0) {
			for (File rawProfile : profiles) {
				String profileName = rawProfile.getName();

				HashMap<String, Object> map = new HashMap<>();
				map.put("name", profileName);
				list_map_backups.add(map);
			}

			listviewFlagsList.setAdapter(new ListviewFlagAdapter(list_map_backups));
			((BaseAdapter) listviewFlagsList.getAdapter()).notifyDataSetChanged();
		}

		button_save_a_backup.setOnClickListener(v -> {
			String backupName = edittext_nameit.getText().toString().trim();

			if (backupName.isEmpty()) {
				return;
			}

			try {
				JSONObject rootObject = new JSONObject(App.getConfig().getProp());
				if (rootObject.has("fflags")) {
					JSONObject fflagsObject = rootObject.getJSONObject("fflags");

					File backupDir = new File(Paths.getBackups());
					if (!backupDir.exists() && !backupDir.mkdirs()) {
						App.getLogger().writeLine("Backups::SaveBackup", "Failed to create backup folder");
						return;
					}

					File backupFile = new File(backupDir, backupName);
					try {
						if (!FileTool.isExist(backupFile.getAbsolutePath())) {
							FileTool.write(backupFile, fflagsObject.toString());
							App.getLogger().writeLine("Backups::SaveBackup", backupName);

							HashMap<String, Object> map = new HashMap<>();
							map.put("name", backupName);
							list_map_backups.add(map);

							listviewFlagsList.setAdapter(new ListviewFlagAdapter(list_map_backups));
							((BaseAdapter) listviewFlagsList.getAdapter()).notifyDataSetChanged();
						} else {
							throw new IOException("File already exists");
						}
					} catch (Exception e) {
						App.getLogger().writeLine("Backups::CreateABackup", "Failed to create a backup");
						App.getLogger().writeException("Backups::CreateABackup", e);
					}
				} else {
					App.getLogger().writeLine("Backups::Save", "No 'fflags' key found in JSON.");
				}
			} catch (Exception e) {
				App.getLogger().writeException("Backups::Save", e);
			}
		});

		button_load_selected_backup.setOnClickListener(v -> {
			try {
				String backupContent = FileTool.read(new File(profilesDirectory, targetFlagClicked));
				App.getLogger().writeLine("Backups::LoadSelectedBackup", new File(profilesDirectory, targetFlagClicked).getName());
				JSONObject fflagsObject = new JSONObject(backupContent);

				if (listener != null) {
					listener.onBackupFlag(fflagsObject.toString());
				}
			} catch (Exception e) {
				App.getLogger().writeLine("Backups::LoadSelectedBackup", "Error loading or parsing backup");
				App.getLogger().writeException("Backups::", e);
			}
		});

		button_delete_selected.setOnClickListener(v -> {
			try {
				File backupFile = new File(profilesDirectory, targetFlagClicked);
				FileTool.deleteFile(backupFile);

				for (int i = 0; i < list_map_backups.size(); i++) {
					Object name = list_map_backups.get(i).get("name");
					if (name != null && name.equals(targetFlagClicked)) {
						list_map_backups.remove(i);
						break;
					}
				}
				((BaseAdapter) listviewFlagsList.getAdapter()).notifyDataSetChanged();
				App.getLogger().writeLine("Backups::DeleteSelectedBackup", backupFile.getName());
			} catch (Exception e) {
				App.getLogger().writeLine("Backups::DeleteSelectedBackup", "Error deleting backup");
				App.getLogger().writeException("Backups::DeleteSelectedBackup", e);
			}
		});


	}

	private void initializeLogic() {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(15);
		drawable.setColor(
				Color.parseColor("#101010")
		);
		drawable.setStroke(5, Color.parseColor("#151515"));

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			drawable.setColor(
					Color.parseColor("#EFEFEF")
			);
			drawable.setStroke(5, Color.parseColor("#EAEAEA"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			drawable.setColor(
					Color.parseColor("#101010")
			);
			drawable.setStroke(5, Color.parseColor("#151515"));
		}
		linear1.setBackground(drawable);
	}

	public static class ListviewFlagAdapter extends BaseAdapter {
		private final ArrayList<HashMap<String, Object>> data;

		public ListviewFlagAdapter(ArrayList<HashMap<String, Object>> data) {
			this.data = data;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public HashMap<String, Object> getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.fastflag_editor_item, parent, false);
			}

			LinearLayout linear1 = convertView.findViewById(R.id.linear1);
			TextView textview_name = convertView.findViewById(R.id.textview_name);
			TextView textview_value = convertView.findViewById(R.id.textview_value);

			Map<String, Object> item = data.get(position);
			String flagName = item.get("name") != null ? Objects.requireNonNull(item.get("name")).toString() : "N/A";

			textview_name.setLayoutParams(new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
			textview_value.setLayoutParams(new LinearLayout.LayoutParams(
					0, LinearLayout.LayoutParams.MATCH_PARENT, 1f));
			textview_name.setText(flagName);

			if (targetFlagClicked != null && targetFlagClicked.equals(flagName)) {
				linear1.setBackgroundColor(Color.parseColor("#3B4550"));
			} else {
				linear1.setBackgroundColor(Color.TRANSPARENT);
			}

			linear1.setOnClickListener(v -> {
				if (flagName.equals(targetFlagClicked)) {
					targetFlagClicked = null;
				} else {
					targetFlagClicked = flagName;
				}
				notifyDataSetChanged();
			});

			return convertView;
		}
	}

	public View addButton(String name, LinearLayout parent) {
		if (getContext() == null) return null;
		CustomUIComponents.SmallButtonResult buttonResult =
				CustomUIComponents.addSmallButton(getContext(), name, parent);

		buttonResult.buttonOne.setPadding(
				buttonResult.buttonOne.getPaddingLeft() + 80,
				buttonResult.buttonOne.getPaddingTop(),
				buttonResult.buttonOne.getPaddingRight() + 80,
				buttonResult.buttonOne.getPaddingBottom()
		);

		if (buttonResult.buttonView.getParent() == null) {
			parent.addView(buttonResult.buttonView);
		}
		return buttonResult.buttonOne;
	}

	public EditText addOnlyTextbox(String name, LinearLayout parent) {
		if (getContext() == null) return null;
		CustomUIComponents.TextboxOnlyResult buttonResult =
				CustomUIComponents.addTextboxOnly(name, parent, 0);

		if (buttonResult.textboxView.getParent() == null) {
			parent.addView(buttonResult.textboxView);
		}
		return buttonResult.editText;
	}
}
