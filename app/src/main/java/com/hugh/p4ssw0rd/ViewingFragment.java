package com.hugh.p4ssw0rd;

import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewingFragment extends DialogFragment {

    Button copyPassword, showPassword;
    TextView password, username, url;

    private String passwordStr;

    private final static String LOGTAG = "Viewing Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_password, container);

        password = (TextView) view.findViewById(R.id.view_password);
        username = (TextView) view.findViewById(R.id.view_username);
        url = (TextView) view.findViewById(R.id.view_url);

        copyPassword = (Button) view.findViewById(R.id.copy_to_clipboard);
        copyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData passwordText = ClipData.newPlainText("password", password.getText().toString());
                clipboard.setPrimaryClip(passwordText);

                Toast.makeText(getActivity(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        showPassword = (Button) view.findViewById(R.id.show_hide_password);
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (showPassword.getText().toString().toLowerCase().startsWith("show")) {
                    password.setText(passwordStr);
                    showPassword.setText("Hide password");
                }
                else {
                    password.setText("**********");
                    showPassword.setText("Show password");
                }
            }
        });

        Bundle passwordBundle = this.getArguments();
        if (passwordBundle != null) {
            getDialog().setTitle(passwordBundle.getString(PasswordList.PASSWORD_ID));
            passwordStr = passwordBundle.getString(PasswordList.PASSWORD_PASSWORD);
            username.setText(passwordBundle.getString(PasswordList.PASSWORD_USERNAME));
            url.setText(passwordBundle.getString(PasswordList.PASSWORD_URL));
        }

        return view;
    }
}
