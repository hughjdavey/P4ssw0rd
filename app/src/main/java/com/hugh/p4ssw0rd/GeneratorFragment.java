package com.hugh.p4ssw0rd;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

public class GeneratorFragment extends DialogFragment implements View.OnClickListener, NumberPicker.OnValueChangeListener {
    Button generate, done;
    CheckBox letters, numbers, symbols;
    EditText length;
    TextView password;

    private int previousValue = 1;
    private static final String LOGTAG = "Generator Fragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_generator, container);

        generate = (Button) view.findViewById(R.id.generator_generate);
        generate.setOnClickListener(this);
        done = (Button) view.findViewById(R.id.generator_done);
        done.setOnClickListener(this);

        letters = (CheckBox) view.findViewById(R.id.letter_checkbox);
        numbers = (CheckBox) view.findViewById(R.id.number_checkbox);
        symbols = (CheckBox) view.findViewById(R.id.symbol_checkbox);

        length = (EditText) view.findViewById(R.id.generator_length);
        length.setOnClickListener(this);
        password = (TextView) view.findViewById(R.id.generator_password);

        getDialog().setTitle("Generate Password");
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.generator_generate:
                if (unfilledFields()) {
                    break;
                }

                int chosenLength = Integer.parseInt(
                        length.getText().toString()
                );

                Set<Pgen.Chartype> types = new HashSet<>();
                if (letters.isChecked()) {
                    types.add(Pgen.Chartype.LETTERS);
                }
                if (numbers.isChecked()) {
                    types.add(Pgen.Chartype.NUMBERS);
                }
                if (symbols.isChecked()) {
                    types.add(Pgen.Chartype.SYMBOLS);
                }

                String generatedPassword = Pgen.generatePassword(chosenLength, types);
                password.setText(generatedPassword);
                break;

            case R.id.generator_done:
                String chosenPassword = password.getText().toString();
                ((EditorActivity) getActivity()).onPasswordChosen(chosenPassword);
                dismiss();
                break;

            case R.id.generator_length:
                Log.d(LOGTAG, "in onClick...");
                showNumberPicker();
                break;
        }
    }

    private boolean unfilledFields() {
        boolean unfilled = false;
        if (length.getText().toString().equals("")) {
            unfilled = true;
            Toast.makeText(getActivity(), "Please choose a password length!", Toast.LENGTH_SHORT).show();
        }
        else if (!letters.isChecked() && !numbers.isChecked() && !symbols.isChecked()) {
            unfilled = true;
            Toast.makeText(getActivity(), "Please choose at least one character set!", Toast.LENGTH_SHORT).show();
        }
        return unfilled;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    }

    public void showNumberPicker() {
        final Dialog dialog = new Dialog(this.getActivity());
        dialog.setTitle("Password length");
        dialog.setContentView(R.layout.numberpicker_dialog);
        Button cancel = (Button) dialog.findViewById(R.id.cancel_number);
        Button confirm = (Button) dialog.findViewById(R.id.confirm_number);

        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.number_picker);
        np.setMaxValue(32);
        np.setMinValue(1);
        np.setValue(previousValue);
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = np.getValue();
                length.setText(String.valueOf(value));
                previousValue = value;
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
