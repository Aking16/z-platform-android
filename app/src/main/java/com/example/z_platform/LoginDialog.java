package com.example.z_platform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class LoginDialog extends DialogFragment {
    public static final String TAG = "editDialog";
    private Toolbar toolbar;
    private Dialog dialog;
    private TextInputLayout edtLayoutEmail, edtLayoutPassword;
    private TextInputEditText edtEmail, edtPassword;
    private Button btnLogin;
    private Context mContext;
    public ApiHandler apiHandler;
    public HashMap data = new HashMap();
    private String userId;

    public static LoginDialog display(FragmentManager fragmentManager) {
        LoginDialog editDialog = new LoginDialog();
        editDialog.show(fragmentManager, TAG);
        return editDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.Base_Theme_Z_Platform_FullScreenDialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Base_Theme_Z_Platform_Slide);
            hideSystemBar();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.login_dialog, container, false);

        mContext = getContext();

        toolbar = view.findViewById(R.id.toolbar);
        btnLogin = view.findViewById(R.id.btnLogin);

        edtEmail = view.findViewById(R.id.edtEmailLogin);
        edtPassword = view.findViewById(R.id.edtPasswordLogin);

        edtLayoutEmail = view.findViewById(R.id.edtLayoutEmailLogin);
        edtLayoutPassword = view.findViewById(R.id.edtLayoutPasswordLogin);

        apiHandler = new ApiHandler(mContext);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Sign in to Z!");

        edtEmail.setOnFocusChangeListener((v, b) -> {
            validator(1);
            btnLogin.setEnabled(true);
        });

        edtPassword.setOnFocusChangeListener((v, b) -> {
            validator(2);
            btnLogin.setEnabled(true);
        });

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (!validator(1) && !validator(2)) {
                btnLogin.setEnabled(false);
            } else {
                btnLogin.setEnabled(true);
                data.put("email", email);
                data.put("password", password);
                apiHandler.signin(data, new VolleyCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Activity activity = (Activity) mContext;
                        Intent intent = new Intent(activity.getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.finish();
                        mContext.startActivity(intent);
                        dismiss();
                    }
                });
            }
        });
    }

    public void hideSystemBar() {
        if (Build.VERSION.SDK_INT < 16) {
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = dialog.getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public boolean validator(int validateID) {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        /* validateID 1 Email validator || validateID 2 = Password validator */

        switch (validateID) {
            case 1:
                if (email.isEmpty()) {
                    edtLayoutEmail.setError("Please write your email!");
                } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                    edtLayoutEmail.setError("Your email is invalid!");
                } else {
                    edtLayoutEmail.setErrorEnabled(false);
                    return true;
                }
                break;
            case 2:
                if (password.isEmpty()) {
                    edtLayoutPassword.setError("Please write your password!");
                } else {
                    edtLayoutPassword.setErrorEnabled(false);
                    return true;
                }
                break;
        }
        return false;
    }
}
