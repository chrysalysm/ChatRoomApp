package com.example.chu.chatroomapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
TruTel Communications - August 2014
Created by Benjamin Chu - Simple G2Sky Chat Application w/ XMPP Connection to eJabberd server
 */

public class Login extends Activity {

    // Declaration
    protected EditText userName;
    private EditText password;
    private static final String TAG = "LoginActivity";

    // Server address of your eJabberd server
    private static String serverAddress = "ejabberd.server.com";
    // Connection port to eJabberd server
    private static int portNumber = 5222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userName = (EditText) findViewById(R.id.editText);
        password = (EditText) findViewById(R.id.editText2);

        // Default user login
        userName.setText("ben");
        password.setText("000000");

        Button loginButton = (Button) findViewById(R.id.button);
        loginButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View x) {
                // Simple Input Validation
                if (userName.getText().toString().equalsIgnoreCase("") || password.getText().toString().equalsIgnoreCase("")) {
                    Toast loginErrorToast = Toast.makeText(getApplicationContext(), "Please enter a user name or password.", Toast.LENGTH_SHORT);
                    loginErrorToast.show();
                } else {
                    // Pass login/connection information to Lobby Activity
                    Intent intent = new Intent(getApplicationContext(), Lobby.class);
                    intent.putExtra("KeyName", userName.getText().toString());
                    intent.putExtra("KeyPass", password.getText().toString());
                    intent.putExtra("KeyAddress", serverAddress);
                    intent.putExtra("KeyPortNum", portNumber);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.i(TAG, "Settings was pressed");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Lifecycle callback methods for logging component lifecycle
    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "Entered the onStart() method.");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Entered the onResume() method.");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Entered the onPause() method.");
    }
    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "Entered the onStop() method.");
    }
    @Override
    public void onRestart() {
        super.onRestart();
        Log.i(TAG, "Entered the onRestart() method.");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Entered the onDestroy() method.");
    }
}
