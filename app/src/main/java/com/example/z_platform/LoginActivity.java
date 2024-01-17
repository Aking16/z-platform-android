package com.example.z_platform;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.RequestQueue;

public class LoginActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    Button btnLogin, btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);

        requestQueue = VolleySingleton.getmInstance(this).getRequestQueue();
        SharedPreferences sh = getSharedPreferences("LoginInfo", MODE_PRIVATE);
        String userId = sh.getString("userId", "");

        if (!userId.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(view -> {
            LoginDialog.display(getSupportFragmentManager());
        });

        btnSignUp.setOnClickListener(view -> {
            SignUpDialog.display(getSupportFragmentManager());
        });

        hideSystemBar();
    }

    public void hideSystemBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}