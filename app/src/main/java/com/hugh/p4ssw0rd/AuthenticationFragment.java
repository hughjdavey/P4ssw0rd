package com.hugh.p4ssw0rd;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AuthenticationFragment extends DialogFragment implements View.OnClickListener {

    Button cancel, submit;
    EditText enteredPassword;
    TextView passwordFailure;

    private static int failedAttempts;
    final String LOGTAG = "Authentication Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authentication, container);

        enteredPassword = (EditText) view.findViewById(R.id.authentication_box);
        passwordFailure = (TextView) view.findViewById(R.id.authentication_failure);

        cancel = (Button) view.findViewById(R.id.authentication_cancel);
        cancel.setOnClickListener(this);
        submit = (Button) view.findViewById(R.id.authentication_submit);
        submit.setOnClickListener(this);

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
                if (submittedPassword.equals("")) {
                    getActivity().finish();
                }
                else {
                    success = Phash.checkPassword(getActivity(), submittedPassword);
                    String message = success ? "Access Granted" : "Access Denied";
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
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

    void returnAuthResult(boolean success) {
        ((MainActivity)getActivity()).onAuthReturn(success);
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

    private long getBackoffMillis(int failedAttempts) {
        return (long) Math.pow(2, failedAttempts - 1) * 1000;
    }
}
