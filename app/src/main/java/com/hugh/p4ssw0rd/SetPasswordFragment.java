package com.hugh.p4ssw0rd;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetPasswordFragment extends DialogFragment implements View.OnClickListener {

    Button cancel, submit;
    EditText password1, password2;
    TextView passwordError;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_password, container);

        passwordError = (TextView) view.findViewById(R.id.set_password_error);

        password1 = (EditText) view.findViewById(R.id.set_password_password1);
        password2 = (EditText) view.findViewById(R.id.set_password_password2);
        password2.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String strPass1 = password1.getText().toString();
                String strPass2 = password2.getText().toString();
                if (strPass1.equals(strPass2)) {
                    passwordError.setText("Passwords match!");
                    enableSubmit();
                } else {
                    passwordError.setText("Passwords do not match!");
                    disableSubmit();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        cancel = (Button) view.findViewById(R.id.set_password_cancel);
        cancel.setOnClickListener(this);
        submit = (Button) view.findViewById(R.id.set_password_submit);
        submit.setOnClickListener(this);
        disableSubmit();

        getDialog().setTitle("Choose a password");
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_password_cancel:
                dismiss();
                getActivity().finish();
                break;

            case R.id.set_password_submit:
                String chosenPassword = password2.getText().toString();
                boolean success = Phash.savePassword(getActivity(), chosenPassword);

                String result = success ? "Success!" : "Error";
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                dismiss();
                break;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                // overriding onBackPressed to prevent user pressing back and skipping password entry by dismissing the dialog
            }
        };
    }

    private void disableSubmit() {
        submit.setAlpha(0.5f);
        submit.setClickable(false);
    }

    private void enableSubmit() {
        submit.setAlpha(1.0f);
        submit.setClickable(true);
    }
}
