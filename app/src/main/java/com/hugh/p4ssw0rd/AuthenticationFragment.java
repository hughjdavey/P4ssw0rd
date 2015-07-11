package com.hugh.p4ssw0rd;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationFragment extends DialogFragment implements View.OnClickListener {

    private static int failedAttempts;
    private final String LOGTAG = "Authentication Fragment";

    private Button cancel, submit;
    private EditText enteredPassword;
    private TextView passwordFailure;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container);

        cancel = (Button) view.findViewById(R.id.authentication_cancel);
        cancel.setOnClickListener(this);
        submit = (Button) view.findViewById(R.id.authentication_submit);
        submit.setOnClickListener(this);

        enteredPassword = (EditText) view.findViewById(R.id.authentication_box);
        enteredPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                boolean textboxEmpty = enteredPassword.getText().toString().isEmpty();
                submit.setEnabled(!textboxEmpty);                                                   // disable submit button if textbox is empty to avoid crashing
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        passwordFailure = (TextView) view.findViewById(R.id.authentication_failure);

        getDialog().setTitle("Enter password");
        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);
        return view;
    }

    @Override
    public void onClick(View view) {
        boolean success = false;

        switch (view.getId()) {
            case R.id.authentication_submit:
                String submittedPassword = enteredPassword.getText().toString();
                success = Phash.checkPassword(getActivity(), submittedPassword);
                String message = success ? "Access Granted" : "Access Denied";
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                break;
            case R.id.authentication_cancel:
                dismiss();
                getActivity().finish();
                break;
        }

        if (success) {
            returnAuthResult(true);
            failedAttempts = 0;
            dismiss();
        }
        else {
            if (passwordFailure.getVisibility() == View.GONE && failedAttempts > 0) {
                passwordFailure.setVisibility(View.VISIBLE);
            }

            // we make user wait a number of seconds before trying again to avoid brute force attacks
            // the amount of time they have to wait increases exponentially on each failed attempt
            long backoff = getBackoffMillis(++failedAttempts);

            if (backoff >= 1000) {
                submit.setEnabled(false);
                new CountDownTimer(backoff, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        passwordFailure.setText("Please wait " + (millisUntilFinished / 1000) + " seconds");
                    }
                    @Override
                    public void onFinish() {
                        passwordFailure.setText("");
                        submit.setEnabled(true);
                    }
                }.start();
            }
        }
    }

    private void returnAuthResult(boolean success) {
        ((MainActivity) getActivity()).onAuthReturn(success);
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

    /* function to return exponential backoff value in milliseconds */
    private long getBackoffMillis(int failedAttempts) {
        return (long) Math.pow(2, failedAttempts - 1) * 1000;
    }
}
