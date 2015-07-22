package com.hugh.p4ssw0rd;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import colorpicker.ColorPickerDialog;
import colorpicker.ColorPickerSwatch;

public class ColorChanger {

    final static int DEFAULT_COLOR = -6736948;
    final static String COLOR_PREFERENCE = "color_pref";
    final static String APP_PREFS = "pw_prefs_yo";

    private Activity activityContext;
    private int currentColor;
    private int[] colorChoices;
    private ArrayList<View> views;

    private static ColorChanger colorChanger;
    private ColorChanger(Context context) {
        this.activityContext = ((Activity) context);
        this.currentColor = loadColorPreference();
        this.colorChoices = colorChoice(activityContext);
        this.views = new ArrayList<>();
    }

    public static ColorChanger getInstance(Context context) {
        if (colorChanger == null) {
            colorChanger = new ColorChanger(context);
        }
        return colorChanger;
    }

    public void applyColor() {
        updateViews();
    }

    public void startColorChange() {
        showColorChooser();
    }

    public void addViews(View... views) {
        Collections.addAll(this.views, views);
    }

    private void showColorChooser() {
        ColorPickerDialog dialog = ColorPickerDialog.newInstance(
                R.string.color_picker_default_title,
                colorChoices,
                currentColor,
                4,                                                          // number of rows
                ColorPickerDialog.SIZE_SMALL);

        dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                currentColor = color;
                applyColorChange();
            }

        });

        dialog.show(activityContext.getFragmentManager(), "colour_dialog");
    }

    private void applyColorChange() {
        updateViews();
        saveColorPreference();
    }

    private void updateViews() {
        for (View view : this.views) {
            if (view instanceof TextView) {
                ((TextView)view).setTextColor(getGoodColor(currentColor));
            }
            else {
                view.setBackgroundColor(currentColor);
            }
        }
    }

    private void saveColorPreference() {
        SharedPreferences prefs = activityContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(COLOR_PREFERENCE, currentColor);
        editor.apply();
    }

    private int loadColorPreference() {
        SharedPreferences prefs = activityContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
        return prefs.getInt(COLOR_PREFERENCE, DEFAULT_COLOR);
    }

    private static int[] colorChoice(Context context){
        int[] mColorChoices=null;
        String[] color_array = context.getResources().getStringArray(R.array.default_color_choice_values);

        if (color_array!=null && color_array.length>0) {
            mColorChoices = new int[color_array.length];
            for (int i = 0; i < color_array.length; i++) {
                mColorChoices[i] = Color.parseColor(color_array[i]);
            }
        }
        return mColorChoices;
    }

    static int getGoodColor(int background) {
        int red = Color.red(background);
        int green = Color.green(background);
        int blue = Color.blue(background);

        if ((red*0.299 + green*0.587 + blue*0.114) > 186) {
            return Color.BLACK;
        }
        else {
            return Color.WHITE;
        }
        //int alpha = Color.alpha(color);
        //return Color.argb(alpha, 255-red, 255-green, 255-blue);
    }
}
