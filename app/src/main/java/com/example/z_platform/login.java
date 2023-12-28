package com.example.z_platform;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    RequestQueue requestQueue;
    EditText edtEmail, edtPassword;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnSubmit = findViewById(R.id.btnSubmit);

        HashMap data = new HashMap();
        data.put("email", edtEmail.getText().toString());
        data.put("password", edtPassword.getText().toString());
        String url = "http://192.168.1.103:3000/api/signin";

        btnSubmit.setOnClickListener(view -> {
            postData(url, data);
        });
    }

    public void postData(String url, HashMap data) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String userId = response.getString("id");

                            SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();

                            myEdit.putString("userId", userId);
                            myEdit.apply();

                            Toast.makeText(login.this, userId, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(login.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(login.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}