package com.example.sickluded;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class changeUsername extends AppCompatActivity implements View.OnClickListener {
    Button submit, back;
    EditText newUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);
        submit = findViewById(R.id.Submit);
        submit.setOnClickListener(this);
        newUsername = findViewById(R.id.newUsername);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String username = newUsername.getText().toString();

        switch (v.getId()) {
            case R.id.Submit:
                if(!username.isEmpty()) {
                    AlterUsername(username);
                }else{
                    Toast.makeText(this, "Please enter new username", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back:
                Intent a = new Intent(changeUsername.this, SettingsActivity.class);
                startActivity(a);
                break;

        }
    }


    public void AlterUsername(String u) {
        ContentValues params = new ContentValues();
        String jwt = SharedPreferenceClass.getData(changeUsername.this, "jwt");
        params.put("jwtPost", jwt);
        params.put("function", "editUsername");
        params.put("newUsername", u);
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";
        new PhpHandler().makeHttpRequest(changeUsername.this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
                JSONObject j = new JSONObject(response);
                String responseCode = new PhpHandler().getResponseCode(j);
                String message = new PhpHandler().getMessage(j);
                if (responseCode.equals("0")) {
                    Toast.makeText(changeUsername.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    String strJWT = new PhpHandler().getJWT(j);
                    JWT jwt = new JWT(strJWT);
                    Map<String, Claim> allClaims = jwt.getClaims();
                    String username = allClaims.get("username").asString();
                    Toast.makeText(changeUsername.this, message, Toast.LENGTH_SHORT).show();
                    String ID = allClaims.get("userID").asString();
                    SharedPreferenceClass.addData(changeUsername.this, "ID", ID);
                    SharedPreferenceClass.addData(changeUsername.this, "jwt", strJWT);
                    SharedPreferenceClass.addData(changeUsername.this, "username", username);

                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        Intent a = new Intent(changeUsername.this, SettingsActivity.class);
        startActivity(a);

    }
}
