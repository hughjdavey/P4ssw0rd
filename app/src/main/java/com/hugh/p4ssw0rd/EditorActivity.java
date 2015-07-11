package com.hugh.p4ssw0rd;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditorActivity extends Activity implements View.OnClickListener {
    Button cancel, save;
    EditText passwordIdentifier, passwordUsername, passwordUrl;
    TextView finalPassword;

    boolean editPassword;
    String LOGTAG = "Password Editor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        cancel = (Button) findViewById(R.id.editor_cancel);
        cancel.setOnClickListener(this);
        save = (Button) findViewById(R.id.editor_save);
        save.setOnClickListener(this);

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

            save.setText("Update");
            editPassword = true;
            setTitle("Edit Password");
        }
        else {                                      // this means we are generating a new password
            editPassword = false;
            setTitle("New Password");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editor_cancel:
                finish();
                break;

            case R.id.editor_save:
                warnIdentifier();

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

    private void warnIdentifier() {
        if (passwordIdentifier.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a name for this password!", Toast.LENGTH_SHORT).show();
        }
    }
}
