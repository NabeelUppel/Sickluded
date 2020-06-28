package com.example.sickluded;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


public class SignUpActivity extends MainActivity implements View.OnClickListener {
    EditText editUsername, editPassword, editEmail, editConfirm;
    Button btnRegister;
    TextView btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editEmail = findViewById(R.id.editEmail);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editConfirm = findViewById(R.id.confirm);
        btnLogin = findViewById(R.id.Login);
        btnRegister = findViewById(R.id.Register);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String confirmP = editConfirm.getText().toString();
        String username = editUsername.getText().toString();
        String password = editPassword.getText().toString();
        String email = editEmail.getText().toString();


        switch (v.getId()) {
            case R.id.Register:
                if (confirmP.equals(password) && !username.isEmpty() && !password.isEmpty() && !email.isEmpty()) {
                    Register(username, password, email, "Register");
                } else {
                    Toast.makeText(this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Login:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                break;

        }
    }


    public void Register(String u, String p, String e, String f) {
        ContentValues params = new ContentValues();
        params.put("username", u);
        params.put("password", p);
        params.put("email", e);
        params.put("function", f);
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";

        new PhpHandler().makeHttpRequest(this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {

                JSONObject j = new JSONObject(response);
                String responseCode = new PhpHandler().getResponseCode(j);
                String message = new PhpHandler().getMessage(j);
                if (responseCode.equals("0")) {
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    String strJWT = new PhpHandler().getJWT(j);
                    JWT jwt = new JWT(strJWT);
                    Map<String, Claim> allClaims = jwt.getClaims();
                    String username = allClaims.get("username").asString();
                    String email = allClaims.get("email").asString();
                    String ID = allClaims.get("userID").asString();
                    Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                    SharedPreferenceClass.addData(SignUpActivity.this, "ID", ID);
                    SharedPreferenceClass.addData(SignUpActivity.this, "jwt", strJWT);
                    SharedPreferenceClass.addData(SignUpActivity.this, "email", email);
                    SharedPreferenceClass.addData(SignUpActivity.this, "username", username);
                    Intent k = new Intent(SignUpActivity.this, HomeActivity.class);
                    startActivity(k);
                }
            }
        });

    }

}