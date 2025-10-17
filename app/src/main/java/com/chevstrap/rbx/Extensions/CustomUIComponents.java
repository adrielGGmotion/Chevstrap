package com.chevstrap.rbx.Extensions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.Models.MethodPair;
import com.chevstrap.rbx.R;
import com.chevstrap.rbx.SettingsActivity;
import com.chevstrap.rbx.UI.ViewModels.GlobalViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

public class CustomUIComponents {
    public static class TextboxResult {
        public final View textboxView;
        public final EditText editText;
        public final TextView nameTextView;

        public TextboxResult(View textboxView, EditText editText, TextView nameTextView) {
            this.textboxView = textboxView;
            this.editText = editText;
            this.nameTextView = nameTextView;
        }
    }

    public static class TextboxOnlyResult {
        public final View textboxView;
        public final EditText editText;
        public final LinearLayout linearTextbox;
        public TextboxOnlyResult(View textboxView, LinearLayout linearTextbox, EditText editText) {
            this.textboxView = textboxView;
            this.linearTextbox = linearTextbox;
            this.editText = editText;
        }
    }

    public static class DropdownResult {
        public final View dropDownView;
        public final Spinner spinner;
        public final ArrayAdapter<String> adapter;
        public final TextView nameTextView;

        public DropdownResult(View dropDownView, Spinner spinner, ArrayAdapter<String> adapter, TextView nameTextView) {
            this.dropDownView = dropDownView;
            this.spinner = spinner;
            this.adapter = adapter;
            this.nameTextView = nameTextView;;
        }
    }

    public static class ToggleResult {
        public final View toggleView;
        public final ImageView toggleSwitch;
        public final TextView nameTextView;

        public ToggleResult(View toggleView, ImageView toggleSwitch, TextView nameTextView) {
            this.toggleView = toggleView;
            this.toggleSwitch = toggleSwitch;
            this.nameTextView = nameTextView;
        }
    }

    public static class ButtonResult {
        public final View buttonView;
        public final LinearLayout buttonOne;
        public final TextView nameTextView;

        public ButtonResult(View buttonView, LinearLayout buttonOne, TextView nameTextView) {
            this.buttonView = buttonView;
            this.buttonOne = buttonOne;
            this.nameTextView = nameTextView;
        }
    }

    public static class AccordionMenuResult  {
        public final View buttonView;
        public final LinearLayout buttonOne;
        public final LinearLayout expandContainer;
        public final LinearLayout expandContainer1;
        public final TextView nameTextView;

        public AccordionMenuResult(View buttonView, LinearLayout buttonOne, TextView nameTextView, LinearLayout expandContainer, LinearLayout expandContainer1) {
            this.buttonView = buttonView;
            this.buttonOne = buttonOne;
            this.expandContainer = expandContainer;
            this.expandContainer1 = expandContainer1;
            this.nameTextView = nameTextView;
        }
    }

    public static class SmallButtonResult {
        public final View buttonView;
        public final LinearLayout buttonOne;
        public final TextView nameTextView;
        public final ImageView AnImageView;

        public SmallButtonResult(View buttonView, LinearLayout buttonOne, TextView nameTextView, ImageView AnImageView) {
            this.buttonView = buttonView;
            this.buttonOne = buttonOne;
            this.nameTextView = nameTextView;
            this.AnImageView = AnImageView;
        }
    }

    public static class DividerResult {
        public final View dividerView;

        public DividerResult(View dividerView) {
            this.dividerView = dividerView;
        }
    }

    public static class SectionResult {
        public final FrameLayout sectionContainer;
        public final TextView nameTextView;

        public SectionResult(FrameLayout container, TextView textView) {
            this.sectionContainer = container;
            this.nameTextView = textView;
        }
    }

