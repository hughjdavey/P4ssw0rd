package com.hugh.p4ssw0rd;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PasswordList extends Activity {

    private ArrayList<Password> passwordArray = new ArrayList<>();
    private Comparator<Password> sortingMethod = Password.ALPHABETICAL;         // default to alphabetical sorting
    private ListView passwordListview;
    private PasswordsAdapter passwordsAdapter;
    private Pstor pstor;

    public final static String PASSWORD_ID = "id", PASSWORD_PASSWORD = "pwd", PASSWORD_URL = "url", PASSWORD_USERNAME = "usr";
    private final static String LOGTAG = "Password List";
    private final static int EDIT_REQUEST_CODE = 1337;

    private ImageButton floatingAction;
    private ColorChanger colorChanger;
    private RelativeLayout layout;

    private enum ACTION {
        VIEW, DELETE, EDIT;
        static String[] actions = {"View", "Edit", "Delete"};
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);

        pstor = Pstor.getPstor(getApplicationContext());
        generateSampleList();

        loadPasswords();
        passwordsAdapter = new PasswordsAdapter(this, passwordArray);

        passwordListview = (ListView) findViewById(R.id.password_listview);
        passwordListview.setOnItemClickListener(PasswordClickHandler);
        passwordListview.setOnItemLongClickListener(PasswordLongClickHandler);
        passwordListview.setAdapter(passwordsAdapter);

        floatingAction = (ImageButton) findViewById(R.id.floating_new_password);
        floatingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditorActivity.class));
            }
        });

        layout = (RelativeLayout) findViewById(R.id.password_list_activity);

        colorChanger = ColorChanger.getInstance(this);
        colorChanger.addViews(layout);
        colorChanger.applyColor();
    }

    private void onColorChanged(int newColor) {
        RelativeLayout thisActivity = (RelativeLayout) findViewById(R.id.password_list_activity);
        thisActivity.setBackgroundColor(newColor);
    }

    private void viewPassword(Password clickedPassword) {
        Bundle passwordBundle = new Bundle();
        passwordBundle.putString(PASSWORD_ID, clickedPassword.identifier);
        passwordBundle.putString(PASSWORD_PASSWORD, clickedPassword.password);
        passwordBundle.putString(PASSWORD_URL, clickedPassword.url);
        passwordBundle.putString(PASSWORD_USERNAME, clickedPassword.username);

        FragmentManager fm = getFragmentManager();
        ViewingFragment viewingFragment = new ViewingFragment();
        viewingFragment.setArguments(passwordBundle);
        viewingFragment.show(fm, "viewer");
    }

    private void editPassword(Password clickedPassword) {
        Bundle passwordBundle = new Bundle();
        passwordBundle.putString(PASSWORD_ID, clickedPassword.identifier);
        passwordBundle.putString(PASSWORD_PASSWORD, clickedPassword.password);
        passwordBundle.putString(PASSWORD_URL, clickedPassword.url);
        passwordBundle.putString(PASSWORD_USERNAME, clickedPassword.username);

        Intent editPasswordActivity = new Intent(this, EditorActivity.class);
        editPasswordActivity.putExtras(passwordBundle);
        startActivityForResult(editPasswordActivity, EDIT_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Password newPassword = new Password(
                        data.getStringExtra(PASSWORD_ID),
                        data.getStringExtra(PASSWORD_PASSWORD),
                        data.getStringExtra(PASSWORD_USERNAME),
                        data.getStringExtra(PASSWORD_URL)
                );
                pstor.updatePassword(newPassword);

                reloadPasswords();
                Toast.makeText(this, "Password updated!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deletePassword(Password clickedPassword) {
        pstor.removePassword(clickedPassword);

        reloadPasswords();
        Toast.makeText(PasswordList.this, "Password Deleted", Toast.LENGTH_SHORT).show();
    }

    private void loadPasswords() {
        passwordArray.clear();
        passwordArray.addAll(pstor.getAllPasswords());
        Collections.sort(passwordArray, sortingMethod);
    }

    private void reloadPasswords() {
        loadPasswords();
        passwordsAdapter.notifyDataSetChanged();
    }

    private void generateSampleList() {
        Password[] passwords = {
            new Password("Amazon", "gdfgdfgg"),
            new Password("Netflix", "45ggh5gg"),
            new Password("PayPal", "5tvrgy4hbhbtyhtrbh"),
            new Password("Santander", "8797l;97897"),
            new Password("Lloyds", "th65yuh56u56j"),
            new Password("Gmail", "657yh89yhhhh"),
            new Password("Hotmail", "y56y65yhuhhgf"),
            new Password("Twitch", "\\dcfsdfdg"),
            new Password("Ticketmaster", "345f3456w"),
            new Password("Ubuntu Forums", "y45#'[#['@@@"),
            new Password("Reddit", "oi8lo;9;l9"),
            new Password("Ebay", "rgb5y54bh56"),
            new Password("Nespresso Club", "vreg45h45eh"),
            new Password("Nuclear Launch Code", "p4ssw0rd123"),
            new Password("PSN", "fgh45yyh6h"),
        };

        pstor.add(passwords);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_password_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean success;

        switch (item.getItemId()) {
            case R.id.sort_alpha:
                sortingMethod = Password.ALPHABETICAL;
                success = true;
                break;
            case R.id.sort_zeta:
                sortingMethod = Password.ALPHABETICAL_REVERSE;
                success = true;
                break;
            case R.id.sort_new:
                sortingMethod = Password.NEWEST_FIRST;
                success = true;
                break;
            case R.id.sort_old:
                sortingMethod = Password.OLDEST_FIRST;
                success = true;
                break;
            default:
                success = super.onOptionsItemSelected(item);
                break;
        }

        Collections.sort(passwordArray, sortingMethod);
        passwordsAdapter.notifyDataSetChanged();
        return success;
    }

    /** list adapter class */
    private class PasswordsAdapter extends ArrayAdapter<Password> {
        public PasswordsAdapter(Context context, ArrayList<Password> passwords) {
            super(context, 0, passwords);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Password password = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem_password, parent, false);
            }
            // Lookup view for data population
            TextView passwordName = (TextView) convertView.findViewById(R.id.item_name);
            // set an appropriate text color
            colorChanger.addViews(passwordName);
            colorChanger.applyColor();

            // Populate the data into the template view using the data object
            passwordName.setText(password.identifier);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    /** class to handle clicking on a password item */
    private AdapterView.OnItemClickListener PasswordClickHandler = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            final Password clickedPassword = passwordsAdapter.getItem(position);
            viewPassword(clickedPassword);
        }
    };

    /** class to handle long clicking on a password item */
    private AdapterView.OnItemLongClickListener PasswordLongClickHandler = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final Password clickedPassword = passwordsAdapter.getItem(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(PasswordList.this);
            builder.setTitle("Actions")
                    .setItems(ACTION.actions, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    viewPassword(clickedPassword);
                                    break;
                                case 1:
                                    editPassword(clickedPassword);
                                    break;
                                case 2:
                                    deletePassword(clickedPassword);
                                    break;
                            }
                        }
                    });
            builder.create().show();
            return true;
        }
    };
}
