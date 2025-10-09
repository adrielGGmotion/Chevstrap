package com.chevstrap.rbx;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.util.*;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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
    private LinearLayout linear20;
    private TextView textview5;
	private LinearLayout button_save;
	private LinearLayout button_saveandlaunch;
    private LinearLayout button_close;
	private final List<ButtonClickListener> buttonClickListener = new ArrayList<>();

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
        LinearLayout linear1 = findViewById(R.id.linear1);
        LinearLayout linear_dark = findViewById(R.id.linear_dark);
        LinearLayout linear_background = findViewById(R.id.linear_background);

        LinearLayout linear24 = findViewById(R.id.linear24);
        LinearLayout linear024 = findViewById(R.id.linear024);
        LinearLayout linear3 = findViewById(R.id.linear3);
        LinearLayout linear25 = findViewById(R.id.linear25);
        LinearLayout linear22 = findViewById(R.id.linear22);
        LinearLayout linear27 = findViewById(R.id.linear27);
        LinearLayout linear10 = findViewById(R.id.linear10);
        TextView textview1 = findViewById(R.id.textview1);
        ScrollView vscroll1 = findViewById(R.id.vscroll1);

        LinearLayout linear26 = findViewById(R.id.linear26);
		linear20 = findViewById(R.id.linear20);
        TextView textview3 = findViewById(R.id.textview3);
		textview5 = findViewById(R.id.textview5);
		button_save = findViewById(R.id.button_save);
		button_saveandlaunch = findViewById(R.id.button_saveandlaunch);
		button_close = findViewById(R.id.button_close);

		Installer installer = new Installer();
		installer.HandleUpgrades();

		GradientDrawable activeBg = new GradientDrawable();
		activeBg.setCornerRadius(20);

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			activeBg.setColor(Color.parseColor("#101010"));
			activeBg.setStroke(2, Color.parseColor("#181818"));
			linear_background.setBackgroundResource(R.drawable.background_normal);
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			linear_background.setBackgroundResource(R.drawable.background_light);
			activeBg.setStroke(2, Color.parseColor("#E3E8EC"));
			activeBg.setColor(Color.parseColor("#FFFFFF"));
		}

		linear_dark.setBackground(activeBg);
		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			textview5.setTextColor(Color.parseColor("#000000"));
			textview1.setTextColor(Color.parseColor("#000000"));
			linear024.setBackgroundColor(Color.parseColor("#E3E8EC"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			textview5.setTextColor(Color.parseColor("#FFFFFF"));
			textview1.setTextColor(Color.parseColor("#FFFFFF"));
			linear024.setBackgroundColor(Color.parseColor("#181818"));
		}

		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_integrations_title), R.drawable.integrations, R.drawable.integrations_on, R.drawable.integrations_light, "Integrations", linear27);
		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_fastflags_title), R.drawable.fastflag_icon, R.drawable.fastflag_icon_on, R.drawable.fastflag_icon_light, "Flags Settings", linear27);
		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_fastflageditor_title), R.drawable.editor_icon, R.drawable.editor_icon_on, R.drawable.editor_icon_light,  "Flags Editor", linear27);
		addButton(this, getTextLocale(App.getAppContext(), R.string.menu_behaviour_title), R.drawable.bootstrapper_icon, R.drawable.bootstrapper_icon_on, R.drawable.bootstrapper_icon_light,  "Launcher", linear27);
		addDivider(this, linear27);
		addButton(this, "Chevstrap", R.drawable.setting_icon, R.drawable.setting_icon_on, R.drawable.setting_icon_light, "Settings", linear27);
		addButton(this, getTextLocale(App.getAppContext(), R.string.about_title), R.drawable.about_icon, R.drawable.about_icon_on, R.drawable.about_icon_light, "About", linear27);

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
		AstyleButtonBTeal1(button_save);
		AstyleButtonBlack1(button_close);
		AstyleButtonBTeal1(button_saveandlaunch);

		movePage("Flags Settings");
	}

	public static void addDivider(Context context, LinearLayout parent) {
		View divider = new View(context);

		int heightPx = (int) (1 * context.getResources().getDisplayMetrics().density + 0.5f);
		LinearLayout.LayoutParams layoutParams = getLayoutParams(context, heightPx);
		divider.setLayoutParams(layoutParams);

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			divider.setBackgroundColor(Color.parseColor("#BCC9D3"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			divider.setBackgroundColor(Color.parseColor("#323232"));
		}
		parent.addView(divider);
	}

	private static LinearLayout.LayoutParams getLayoutParams(Context context, int heightPx) {
		int marginLeft = (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f);
		int marginTop = (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f);
		int marginRight = (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f);
		int marginBottom = (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				heightPx
		);
		layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
		return layoutParams;
	}

	public void addButton(Context context, String name, int IconResId, int IconResId1, int IconResId_light, String MovePageTo, LinearLayout parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View buttonView = inflater.inflate(R.layout.button_menu_settingspage, parent, false);

		LinearLayout container = buttonView.findViewById(R.id.button_option);
		TextView nameView = buttonView.findViewById(R.id.textview_name);
		ImageView imageView = buttonView.findViewById(R.id.imageview);

		imageView.setBackgroundResource(IconResId);

		nameView.setText(name);
		container.setOnClickListener(v -> {
			movePage(MovePageTo);
		});

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			nameView.setTextColor(Color.parseColor("#000000"));
			imageView.setBackgroundResource(IconResId_light);
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			nameView.setTextColor(Color.parseColor("#FFFFFF"));
			imageView.setBackgroundResource(IconResId);
		}

		AstyleButtonTRANSPARENT(container);
		buttonClickListener.add(query -> {
			if (!Objects.equals(query, MovePageTo)) {
				AstyleButtonTRANSPARENT(container);
				nameView.setVisibility(View.VISIBLE);

				if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
					nameView.setTextColor(Color.parseColor("#000000"));
					imageView.setBackgroundResource(IconResId_light);
				} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
					nameView.setTextColor(Color.parseColor("#FFFFFF"));
					imageView.setBackgroundResource(IconResId);
				}

				container.setPadding(0, 15, 0, 15);
			} else {
				nameView.setVisibility(View.GONE);
				GradientDrawable activeBg = new GradientDrawable();
				activeBg.setCornerRadius(20);
				activeBg.setStroke(0, Color.TRANSPARENT);

				if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
					activeBg.setColor(Color.parseColor("#151515"));
				} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
					activeBg.setColor(Color.parseColor("#FFFFFF"));
				}

				imageView.setBackgroundResource(IconResId1);
				container.setBackground(activeBg);
				container.setPadding(0, 53, 0, 53);
			}
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
			fadeIn(linear20);
			animateTranslationY(linear20);
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

	public void AstyleButtonBTeal1(LinearLayout button) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(30);
		drawable.setColor(Color.parseColor("#38A181"));
		button.setBackground(drawable);
	}

	public void AstyleButtonBlack1(LinearLayout button) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(30);
		drawable.setColor(Color.parseColor("#1B1B1B"));
		button.setBackground(drawable);
	}

	public void AstyleButtonTRANSPARENT(LinearLayout button) {
		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadius(5);
		drawable.setColor(Color.TRANSPARENT);
		button.setBackground(drawable);
	}

	private static String getTextLocale(Context context, int resId) {
		return context.getString(resId);
	}
}