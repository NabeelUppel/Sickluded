package com.example.sickluded;

import org.json.JSONException;

public interface RequestHandler{
   void processRequest(String response) throws JSONException;
}