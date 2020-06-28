package com.example.sickluded;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends MainActivity implements View.OnClickListener {
    Button btnUsername, btnEmail, btnDelete;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout contentFrameLayout = findViewById(R.id.content_frame); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_settings, contentFrameLayout);
        btnUsername = findViewById(R.id.changeUsername);
        btnEmail = findViewById(R.id.changeEmail);
        btnDelete = findViewById(R.id.deleteAccount);
        btnEmail.setOnClickListener(this);
        btnUsername.setOnClickListener(this);
        btnDelete.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeEmail:
                Intent j = new Intent(this, changeEmail.class);
                startActivity(j);
                break;

            case R.id.changeUsername:
                Intent i = new Intent(this, changeUsername.class);
                startActivity(i);
                break;

            case R.id.deleteAccount:
                DeleteAccountDialog();
                break;
        }
    }


    public void DeleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");

// Set up the input
        final EditText input = new EditText(this);

        input.setHint("Please enter your password");
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);


// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                password = input.getText().toString();
                if (!password.isEmpty()) {
                    ContentValues params = new ContentValues();
                    String jwt = SharedPreferenceClass.getData(SettingsActivity.this, "jwt");
                    params.put("password", password);
                    params.put("function", "deleteAccount");
                    params.put("jwtPost", jwt);
                    String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";
                    new PhpHandler().makeHttpRequest(SettingsActivity.this, URL, params, new RequestHandler() {
                        @Override
                        public void processRequest(String response) throws JSONException {


                            JSONObject j = new JSONObject(response);
                            String message = new PhpHandler().getMessage(j);
                            String responseCode = new PhpHandler().getResponseCode(j);
                            if (responseCode.equals("0")) {
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                            } else {
                                dialog.dismiss();
                                Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_SHORT).show();
                                Logout();
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


}
