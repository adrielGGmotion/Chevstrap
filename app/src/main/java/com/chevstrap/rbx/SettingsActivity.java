package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.*;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import android.widget.Toast;

import com.chevstrap.rbx.UI.Elements.Settings.Pages.AboutOneFragment;
import com.chevstrap.rbx.UI.Elements.Settings.Pages.BehaviourFragment;
import com.chevstrap.rbx.UI.Elements.Settings.Pages.FFlagsSettingsFragment;
import com.chevstrap.rbx.UI.Elements.Settings.Pages.FFlagsEditorFragment;
import com.chevstrap.rbx.UI.Elements.Settings.Pages.IntegrationsFragment;
import com.chevstrap.rbx.UI.Frontend;
import com.chevstrap.rbx.Utility.FileTool;
import com.chevstrap.rbx.UI.Elements.Settings.Pages.ChevstrapFragment;

import androidx.fragment.app.Fragment;

import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity {
	private String currentPage = null;
    private TextView textview5;
	private com.google.android.material.button.MaterialButton button_save;
	private com.google.android.material.button.MaterialButton button_saveandlaunch;
    private com.google.android.material.button.MaterialButton button_close;
	private final List<ButtonClickListener> buttonClickListener = new ArrayList<>();
    private LinearLayout linear20;

	private interface ButtonClickListener {
		void onQueryChanged(String query);
	}

    @Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		setContentView(R.layout.settings);

		try {
			initialize();
			initializeLogic();
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				showMessageBoxUnsavedChanges();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		App.setTempActivityWatcherClass(null);
	}

	private void initialize() {
		textview5 = findViewById(R.id.textview5);
		button_save = findViewById(R.id.button_save);
		button_saveandlaunch = findViewById(R.id.button_saveandlaunch);
		button_close = findViewById(R.id.button_close);
        com.google.android.material.button.MaterialButtonToggleGroup toggleGroup = findViewById(R.id.toggle_group);
        linear20 = findViewById(R.id.linear20);

		Installer installer = new Installer();
		installer.HandleUpgrades();


		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_integrations_title), "Integrations", toggleGroup);
		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_fastflags_title), "Flags Settings", toggleGroup);
		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_fastflageditor_title), "Flags Editor", toggleGroup);
		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_behaviour_title), "Launcher", toggleGroup);
		addButton(this, "Chevstrap", "Settings", toggleGroup);
		addButton(this, getTextLocale(App.getAppContext(), R.string.about_title), "About", toggleGroup);

		button_close.setOnClickListener(_view -> {
			showMessageBoxUnsavedChanges();
		});

		button_saveandlaunch.setOnClickListener(_view -> {
			Launcher launcher = new Launcher();
			launcher.setFragmentManager(getSupportFragmentManager());
			try {
				App.getConfig().save();
				launcher.Run();
			} catch (Exception e) {
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
        });

		button_save.setOnClickListener(_view -> {
			App.getConfig().save();
		});
	}

	private void initializeLogic() {
		movePage("Flags Settings");
	}

	public void addButton(Context context, String name, String MovePageTo, com.google.android.material.button.MaterialButtonToggleGroup parent) {
		com.google.android.material.button.MaterialButton buttonView = (com.google.android.material.button.MaterialButton) LayoutInflater.from(context).inflate(R.layout.button_menu_settingspage, parent, false);

		buttonView.setText(name);
		buttonView.setOnClickListener(v -> {
			movePage(MovePageTo);
		});

		buttonClickListener.add(query -> {
			buttonView.setChecked(Objects.equals(query, MovePageTo));
		});

		parent.addView(buttonView);
	}

	public void movePage(String whatpage) {
		if (whatpage.equals(currentPage)) {
			return;
		}

		for (ButtonClickListener listener : buttonClickListener) {
			listener.onQueryChanged(whatpage);
		}

		Fragment fragment = null;
		String title = null;

		try {
			switch (whatpage) {
				case "Flags Editor":
					fragment = new FFlagsEditorFragment();
					title = App.getTextLocale(App.getAppContext(), R.string.menu_fastflageditor_title);
					break;
				case "Flags Settings":
					fragment = new FFlagsSettingsFragment();
					title = App.getTextLocale(App.getAppContext(), R.string.menu_fastflags_title);
					break;
				case "Integrations":
					fragment = new IntegrationsFragment();
					title = App.getTextLocale(App.getAppContext(), R.string.menu_integrations_title);
					break;
				case "Launcher":
					fragment = new BehaviourFragment();
					title = App.getTextLocale(App.getAppContext(), R.string.menu_behaviour_title);
					break;
				case "Settings":
					fragment = new ChevstrapFragment();
					title = App.getTextLocale(App.getAppContext(), R.string.common_chevstrap);
					break;
				case "About":
					fragment = new AboutOneFragment();
					title = App.getTextLocale(App.getAppContext(), R.string.about_title);
					break;
				default:
					throw new IllegalArgumentException("Unknown page: " + whatpage);
			}
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}

		if (fragment != null) {
			currentPage = whatpage;
			textview5.setText(title);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.linear20, fragment)
					.commit();
			fadeIn(findViewById(R.id.linear20));
			animateTranslationY(findViewById(R.id.linear20));
		}
	}

	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}

	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}

	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}

	private void animateTranslationY(View view) {
		view.setTranslationY(50f);
		view.animate()
				.translationY(0f)
				.setDuration(300)
				.start();
	}

	public void fadeIn(View view) {
		if (view != null) {
			view.setAlpha(0f);
			view.setVisibility(View.VISIBLE);
			view.animate()
					.alpha(1f)
					.setDuration(300)
					.start();
		}
	}

	public void showMessageBoxUnsavedChanges() {
		File clientSettingsDirLocation = new File(String.valueOf(App.getConfig().getFileLocation()));
		JSONObject savedFlagsJson = null;

		try {
			if (!FileTool.isExist(String.valueOf(clientSettingsDirLocation))) {
				savedFlagsJson = new JSONObject();
			}
			String fileContent = FileTool.read(clientSettingsDirLocation);
			JSONObject rootObject = new JSONObject(fileContent);
			savedFlagsJson = rootObject.optJSONObject("fflags");
		} catch (Exception ignored) {}

		String currentFlags = null;
		String savedFlags = savedFlagsJson != null ? savedFlagsJson.toString() : "";

		try {
			JSONObject root = new JSONObject(App.getConfig().getProp());
			JSONObject fflags = root.optJSONObject("fflags");

			if (fflags != null) {
				Iterator<String> keys = fflags.keys();
				ArrayList<String> toRemove = new ArrayList<>();

				while (keys.hasNext()) {
					String key = keys.next();
					if (fflags.isNull(key) || fflags.opt(key) == null) {
						toRemove.add(key);
					}
				}

				for (String key : toRemove) {
					fflags.remove(key);
				}

				currentFlags = fflags.toString();
			} else {
				currentFlags = "{}";
			}
		} catch (Exception e) {
			App.getLogger().writeException("SettingsActivity::showMessageBoxUnsavedChanges", e);
		}

		assert currentFlags != null;
        if (!currentFlags.equals(savedFlags)) {
			Frontend.ShowMessageBoxWithRunnable(App.getSavedFragmentActivity(), App.getTextLocale(App.getAppContext(), R.string.dialog_unsaved_changes), true, this::finish, () -> {});
		} else {
			finish();
		}
	}


	private static String getTextLocale(Context context, int resId) {
		return context.getString(resId);
	}
}