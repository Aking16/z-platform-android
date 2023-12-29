package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Test extends AppCompatActivity {
    RequestQueue requestQueue;
    TextView txtUserId;
    EditText edtPost;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();

        btnSubmit = findViewById(R.id.btnSubmitPost);
        txtUserId = findViewById(R.id.txtUserId);
        edtPost = findViewById(R.id.edtPost);

        SharedPreferences sh = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String userId = sh.getString("userId", "");

        txtUserId.setText(userId);

        HashMap data = new HashMap();

        btnSubmit.setOnClickListener(view -> {
            data.put("body", edtPost.getText().toString());
            data.put("userId", userId);

            String url = "http://192.168.1.103:3000/api/posts";
            postData(url, data);
        });
    }

    public void postData(String url, HashMap data) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Test.this, response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Test.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
    }
}