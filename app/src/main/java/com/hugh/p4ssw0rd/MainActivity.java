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

    Button changeMaster, newPassword, viewPasswords;
    Context thisActivity;
    LinearLayout layout;
    TextView mainTitle;

    private ColorChanger colorChanger;

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
        layout = (LinearLayout) findViewById(R.id.main_layout);

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

        colorChanger = ColorChanger.getInstance(this);
        colorChanger.addViews(mainTitle, layout, newPassword, viewPasswords, changeMaster);
        colorChanger.applyColor();
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
                colorChanger.startColorChange();
                success = true;
                break;
            default:
                success = super.onOptionsItemSelected(item);
                break;
        }
        return success;
    }
}