    public static SectionResult addSection(Context context, String text) {
        FrameLayout container = new FrameLayout(context);
        int heightTargetIs = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (text == null) {
            heightTargetIs = 0;
        }
        container.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                heightTargetIs
        ));

        TextView sectionText = new TextView(context);
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textParams.setMargins(10, 25, 10, 5);
        sectionText.setLayoutParams(textParams);
        sectionText.setText(text);
        sectionText.setTextSize(14);
        sectionText.setTypeface(null, Typeface.BOLD);
        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            sectionText.setTextColor(Color.parseColor("#000000"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            sectionText.setTextColor(Color.parseColor("#FFFFFF"));
        }

        container.addView(sectionText);
        return new SectionResult(container, sectionText);
    }

    public static DividerResult addDivider(Context context) {
        View divider = new View(context);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                5
        );

        layoutParams.setMargins(10, 25, 10, 20);
        divider.setLayoutParams(layoutParams);

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            divider.setBackgroundColor(Color.parseColor("#E3E8EC"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            divider.setBackgroundColor(Color.parseColor("#181818"));
        }

        return new DividerResult(divider);
    }

    public static TextboxResult addTextbox(Context context, String name, String description, LinearLayout parent, MethodPair selectedMethod, String TypeIs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View textboxView = inflater.inflate(R.layout.textbox_advanced, parent, false);

        LinearLayout container = textboxView.findViewById(R.id.linear_hey);
        EditText input = textboxView.findViewById(R.id.edittext);
        LinearLayout linearTextbox = textboxView.findViewById(R.id.linear_edittext13);
        TextView nameView = textboxView.findViewById(R.id.textview_name_option);
        TextView descView = textboxView.findViewById(R.id.textview_iswhatda);

        AstyleButton1(container);
        StyleTextbox(linearTextbox);
        nameView.setText(name);
        descView.setText(description);

        if (description.isEmpty()) {
            descView.setVisibility(View.GONE);
        }

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            nameView.setTextColor(Color.parseColor("#000000"));
            descView.setTextColor(Color.parseColor("#545454"));
            input.setTextColor(Color.parseColor("#000000"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            nameView.setTextColor(Color.parseColor("#FFFFFF"));
            descView.setTextColor(Color.parseColor("#ABABAB"));
            input.setTextColor(Color.parseColor("#FFFFFF"));
        }

        if (TypeIs.equals("number")) {
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else if (TypeIs.equals("string")) {
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        Class<?> clazz = selectedMethod.getMethod.getDeclaringClass();
        Object viewModel;

        try {
            viewModel = clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        try {
            Object valtemp = selectedMethod.getMethod.invoke(viewModel);
            String value = (String) valtemp;

            if (valtemp != null) {
                if (!value.isEmpty()) {
                    input.setText(value);
                } else {
                    input.setText("");
                }
            } else {
                input.setText("");
            }
        } catch (Exception ignored) {}

        input.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String val = s.toString();
                try {
                    selectedMethod.setMethod.invoke(viewModel, val);
                } catch (Exception ignored) {}
            }
        });

        return new TextboxResult(textboxView, input, nameView);
    }

    public static TextboxOnlyResult addTextboxOnly(String placeholderText, LinearLayout parent, int imageResId) {
        Context context = App.getAppContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View textboxView = inflater.inflate(R.layout.custom_the_textbox, parent, false);

        EditText input = textboxView.findViewById(R.id.edittext1);
        LinearLayout linearTextbox = textboxView.findViewById(R.id.linear1);
        ImageView imageview1 = textboxView.findViewById(R.id.imageview1);
        StyleTextbox(linearTextbox);

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            imageview1.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            imageview1.clearColorFilter();
        }

        if (imageResId != 0) {
            int sizeInPx = (int) (25 * context.getResources().getDisplayMetrics().density);
            ViewGroup.LayoutParams params = imageview1.getLayoutParams();
            params.width = sizeInPx;
            params.height = sizeInPx;
            imageview1.setLayoutParams(params);
            imageview1.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageview1.setImageResource(imageResId);
            imageview1.setVisibility(View.VISIBLE);
        } else {
            imageview1.setVisibility(View.GONE);
        }

        input.setHint(placeholderText);
        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            input.setTextColor(Color.parseColor("#000000"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            input.setTextColor(Color.parseColor("#FFFFFF"));
        }
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        return new TextboxOnlyResult(textboxView, linearTextbox, input);
    }

    public static DropdownResult addDropdown(Context context, String name, String description, LinearLayout parent, final JSONArray jsonArray, MethodPair selectedMethod) {
        final ArrayList<String> labels = new ArrayList<>();

        LayoutInflater inflater = LayoutInflater.from(context);
        View dropDownView = inflater.inflate(R.layout.dropdown_advanced, parent, false);

        LinearLayout container = dropDownView.findViewById(R.id.linear_hey);
        Spinner spinner = dropDownView.findViewById(R.id.spinnercustom);
        TextView nameView = dropDownView.findViewById(R.id.textview_name_option);
        TextView descView = dropDownView.findViewById(R.id.textview_iswhatda);

        AstyleButton1(container);
        StyleDropdown(spinner);
        nameView.setText(name);
        descView.setText(description);

        if (description.isEmpty()) {
            descView.setVisibility(View.GONE);
        }

        GradientDrawable popupBg = new GradientDrawable();
        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            nameView.setTextColor(Color.parseColor("#000000"));
            descView.setTextColor(Color.parseColor("#545454"));

            popupBg.setColor(Color.parseColor("#FFFFFF"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            nameView.setTextColor(Color.parseColor("#FFFFFF"));
            descView.setTextColor(Color.parseColor("#ABABAB"));

            popupBg.setColor(Color.parseColor("#070707"));
        }
        popupBg.setShape(GradientDrawable.RECTANGLE);
        popupBg.setCornerRadius(20f);

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                labels.add(jsonArray.getJSONObject(i).getString("label"));
            } catch (JSONException ignored) { }
        }

        spinner.setPopupBackgroundDrawable(popupBg);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.custom_dropdown, R.id.textview1, labels) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(R.id.textview1);

                if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
                    tv.setTextColor(Color.parseColor("#000000"));
                } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return view;
            }

            @NonNull
            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = view.findViewById(R.id.textview1);

                if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
                    tv.setTextColor(Color.parseColor("#000000"));
                } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
                    tv.setTextColor(Color.parseColor("#FFFFFF"));
                }
                return view;
            }
        };
        spinner.setAdapter(adapter);

        Class<?> clazz = selectedMethod.getMethod.getDeclaringClass();
        Object viewModel;

        try {
            viewModel = clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        String TargetSelected;
        try {
            TargetSelected = (String) selectedMethod.getMethod.invoke(viewModel);
        } catch (Exception ignored) {
            TargetSelected = null;
        }

        if (TargetSelected != null && TargetSelected.isEmpty()) {
            try {
                JSONObject first = jsonArray.getJSONObject(0);
                TargetSelected = first.optString("value", "");
            } catch (JSONException e) {
                TargetSelected = "";
            }
        }

        int index = -1;
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject obj = jsonArray.getJSONObject(i);
                assert TargetSelected != null;
                if (TargetSelected.equals(obj.getString("value"))) {
                    String label = obj.getString("label");
                    index = adapter.getPosition(label);
                    break;
                }
            } catch (JSONException ignored) { }
        }

        if (index >= 0) {
            spinner.setSelection(index);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                try {
                    JSONObject selected = jsonArray.getJSONObject(position);
                    String value = selected.optString("value");
                    selectedMethod.setMethod.invoke(viewModel, value);

                } catch (JSONException ignored) {
                } catch (Exception e) {
//					e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        return new DropdownResult(dropDownView, spinner, adapter, nameView);
    }

    public static ToggleResult addToggle(Context context, String name, String description, LinearLayout parent, MethodPair selectedMethod) {
        Class<?> clazz = selectedMethod.getMethod.getDeclaringClass();
        Object instance;

        try {
            instance = clazz.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }

        final boolean[] toggleState = {false};

        LayoutInflater inflater = LayoutInflater.from(context);
        View toggleView = inflater.inflate(R.layout.toggle_advanced, parent, false);

        LinearLayout container = toggleView.findViewById(R.id.linear_hey);
        ImageView toggleSwitch = toggleView.findViewById(R.id.imageview_switch);
        TextView nameView = toggleView.findViewById(R.id.textview_name_option);
        TextView descView = toggleView.findViewById(R.id.textview_iswhatda);

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            nameView.setTextColor(Color.parseColor("#000000"));
            descView.setTextColor(Color.parseColor("#545454"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            nameView.setTextColor(Color.parseColor("#FFFFFF"));
            descView.setTextColor(Color.parseColor("#ABABAB"));
        }

        AstyleButton1(container);
        nameView.setText(name);
        descView.setText(description);

        if (description.isEmpty()) {
            descView.setVisibility(View.GONE);
        }

        boolean lastStatusToggle = false;

        try {
            Object result = selectedMethod.getMethod.invoke(instance);
            if (result instanceof Boolean) {
                lastStatusToggle = (Boolean) result;
            }
        } catch (Exception e) {
//			e.printStackTrace();
        }

        toggleState[0] = lastStatusToggle;
        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            toggleSwitch.setBackgroundResource(toggleState[0] ? R.drawable.toggle_on_light : R.drawable.toggle_off_light);
        } else {
            toggleSwitch.setBackgroundResource(toggleState[0] ? R.drawable.toggle_on : R.drawable.toggle_off);
        }

        toggleSwitch.setOnClickListener(v -> {
            toggleState[0] = !toggleState[0];

            if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
                toggleSwitch.setBackgroundResource(toggleState[0] ? R.drawable.toggle_on_light : R.drawable.toggle_off_light);
            } else {
                toggleSwitch.setBackgroundResource(toggleState[0] ? R.drawable.toggle_on : R.drawable.toggle_off);
            }
            try {
                selectedMethod.setMethod.invoke(instance, toggleState[0]);
            } catch (Exception e) {
//				e.printStackTrace();
            }
        });
        return new ToggleResult(toggleView, toggleSwitch, nameView);
    }

    public static ButtonResult addButton(Context context, String name, String description, LinearLayout parent, String command, String typeCommand) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View buttonView = inflater.inflate(R.layout.button_advanced, parent, false);

        LinearLayout container = buttonView.findViewById(R.id.linear_hey);
        TextView nameView = buttonView.findViewById(R.id.textview_name_option);
        TextView descView = buttonView.findViewById(R.id.textview_iswhatda);

        AstyleButton1(container);
        nameView.setText(name);
        descView.setText(description);

        if (description.isEmpty()) {
            descView.setVisibility(View.GONE);
        }

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            nameView.setTextColor(Color.parseColor("#000000"));
            descView.setTextColor(Color.parseColor("#545454"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            nameView.setTextColor(Color.parseColor("#FFFFFF"));
            descView.setTextColor(Color.parseColor("#ABABAB"));
        }

        container.setOnClickListener(v -> {
            if (Objects.equals(typeCommand, "link")) {
                GlobalViewModel.openWebpage(context, command);
            }
        });

        return new ButtonResult(buttonView, container, nameView);
    }

    public static AccordionMenuResult addAccordionMenu(Context context, String name, String description, LinearLayout parent, Runnable AFunction) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View buttonView = inflater.inflate(R.layout.accordion_menu, parent, false);

        LinearLayout container = buttonView.findViewById(R.id.linear_hey);
        LinearLayout expanderContainer = buttonView.findViewById(R.id.linear_container_expander);
        LinearLayout realExpander = buttonView.findViewById(R.id.linear_expander);
        TextView nameView = buttonView.findViewById(R.id.textview_name_option);
        TextView descView = buttonView.findViewById(R.id.textview_iswhatda);
        ImageView imageview_button = buttonView.findViewById(R.id.imageview_button);

        AstyleButton1(container);
        nameView.setText(name);
        descView.setText(description);

        if (description.isEmpty()) {
            descView.setVisibility(View.GONE);
        }

        imageview_button.setBackgroundResource(R.drawable.arrow_down);
        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            nameView.setTextColor(Color.parseColor("#000000"));
            descView.setTextColor(Color.parseColor("#545454"));

            imageview_button.setColorFilter(Color.parseColor("#000000"));

            GradientDrawable drawable1 = new GradientDrawable();
            drawable1.setCornerRadii(new float[]{
                    0f, 0f,
                    0f, 0f,
                    30f, 30f,
                    30f, 30f
            });

            drawable1.setColor(Color.parseColor("#45555F"));
            expanderContainer.setBackground(drawable1);
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            nameView.setTextColor(Color.parseColor("#FFFFFF"));
            descView.setTextColor(Color.parseColor("#ABABAB"));

            imageview_button.setColorFilter(Color.parseColor("#FFFFFF"));

            GradientDrawable drawable1 = new GradientDrawable();
            drawable1.setCornerRadii(new float[]{
                    0f, 0f,
                    0f, 0f,
                    30f, 30f,
                    30f, 30f
            });

            drawable1.setColor(Color.parseColor("#070707"));
            expanderContainer.setBackground(drawable1);
        }

        expanderContainer.setVisibility(View.GONE);
        container.setOnClickListener(v -> {
            if (expanderContainer.getVisibility() == View.GONE) {
                if (realExpander.getChildCount() == 0) {
                    AFunction.run();
                }

                expanderContainer.measure(
                        View.MeasureSpec.makeMeasureSpec(container.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );
                int targetHeight = expanderContainer.getMeasuredHeight();

                expanderContainer.getLayoutParams().height = 0;
                expanderContainer.setVisibility(View.VISIBLE);
                expanderContainer.animate()
                        .setDuration(100)
                        .setUpdateListener(animation -> {
                            float fraction = animation.getAnimatedFraction();
                            expanderContainer.getLayoutParams().height = (int) (targetHeight * fraction);
                            expanderContainer.requestLayout();
                        })
                        .withEndAction(() -> expanderContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT)
                        .start();

            } else {
                int initialHeight = expanderContainer.getHeight();
                expanderContainer.animate()
                        .setDuration(100)
                        .setUpdateListener(animation -> {
                            float fraction = animation.getAnimatedFraction();
                            expanderContainer.getLayoutParams().height = (int) (initialHeight * (1f - fraction));
                            expanderContainer.requestLayout();
                        })
                        .withEndAction(() -> expanderContainer.setVisibility(View.GONE))
                        .start();
            }
        });

        return new AccordionMenuResult(buttonView, container, nameView, realExpander, expanderContainer);
    }

    public static SmallButtonResult addSmallButton(Context context, String name, LinearLayout parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View buttonView = inflater.inflate(R.layout.custom_button_very_small, parent, false);
        LinearLayout container = buttonView.findViewById(R.id.button);
        TextView nameView = buttonView.findViewById(R.id.textview);
        ImageView THEimageView = buttonView.findViewById(R.id.imageview);
        nameView.setText(name);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(30);

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            drawable.setColor(Color.parseColor("#1B1B1B"));
            drawable.setStroke(1, Color.parseColor("#070707"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            drawable.setColor(Color.parseColor("#FFFFFF"));
            drawable.setStroke(1, Color.parseColor("#C6C6C6"));
        }

        container.setBackground(drawable);

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
        nameView.setTextColor(Color.parseColor("#000000"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
        nameView.setTextColor(Color.parseColor("#FFFFFF"));
        }
        THEimageView.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        buttonView.setLayoutParams(params);
        return new SmallButtonResult(buttonView, container, nameView, THEimageView);
    }

    public static void AstyleButton1(LinearLayout button) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(30);
        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            drawable.setColor(Color.parseColor("#1B1B1B"));
            drawable.setStroke(2, Color.parseColor("#070707"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            drawable.setColor(Color.parseColor("#F0F3F5"));
            drawable.setStroke(2, Color.parseColor("#EAEDEF"));
        }
        button.setBackground(drawable);
    }

    public static void StyleDropdown(Spinner spinner) {
        spinner.post(() -> spinner.setBackground(buildSpinnerBackground(spinner)));
    }

    private static Drawable buildSpinnerBackground(Spinner spinner) {
        GradientDrawable gradient = new GradientDrawable();
        gradient.setCornerRadius(30);

        boolean isDark = Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark");

        if (isDark) {
            gradient.setColor(Color.parseColor("#262626"));
            gradient.setStroke(2, Color.parseColor("#252525"));
        } else {
            gradient.setStroke(2, Color.parseColor("#DADADA"));
            gradient.setColor(Color.parseColor("#FFFFFF"));
        }

        Drawable arrow = ContextCompat.getDrawable(spinner.getContext(), R.drawable.arrow_down);
        if (arrow != null) {
            int arrowColor = isDark ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");

            arrow.setTint(arrowColor);
            return buildLayerDrawable(spinner, gradient, arrow);
        }

        return gradient;
    }

    private static LayerDrawable buildLayerDrawable(Spinner spinner, Drawable background, Drawable arrow) {
        int arrowSize = (int) (arrow.getIntrinsicWidth() * 0.05);
        int rightPadding = 25;

        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{background, arrow});
        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(
                1,
                spinner.getWidth() - arrowSize - rightPadding,
                (spinner.getHeight() - arrowSize) / 2,
                rightPadding,
                (spinner.getHeight() - arrowSize) / 2
        );
        return layerDrawable;
    }

    public static void StyleTextbox(LinearLayout textbox) {
        GradientDrawable normal = new GradientDrawable();
        normal.setCornerRadius(20);

        if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "dark")) {
            normal.setColor(Color.parseColor("#101010"));
            normal.setStroke(2, Color.parseColor("#202020"));
        } else if (Objects.equals(App.getConfig().getSettingValue("app_theme_in_app"), "light")) {
            normal.setColor(Color.parseColor("#FFFFFF"));
            normal.setStroke(2, Color.parseColor("#DFDFDF"));
        }

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_enabled}, normal);
        states.addState(new int[]{}, normal);

        textbox.setBackground(states);
    }

}