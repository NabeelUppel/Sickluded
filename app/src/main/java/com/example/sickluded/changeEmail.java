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

public class changeEmail extends AppCompatActivity implements View.OnClickListener {
    Button submit, back;
    EditText newEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        submit = findViewById(R.id.Submit);
        submit.setOnClickListener(this);
        newEmail = findViewById(R.id.newEmail);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String email = newEmail.getText().toString();
        switch (view.getId()) {
            case R.id.Submit:
                if(!email.isEmpty()) {
                    AlterEmail(email);
                }else{
                    Toast.makeText(this, "Please enter new email address", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.back:
                Intent a = new Intent(changeEmail.this, SettingsActivity.class);
                startActivity(a);
                break;

        }
    }


    public void AlterEmail(String e){
        ContentValues params = new ContentValues();
        String jwt = SharedPreferenceClass.getData(changeEmail.this, "jwt");
        params.put("jwtPost", jwt);
        params.put("function", "editEmail");
        params.put("newEmail", e);
        String URL = "https://lamp.ms.wits.ac.za/home/s2090704/index.php";
        new PhpHandler().makeHttpRequest(changeEmail.this, URL, params, new RequestHandler() {
            @Override
            public void processRequest(String response) throws JSONException {
                System.out.println(response);
                JSONObject j = new JSONObject(response);
                String responseCode = new PhpHandler().getResponseCode(j);
                String message = new PhpHandler().getMessage(j);
                if (responseCode.equals("0")) {
                    Toast.makeText(changeEmail.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    String strJWT = new PhpHandler().getJWT(j);
                    JWT jwt = new JWT(strJWT);
                    Map<String, Claim> allClaims = jwt.getClaims();
                    String email = allClaims.get("email").asString();
                    Toast.makeText(changeEmail.this, message, Toast.LENGTH_SHORT).show();
                    String ID = allClaims.get("userID").asString();
                    SharedPreferenceClass.addData(changeEmail.this, "ID", ID);
                    SharedPreferenceClass.addData(changeEmail.this, "jwt", strJWT);
                    SharedPreferenceClass.addData(changeEmail.this, "email", email);

                }
            }
        });
    }
}
