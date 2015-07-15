package com.hugh.p4ssw0rd;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import colorpicker.ColorPickerDialog;
import colorpicker.ColorPickerSwatch;

public class MainActivity extends Activity {

    private final static int DEFAULT_COLOR = -6736948;
    private final static String COLOR_PREFERENCE = "color_pref";

    private int[] colorChoices;
    private int currentColor;

    Button changeMaster, newPassword, viewPasswords;
    Context thisActivity;
    TextView mainTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;

        if (!Phash.hasPasswordBeenSet(getApplicationContext())) {
            showSetPassword();            // TODO: make resilient to someone deleting hash file on disk
        }
        else {
            showAuthenticator();
        }

        mainTitle = (TextView) findViewById(R.id.main_title);

        newPassword = (Button) findViewById(R.id.new_password);
        newPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditor();
            }
        });

        viewPasswords = (Button) findViewById(R.id.view_passwords);
        viewPasswords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPasswordList();
            }
        });

        changeMaster = (Button) findViewById(R.id.change_app_password);
        changeMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeMaster();
            }
        });

        colorChoices = colorChoice(this);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        currentColor = prefs.getInt(COLOR_PREFERENCE, DEFAULT_COLOR);
        onColorChanged(currentColor);
    }

    private void showAuthenticator() {
        FragmentManager fm = getFragmentManager();
        AuthenticationFragment authenticationFragment = new AuthenticationFragment();
        authenticationFragment.show(fm, "authenticator");
    }

    private void showSetPassword() {
        FragmentManager fm = getFragmentManager();
        SetPasswordFragment setPasswordFragment = new SetPasswordFragment();
        setPasswordFragment.show(fm, "set password");
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
                onColorChanged(currentColor);
            }

        });

        dialog.show(getFragmentManager(), "colour_dialog");
    }

    private void onColorChanged(int color) {
        LinearLayout main = (LinearLayout) findViewById(R.id.main_layout);
        main.setBackgroundColor(color);
        setTextColors(getGoodColor(color));
        // TODO make colour change apply to other activities
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(COLOR_PREFERENCE, color);
        editor.apply();
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

    private void setTextColors(int color) {
        changeMaster.setTextColor(color);
        newPassword.setTextColor(color);
        viewPasswords.setTextColor(color);
        mainTitle.setTextColor(color);
    }

    private int getGoodColor(int background) {
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

    private void showEditor() {
        startActivity( new Intent(this, EditorActivity.class) );
    }

    private void showPasswordList() {
        Intent passList = new Intent(thisActivity, PasswordList.class);
        startActivity(passList);
    }

    boolean changingMaster = false;
    private void showChangeMaster() {
        changingMaster = true;
        showAuthenticator();
    }

    public void onAuthReturn(boolean success) {
        if (success) {
            if (changingMaster) {
                showSetPassword();
                changingMaster = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean success;
        switch(item.getItemId()) {
            case R.id.change_theme:
                showColorChooser();
                success = true;
                break;
            default:
                success = super.onOptionsItemSelected(item);
                break;
        }
        return success;
    }
}
