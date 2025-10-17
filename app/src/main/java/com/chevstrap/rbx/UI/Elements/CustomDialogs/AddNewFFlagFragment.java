package com.chevstrap.rbx.UI.Elements.CustomDialogs;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.Extensions.CustomUIComponents;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.UI.Frontend;
import com.chevstrap.rbx.Utility.FileTool;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddNewFFlagFragment extends DialogFragment {
	private ActivityResultLauncher<String> storagePermissionLauncher;
	private LinearLayout linear1;
	private LinearLayout linear_top_main_tabs;
	private LinearLayout container_addsingle;
	private LinearLayout container_importjson;
	private LinearLayout container_normal;
	private TextView textview3;
	private EditText edittext_importjson;
	private String currentPageIs = "single";
	private final List<ButtonClickListener> buttonClickListener = new ArrayList<>();
	private final Intent JsonFilePicker = new Intent(Intent.ACTION_GET_CONTENT);
	private ActivityResultLauncher<Intent> jsonFilePickerLauncher;

	private interface ButtonClickListener {
		void onQueryChanged(String query);
	}

	public interface OnJsonImportedListener {
		void onJsonImported(String json);
	}

	private OnJsonImportedListener listener;

	public void setOnJsonImportedListener(OnJsonImportedListener listener) {
		this.listener = listener;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_fflag_fragment, container, false);

		try {
			storagePermissionLauncher =
					registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
						if (isGranted) {
							jsonFilePickerLauncher.launch(JsonFilePicker);
						}
					});

			initialize(view);
			initializeLogic();

			jsonFilePickerLauncher = registerForActivityResult(
					new ActivityResultContracts.StartActivityForResult(),
					result -> {
						try {
							if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
								Uri uri = result.getData().getData();
								if (uri != null && getContext() != null) {
									String uriPath = FileTool.convertUriToFilePath(getContext(), uri);
									if (uriPath != null) {
										String fileContent = FileTool.read(new File(uriPath));
										if (edittext_importjson != null) {
											edittext_importjson.setText(fileContent);
										}
									} else {
										throw new Exception("Invalid file path");
									}
								}
							}
						} catch (Exception e) {
							App.getLogger().writeLine("AddNewFFlagFragment::ImportFromFile", "Failed to open file picker");
							App.getLogger().writeException("AddNewFFlagFragment::ImportFromFile", e);

							if (getContext() != null) {
								Frontend.ShowMessageBox(
										App.getSavedFragmentActivity(),
										App.getTextLocale(requireContext(), R.string.dialog_failed_to_importfile)
												+ " " + e.getMessage()
								);
							}
						}
					}
			);
			return view;
		} catch (Exception e) {
			App.getLogger().writeException("AddNewFFlagFragment::onCreateView", e);
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
		linear1 = view.findViewById(R.id.linear1);
		container_normal = view.findViewById(R.id.container_normal);
		linear_top_main_tabs = view.findViewById(R.id.linear_top_main_tabs);
		container_addsingle = view.findViewById(R.id.container_addsingle);
		container_importjson = view.findViewById(R.id.container_importjson);

		LinearLayout linear_center_list1 = view.findViewById(R.id.linear_center_list1);
		LinearLayout linear_center_list2 = view.findViewById(R.id.linear_center_list2);
		LinearLayout linear_bottom_list = view.findViewById(R.id.linear_bottom_list);
		LinearLayout linear_bottom_importjson = view.findViewById(R.id.linear_bottom_importjson);
		LinearLayout linear_center_list_importjson = view.findViewById(R.id.linear_center_list_importjson);

		TextView textview_title = view.findViewById(R.id.textview_title);
		TextView textview_name = view.findViewById(R.id.textview_name);
		TextView textview_value = view.findViewById(R.id.textview_value);

		GradientDrawable drawable = new GradientDrawable();
		drawable.setCornerRadii(new float[]{
				0f, 0f,
				0f, 0f,
				15f, 15f,
				15f, 15f
		});
		drawable.setColor(
				Color.parseColor("#101010")
		);

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			drawable.setColor(
					Color.parseColor("#EFEFEF")
			);
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			drawable.setColor(
					Color.parseColor("#151515")
			);
		}
		linear_bottom_list.setBackground(drawable);

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			textview_title.setTextColor(Color.parseColor("#000000"));
			textview_name.setTextColor(Color.parseColor("#000000"));
			textview_value.setTextColor(Color.parseColor("#000000"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			textview_title.setTextColor(Color.parseColor("#FFFFFF"));
			textview_name.setTextColor(Color.parseColor("#FFFFFF"));
			textview_value.setTextColor(Color.parseColor("#FFFFFF"));
		}

		View button_ok = addButton(App.getTextLocale(requireContext(), R.string.common_ok), linear_bottom_list);
		View button_close = addButton(App.getTextLocale(requireContext(), R.string.common_close), linear_bottom_list);
		View tab_button_single = addTabsButton(App.getTextLocale(requireContext(), R.string.common_addsingle), "single", linear_top_main_tabs);
		View tab_button_importjson = addTabsButton(App.getTextLocale(requireContext(), R.string.common_importjson), "import_json", linear_top_main_tabs);
		EditText edittext_nameit = addOnlyTextbox("", linear_center_list1);
		EditText edittext_valueit = addOnlyTextbox("", linear_center_list2);
		edittext_importjson = addOnlyTextbox("{\n" +
				"  \"FIntDebugForceMSAASamples\": \"1\"\n" +
				"}", linear_center_list_importjson);
		View button_import_from_file = addButton(App.getTextLocale(requireContext(), R.string.common_importfromfile), linear_bottom_importjson);

		JsonFilePicker.setType("*/*");
		JsonFilePicker.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"application/json",
				"text/plain",
				"text/*"});
		JsonFilePicker.addCategory(Intent.CATEGORY_OPENABLE);
		JsonFilePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);

		button_close.setOnClickListener(v -> dismiss());
		edittext_nameit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		button_ok.setOnClickListener(v -> {
			try {
				if (Objects.equals(currentPageIs, "single")) {
					JSONObject json = new JSONObject();
					json.put(edittext_nameit.getText().toString(), edittext_valueit.getText().toString());

					if (listener != null) {
						listener.onJsonImported(json.toString());
					}
					dismiss();
				} else if (Objects.equals(currentPageIs, "import_json")) {
					if (listener != null) {
						listener.onJsonImported(edittext_importjson.getText().toString());
					}
					dismiss();
				}
			} catch (Exception e) {
				App.getLogger().writeException("EditFlag::Ok", e);
			}
		});

		tab_button_single.setOnClickListener(v -> updatePage("single"));
		tab_button_importjson.setOnClickListener(v -> updatePage("import_json"));
		button_import_from_file.setOnClickListener(v -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
				if (!Environment.isExternalStorageManager()) {
					try {
						Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
						intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
						startActivity(intent);
					} catch (Exception e) {
						Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
						startActivity(intent);
					}
				} else {
					jsonFilePickerLauncher.launch(JsonFilePicker);
				}
			} else {
				if (ContextCompat.checkSelfPermission(requireContext(),
						Manifest.permission.READ_EXTERNAL_STORAGE)
						!= PackageManager.PERMISSION_GRANTED) {
					storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
				} else {
					jsonFilePickerLauncher.launch(JsonFilePicker);
				}
			}
		});
	}

	public void updatePage(String PageName) {
		for (ButtonClickListener listener : buttonClickListener) {
			listener.onQueryChanged(PageName);
		}

		if (Objects.equals(PageName, "single")) {
			container_addsingle.setVisibility(View.VISIBLE);
			container_importjson.setVisibility(View.GONE);
		} else if (Objects.equals(PageName, "import_json")) {
			container_addsingle.setVisibility(View.GONE);
			container_importjson.setVisibility(View.VISIBLE);
		}
		currentPageIs = PageName;
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
					Color.parseColor("#151515")
			);
			drawable.setStroke(1, Color.parseColor("#070707"));
		}

		GradientDrawable drawable1 = new GradientDrawable();
		drawable1.setCornerRadius(15);
		drawable1.setColor(
				Color.parseColor("#151515")
		);
		drawable1.setStroke(1, Color.parseColor("#070707"));

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			drawable1.setColor(
					Color.parseColor("#FFFFFF")
			);
			drawable1.setStroke(1, Color.parseColor("#C6C6C6"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			drawable1.setColor(
					Color.parseColor("#101010")
			);
			drawable1.setStroke(5, Color.parseColor("#151515"));
		}
		updatePage("single");

		linear1.setBackground(drawable1);
		container_normal.setBackground(drawable);
	}

    public View addButton(String name, LinearLayout parent) {
		if (getContext() == null) return null;
		CustomUIComponents.SmallButtonResult buttonResult =
				CustomUIComponents.addSmallButton(getContext(), name, parent);

		buttonResult.buttonOne.setPadding(
				buttonResult.buttonOne.getPaddingLeft() + 180,
				buttonResult.buttonOne.getPaddingTop(),
				buttonResult.buttonOne.getPaddingRight() + 180,
				buttonResult.buttonOne.getPaddingBottom()
		);

		if (buttonResult.buttonView.getParent() == null) {
			parent.addView(buttonResult.buttonView);
		}
		return buttonResult.buttonOne;
	}

	public View addTabsButton(String name, String MovePageTo, LinearLayout parent) {
		LayoutInflater inflater = getLayoutInflater();
		View buttonView = inflater.inflate(R.layout.custom_button_very_small, parent, false);
		LinearLayout container = buttonView.findViewById(R.id.button);
		TextView nameView = buttonView.findViewById(R.id.textview);
		ImageView THEimageView = buttonView.findViewById(R.id.imageview);
		nameView.setText(name);

		Runnable applyAsNonSelected = () -> {
			GradientDrawable drawable = new GradientDrawable();
			drawable.setCornerRadii(new float[]{
					30f, 30f,
					30f, 30f,
					0f, 0f,
					0f, 0f
			});

			if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
				drawable.setColor(Color.parseColor("#101010"));
				drawable.setStroke(1, Color.parseColor("#070707"));
			} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
				drawable.setColor(Color.parseColor("#FFFFFF"));
				drawable.setStroke(1, Color.parseColor("#C6C6C6"));
			}
			container.setBackground(drawable);
		};

		Runnable applyAsSelect = () -> {
			GradientDrawable drawable = new GradientDrawable();
			drawable.setCornerRadii(new float[]{
					30f, 30f,
					30f, 30f,
					0f, 0f,
					0f, 0f
			});

			if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
				drawable.setColor(Color.parseColor("#151515"));
				drawable.setStroke(1, Color.parseColor("#070707"));
			} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
				drawable.setColor(Color.parseColor("#EFEFEF"));
				drawable.setStroke(1, Color.parseColor("#C6C6C6"));
			}
			container.setBackground(drawable);
		};

		container.setPadding(
				container.getPaddingLeft() + 30,
				container.getPaddingTop(),
				container.getPaddingRight() + 30,
				container.getPaddingBottom()
		);

		buttonClickListener.add(query -> {
			if (!Objects.equals(query, MovePageTo)) {
				applyAsNonSelected.run();
			} else {
				applyAsSelect.run();
			}
		});

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			nameView.setTextColor(Color.parseColor("#000000"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			nameView.setTextColor(Color.parseColor("#FFFFFF"));
		}
		THEimageView.setVisibility(View.GONE);

		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
		);
		ViewGroup.LayoutParams lp = container.getLayoutParams();
		if (lp instanceof ViewGroup.MarginLayoutParams) {
			ViewGroup.MarginLayoutParams params2 = (ViewGroup.MarginLayoutParams) lp;
			params2.setMargins(0, 0, 0, 0);
			container.setLayoutParams(params2);
		}
		buttonView.setLayoutParams(params1);
		linear_top_main_tabs.addView(buttonView);

		return buttonView;
	}

	public EditText addOnlyTextbox(String name, LinearLayout parent) {
		if (getContext() == null) return null;
		CustomUIComponents.TextboxOnlyResult buttonResult =
				CustomUIComponents.addTextboxOnly(name, parent, 0);

		if (buttonResult.textboxView.getParent() == null) {
			parent.addView(buttonResult.textboxView);
		}

		if (!name.isEmpty()) {
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					250
			);

			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT
			);

			buttonResult.editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
			buttonResult.editText.setSingleLine(false);

			buttonResult.linearTextbox.setLayoutParams(params1);
			buttonResult.editText.setLayoutParams(params2);

			buttonResult.linearTextbox.setGravity(Gravity.TOP);
			buttonResult.editText.setGravity(Gravity.TOP);

			int paddingTopBottomDp = 7;
			float scale = requireContext().getResources().getDisplayMetrics().density;
			int paddingPx = (int) (paddingTopBottomDp * scale + 0.5f);

			buttonResult.linearTextbox.setPadding(paddingPx, paddingPx, paddingPx, paddingPx);

		}
		return buttonResult.editText;
	}
}
