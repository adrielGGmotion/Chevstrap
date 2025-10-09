package com.chevstrap.rbx.UI.Elements.Settings.Pages;

import android.content.Context;

import com.chevstrap.rbx.App;
import com.chevstrap.rbx.R;

public class FFlagsEditor {

    public static void addEveryPresets(FFlagsEditorFragment fragment) {
        Context context = App.getAppContext();

        fragment.addSmallButton(context, 1,
                R.drawable.add_icon,
                getTextLocale(context, R.string.menu_fastflageditor_addnew),
                v -> fragment.openAddFFlagDialog());
        fragment.addSmallButton(context, 2,
                R.drawable.eraser_icon,
                getTextLocale(context, R.string.menu_fastflageditor_deleteselected),
                v -> fragment.deleteSelected());

        fragment.addSmallButton(context, 3,
                R.drawable.deletee_icon,
                getTextLocale(context, R.string.menu_fastflageditor_deleteall),
                v -> fragment.askDeleteAllFlags());

        fragment.addSmallButton(context, 4,
                R.drawable.copy_icon,
                getTextLocale(context, R.string.menu_fastflageditor_copyall),
                v -> fragment.copyAll());

        fragment.addSmallButton(context, 5,
                0,
                getTextLocale(context, R.string.menu_fastflageditor_showpresetflags),
                v -> fragment.DoShowPreset());
        fragment.addSmallButton(context, 6,
                R.drawable.editor_icon,
                getTextLocale(context, R.string.menu_fastflageditor_backups),
                v -> fragment.openBackupsMenu());
    }

    private static String getTextLocale(Context context, int resId) {
        return context.getString(resId);
    }
}