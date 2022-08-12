package com.example.helloworld.util;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.helloworld.application.AppContext;

public class UIUtils {
    public UIUtils() {
    }

    public static int getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static int getRealScreenHeight() {
        int var0 = 0;
        Display var1 = ((WindowManager) AppContext.INSTANCE.getAppContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics var2 = new DisplayMetrics();

        try {
            Class.forName("android.view.Display").getMethod("getRealMetrics", DisplayMetrics.class).invoke(var1, var2);
            var0 = var2.heightPixels;
        } catch (Exception var3) {
            var3.printStackTrace();
        }

        if (var0 <= 0) {
            var0 = getScreenHeight();
        }

        return var0;
    }

    public static int getVirtualBarHeight() {
        return getRealScreenHeight() - getScreenHeight();
    }

    private static DisplayMetrics getDisplayMetrics() {
        return AppContext.INSTANCE.getAppContext().getResources().getDisplayMetrics();
    }

    public static void hideInputMethod(Activity activity) {
        InputMethodManager var1 = (InputMethodManager) AppContext.INSTANCE.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View activity1;
        if ((activity1 = activity.getCurrentFocus()) != null) {
            var1.hideSoftInputFromWindow(activity1.getWindowToken(), 2);
        }

    }

    public static int getColor(int resource) {
        return AppContext.INSTANCE.getAppContext().getResources().getColor(resource);
    }

    public static int sp2pix(float sp) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getDisplayMetrics()));
    }

    public static int getPixels(float dip) {
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getDisplayMetrics()));
    }

    public static int getDimensionPixelSize(int id) {
        return AppContext.INSTANCE.getAppContext().getResources().getDimensionPixelSize(id);
    }

    public static Drawable getDrawable(int res) {
        return AppContext.INSTANCE.getAppContext().getResources().getDrawable(res);
    }

    public static void setTopDrawable(TextView textView, int res) {
        Drawable res1;
        (res1 = AppContext.INSTANCE.getAppContext().getResources().getDrawable(res)).setBounds(0, 0, res1.getMinimumWidth(), res1.getMinimumHeight());
        textView.setCompoundDrawables(null, res1, null, null);
    }

    public static String formatTime(int ms) {
        StringBuilder var1 = new StringBuilder();
        int var2 = (ms /= 1000) / 60;
        ms %= 60;
        if (var2 < 10) {
            var1.append("0");
        }

        var1.append(var2);
        var1.append(":");
        if (ms < 10) {
            var1.append("0");
        }

        var1.append(ms);
        return var1.toString();
    }
}
