package com.example.sickluded;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editEmail, editPassword;
    Button btnLogin;
    TextView btnRegister, btnForgotPassword;
    private String m_Text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.Login);
        btnRegister = findViewById(R.id.Register);
        btnForgotPassword = findViewById(R.id.forgotPassword);
        btnForgotPassword.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Login:
                String email = editEmail.getText().toString();
                String password = editPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {

                    try {
                        login(email, password, "Login");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter Login Details", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.Register:
                Intent i = new Intent(this, SignUpActivity.class);
                startActivity(i);
                break;

            case R.id.forgotPassword:
                EmailDialog();
                break;
        }


    }


    public void login(String e, String p, String f) throws IOException {
        ContentValues params = new ContentValues();

        params.put("email", e);
        params.put("password", p);
        params.put("function", f);
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";
        new PhpHandler().makeHttpRequest(LoginActivity.this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
                JSONObject j = new JSONObject(response);
                String message = new PhpHandler().getMessage(j);

                String responseCode = new PhpHandler().getResponseCode(j);
                if (responseCode.equals("0")) {

                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    String strJWT = new PhpHandler().getJWT(j);
                    JWT jwt = new JWT(strJWT);
                    Map<String, Claim> allClaims = jwt.getClaims();
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    String username = allClaims.get("username").asString();
                    String email = allClaims.get("email").asString();
                    String ID = allClaims.get("userID").asString();
                    SharedPreferenceClass.addData(LoginActivity.this, "ID", ID);
                    SharedPreferenceClass.addData(LoginActivity.this, "jwt", strJWT);
                    SharedPreferenceClass.addData(LoginActivity.this, "email", email);
                    SharedPreferenceClass.addData(LoginActivity.this, "username", username);

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    addDeviceToken(ID);
                    startActivity(intent);

                }
            }
        });


    }

    public void EmailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");

// Set up the input
        final EditText input = new EditText(this);
        input.setHint("Enter your email address");
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);


// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                ContentValues params = new ContentValues();
                params.put("email", m_Text);
                String URL = "https://lamp.ms.wits.ac.za/home/s2090704/resetPassword.php";
                new PhpHandler().makeHttpRequest(LoginActivity.this, URL, params, new RequestHandler() {
                    @Override
                    public void processRequest(String response) throws JSONException {
                        dialog.dismiss();

                        Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                    }
                });
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

    public void addDeviceToken(final String id) {

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();
                ContentValues params = new ContentValues();
                params.put("user_id", id);
                params.put("DeviceToken", token);
                params.put("function", "addDeviceToken");
                String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";

                new PhpHandler().ContactHttp(URL, params);
            }
        });

    }
}
