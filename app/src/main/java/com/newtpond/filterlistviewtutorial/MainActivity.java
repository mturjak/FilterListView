package com.newtpond.filterlistviewtutorial;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.parse.ParseObject.pinAllInBackground;

public class MainActivity extends ActionBarActivity {

    // Declare Variables
    private ParseUser mCurrentUser;

    ListView list;
    GravatarAdapter adapter;
    EditText editsearch;
    boolean mExtendedOnline = false;
    ArrayList<String> onlineSearches = new ArrayList<String>();
    ArrayList<ParseObject> mArraylist = new ArrayList<ParseObject>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_main);

        list = (ListView) findViewById(R.id.listview);
        adapter = new GravatarAdapter(MainActivity.this, "item");

        mCurrentUser = ParseUser.getCurrentUser();

        if(mCurrentUser == null) {
            // Parse login and query user profiles (for now we will jus log in a test user, assuming the user exist)
            String username = "testuser";
            String password = "test123";

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                public void done(ParseUser user, ParseException e) {

                    if (user != null) {
                        Log.d("parse_user", "User logged in successfully");
                        Toast.makeText(MainActivity.this, "Successfully connected!", Toast.LENGTH_LONG).show();

                        // get data from server into the adapter
                        listUsers(false, null);
                    } else {
                        Toast.makeText(MainActivity.this, "Couldn't get user data!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            // get local data into adapter
            listUsers(true, null);
        }

        list.setAdapter(adapter);

        editsearch = (EditText) findViewById(R.id.search);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                text = text.trim();
                adapter.filter(text);

                if(adapter.getCount() < 1) {
                    if(!onlineSearches.contains(text)) {
                        // search online if no match found locally
                        // mExtendedOnline checks if we already tried searching online
                        if (!mExtendedOnline && !text.isEmpty()) {

                            // adds new matched profiles to the previous list
                            listUsers(false, text);
                            onlineSearches.add(text);
                        }
                    }
                    mExtendedOnline = true;
                } else {
                    mExtendedOnline = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
        });
    }

    // Not using options menu in this tutorial
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void listUsers(final boolean fromLocalData, final String contains) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Profile");
        query.addAscendingOrder("displayName");
        query.setLimit(1000);

        if(fromLocalData)
            query.fromLocalDatastore();

        if(contains != null)
            query.whereContains("displayName", contains);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> usersList, ParseException e) {
                if (e == null) {
                    Log.d("score", "Retrieved " + usersList.size() + " scores; local=" + String.valueOf(fromLocalData) + ";");

                    if(contains != null) {
                        for (ParseObject item : usersList) {
                            if (!mArraylist.contains(item))
                                mArraylist.add(item);
                        }
                    } else {
                        mArraylist.addAll(usersList);
                    }

                    if(!fromLocalData)
                        pinAllInBackground("profiles", mArraylist);

                    adapter.updateUsers(mArraylist);

                    // Locate the EditText in listview_main.xml
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
    }
}
