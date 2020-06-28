package com.example.sickluded;

import android.app.Activity;
import android.content.ContentValues;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PhpHandler {

    static String responseData = "";

    public void makeHttpRequest(final Activity a, String url,
                                ContentValues params, final RequestHandler rh) {

        // Making HTTP request
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();

        for (String key : params.keySet()) {
            builder.add(key, params.getAsString(key));
        }

        final Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                responseData = Objects.requireNonNull(response.body()).string();

                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            rh.processRequest(responseData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }

    public void StatsRequest(final Activity a, String url, final RequestHandler rh) {
        // Making HTTP request
        OkHttpClient client = new OkHttpClient();


        final Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                responseData = Objects.requireNonNull(response.body()).string();
                a.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            rh.processRequest(responseData);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void ContactHttp(String url,
                            ContentValues params) {

        // Making HTTP request
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();

        for (String key : params.keySet()) {
            builder.add(key, params.getAsString(key));
        }

        final Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                responseData = Objects.requireNonNull(response.body()).string();
                System.out.println(responseData);
            }
        });

    }

    //fix this, too many methods
    public String getMessage(JSONObject j) throws JSONException {
        String m = j.getString("message");
        return m;
    }

    public String getResponseCode(JSONObject j) throws JSONException {
        String r = j.getString("response");
        return r;
    }

    public String getUsername(JSONObject j) throws JSONException {
        String u = j.getString("username");
        return u;
    }

    public String getAuth(JSONObject j) throws JSONException {
        String a = j.getString("auth");
        return a;
    }

    public String getSessionID(JSONObject j) throws JSONException {
        String a = j.getString("session_id");
        return a;
    }

    public String getJWT(JSONObject j) throws JSONException {
        String a = j.getString("jwt");
        return a;
    }
}