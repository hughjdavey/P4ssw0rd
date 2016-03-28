package com.hugh.p4ssw0rd;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

public class EditorActivity extends Activity implements View.OnClickListener {
    ColorChanger colorChanger;
    EditText passwordIdentifier, passwordUsername, passwordUrl;
    LinearLayout layout;
    TextView finalPassword;

    boolean editPassword;
    String LOGTAG = "Password Editor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_editor);
        ObservableScrollView scrollView = (ObservableScrollView) findViewById(R.id.editor_scrollview);
        //fab.attachToScrollView(scrollView);
        fab.setOnClickListener(this);

        passwordIdentifier = (EditText) findViewById(R.id.password_identifier);
        passwordUsername = (EditText) findViewById(R.id.password_username);
        passwordUrl = (EditText) findViewById(R.id.password_url);

        finalPassword = (TextView) findViewById(R.id.editor_password);
        finalPassword.setOnClickListener(this);

        Bundle passwordBundle = getIntent().getExtras();
        if (passwordBundle != null) {               // this means we are editing an existing password
            passwordIdentifier.setText(passwordBundle.getString(PasswordList.PASSWORD_ID));
            passwordIdentifier.setEnabled(false);
            passwordIdentifier.setFocusable(false);
            passwordIdentifier.setClickable(false);

            finalPassword.setText(passwordBundle.getString(PasswordList.PASSWORD_PASSWORD));
            passwordUsername.setText(passwordBundle.getString(PasswordList.PASSWORD_USERNAME));
            passwordUrl.setText(passwordBundle.getString(PasswordList.PASSWORD_URL));

            editPassword = true;
            setTitle("Edit Password");
        }
        else {                                      // this means we are generating a new password
            editPassword = false;
            setTitle("New Password");
        }

        layout = (LinearLayout) findViewById(R.id.editor_layout);
        TextView id_tv = (TextView) findViewById(R.id.editor_identifier_tv);
        TextView usr_tv = (TextView) findViewById(R.id.editor_username_tv);
        TextView url_tv = (TextView) findViewById(R.id.editor_url_tv);
        TextView pwd_tv = (TextView) findViewById(R.id.editor_password_tv);

        colorChanger = ColorChanger.getInstance(this);
        colorChanger.addViews(layout, passwordIdentifier, passwordUrl, passwordUsername, finalPassword, id_tv, usr_tv, url_tv, pwd_tv, scrollView);
        colorChanger.applyColor();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_editor:
                if (fieldsUnfilled()) {
                    break;
                }

                if (!editPassword) {
                    onPasswordSaved(
                            passwordIdentifier.getText().toString(),
                            passwordUsername.getText().toString(),
                            passwordUrl.getText().toString(),
                            finalPassword.getText().toString()
                    );
                }
                else {
                    Intent passwordBundle = new Intent();
                    passwordBundle.putExtra(PasswordList.PASSWORD_ID, passwordIdentifier.getText().toString());
                    passwordBundle.putExtra(PasswordList.PASSWORD_PASSWORD, finalPassword.getText().toString());
                    passwordBundle.putExtra(PasswordList.PASSWORD_URL, passwordUrl.getText().toString());
                    passwordBundle.putExtra(PasswordList.PASSWORD_USERNAME, passwordUsername.getText().toString());
                    setResult(RESULT_OK, passwordBundle);
                }
                finish();
                break;

            case R.id.editor_password:
                FragmentManager fm = getFragmentManager();
                GeneratorFragment generatorFragment = new GeneratorFragment();
                generatorFragment.show(fm, "generator");
                break;
        }
    }

    /** callback from generator */
    public void onPasswordChosen(String password) {
        finalPassword.setText(password);
    }

    void onPasswordSaved(String identifier, String username, String url, String password) {
        Pstor pstor = Pstor.getPstor(getApplicationContext());
        Password newPassword = new Password(identifier, password, username, url);

        pstor.add(newPassword);
        Toast.makeText(this, "Password saved!", Toast.LENGTH_SHORT).show();
    }

    private boolean fieldsUnfilled() {
        boolean unfilled = passwordIdentifier.getText().toString().isEmpty() |
                           passwordUsername.getText().toString().isEmpty()   |
                           passwordUrl.getText().toString().isEmpty()        |
                           finalPassword.getText().toString().isEmpty();

        if (unfilled) {
            Toast.makeText(this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            return false;
        }
    }
}
