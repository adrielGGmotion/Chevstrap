package com.chevstrap.rbx.UI.Elements.CustomDialogs;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.Extensions.CustomUIComponents;
import com.chevstrap.rbx.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class EditFlagFragment extends DialogFragment {
	private LinearLayout linear1;
	private TextView textview3;
	private String TargetFlagIs;

	public interface onEditFlagListener {
		void onEditFlag(String key, String value, String oldFlagName);
	}
	private onEditFlagListener listener;
	public void setEditFlagListener(onEditFlagListener listener) {
		this.listener = listener;
	}

	public void setTargetFlagClickedToEdit(String GetTargetFlagClicked) {
		TargetFlagIs = GetTargetFlagClicked;
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.editflag_fragment, container, false);
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
		linear1 = view.findViewById(R.id.linear1);
		LinearLayout linear_center_list1 = view.findViewById(R.id.linear_center_list1);
		LinearLayout linear_center_list2 = view.findViewById(R.id.linear_center_list2);
		LinearLayout linear_bottom_list = view.findViewById(R.id.linear_bottom_list);

		View button_ok = addButton(App.getTextLocale(requireContext(), R.string.common_ok), linear_bottom_list);
		View button_close = addButton(App.getTextLocale(requireContext(), R.string.common_close), linear_bottom_list);
		EditText edittext_nameit = addOnlyTextbox("", linear_center_list1);
		EditText edittext_valueit = addOnlyTextbox("", linear_center_list2);

		TextView textview_title = view.findViewById(R.id.textview_title);
		TextView textview_name = view.findViewById(R.id.textview_name);
		TextView textview_value = view.findViewById(R.id.textview_value);

		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
			textview_title.setTextColor(Color.parseColor("#000000"));
			textview_name.setTextColor(Color.parseColor("#000000"));
			textview_value.setTextColor(Color.parseColor("#000000"));
		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
			textview_title.setTextColor(Color.parseColor("#FFFFFF"));
			textview_name.setTextColor(Color.parseColor("#FFFFFF"));
			textview_value.setTextColor(Color.parseColor("#FFFFFF"));
		}

		edittext_nameit.setText(TargetFlagIs);
		edittext_valueit.setText(App.getConfig().getFFlagValue(TargetFlagIs));

		button_close.setOnClickListener(v -> dismiss());
		edittext_nameit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {}
		});

		button_ok.setOnClickListener(v -> {
			try {
				if (listener != null) {
					listener.onEditFlag(edittext_nameit.getText().toString(), edittext_valueit.getText().toString(), TargetFlagIs);
				}
				dismiss();
			} catch (Exception e) {
				App.getLogger().writeException("EditFlag::Ok", e);
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

//		if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
//			textview3.setTextColor(Color.parseColor("#000000"));
//		} else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
//			textview3.setTextColor(Color.parseColor("#FFFFFF"));
//		}

		linear1.setBackground(drawable);

//		if (!messageText.isEmpty()) {
//			textview3.setText(messageText);
//		}
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
