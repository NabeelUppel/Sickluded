package com.example.sickluded;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class forgotPassword extends AppCompatActivity implements View.OnClickListener {
    Button btnSubmit;
    EditText txtEmail, txtPassword, txtConfirm;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        txtConfirm = findViewById(R.id.ConfirmPassword);
        txtEmail = findViewById(R.id.editEmail);
        txtPassword = findViewById(R.id.editPassword);
        btnSubmit = findViewById(R.id.Submit);
        btnSubmit.setOnClickListener(this);
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
        Intent intent = getIntent();
        Uri Data = intent.getData();
        token = Data.getQueryParameter("token"); //set query and assigns it to query.

    }

    @Override
    public void onClick(View v) {
        String confirmP = txtConfirm.getText().toString();
        String password = txtPassword.getText().toString();

        if (confirmP.equals(password) && !password.isEmpty()) {
            changePassword(password, token);
        } else {
            Toast.makeText(this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
        }
    }

    private void changePassword(String Password, String Token) {

        ContentValues params = new ContentValues();
        params.put("newPassword", Password);
        System.out.println(Token);
        params.put("function", "editPassword");
        params.put("Token", Token);
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";

        new PhpHandler().makeHttpRequest(forgotPassword.this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
                System.out.println(response);
                JSONObject j = new JSONObject(response);
                String responseCode = new PhpHandler().getResponseCode(j);
                String message = new PhpHandler().getMessage(j);
                if (responseCode.equals("0")) {
                    Toast.makeText(forgotPassword.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(forgotPassword.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }


}
