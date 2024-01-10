package com.example.z_platform;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class SignUpDialog extends DialogFragment {
    public static final String TAG = "editDialog";
    private Toolbar toolbar;
    private Dialog dialog;
    private TextInputLayout edtLayoutName, edtLayoutUsername, edtLayoutEmail, edtLayoutPassword, edtLayoutPasswordConf;
    private TextInputEditText edtName, edtUsername, edtEmail, edtPassword, edtPasswordConf;
    private Button btnLogin;
    private Context mContext;
    public ApiHandler apiHandler;
    public HashMap data = new HashMap();
    private String userId;

    public static SignUpDialog display(FragmentManager fragmentManager) {
        SignUpDialog editDialog = new SignUpDialog();
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
        View view = inflater.inflate(R.layout.signup_dialog, container, false);

        mContext = getContext();

        toolbar = view.findViewById(R.id.toolbar);
        btnLogin = view.findViewById(R.id.btnLogin);

        edtName = view.findViewById(R.id.edtNameSignUp);
        edtUsername = view.findViewById(R.id.edtUsernameSignUp);
        edtEmail = view.findViewById(R.id.edtEmailSignUp);
        edtPassword = view.findViewById(R.id.edtPasswordSignUp);
        edtPasswordConf = view.findViewById(R.id.edtPasswordConfSignUp);

        edtLayoutName = view.findViewById(R.id.edtLayoutNameSignUp);
        edtLayoutUsername = view.findViewById(R.id.edtLayoutUsernameSignUp);
        edtLayoutEmail = view.findViewById(R.id.edtLayoutEmailSignUp);
        edtLayoutPassword = view.findViewById(R.id.edtLayoutPasswordSignUp);
        edtLayoutPasswordConf = view.findViewById(R.id.edtLayoutPasswordConfSignUp);

        apiHandler = new ApiHandler(mContext);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Sign up to Z!");

        edtName.setOnFocusChangeListener((v, b) -> {
            validator(1);
            btnLogin.setEnabled(false);
        });

        edtUsername.setOnFocusChangeListener((v, b) -> {
            validator(2);
            btnLogin.setEnabled(false);
        });

        edtEmail.setOnFocusChangeListener((v, b) -> {
            validator(3);
            btnLogin.setEnabled(false);
        });

        edtPassword.setOnFocusChangeListener((v, b) -> {
            validator(4);
            btnLogin.setEnabled(false);
        });

        edtPasswordConf.setOnFocusChangeListener((v, b) -> {
            validator(5);
            btnLogin.setEnabled(false);
        });

        btnLogin.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String username = edtUsername.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (!validator(1) && !validator(2)
                    && !validator(3) && !validator(4) && !validator(5)) {
                btnLogin.setEnabled(false);
            } else {
                data.put("name", name);
                data.put("username", username);
                data.put("email", email);
                data.put("password", password);
                apiHandler.register(data, new VolleyCallback<String>() {
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
        String name = edtName.getText().toString();
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String passwordConf = edtPasswordConf.getText().toString().trim();

        /* validateID 1 = Name validator || validateID 2 = Username validator || validateID 3 = Email validator
        validateID 4 = Password validator || validateID 5 = Password Confirmation validator */

        switch (validateID) {
            case 1:
                if (name.isEmpty()) {
                    edtLayoutName.setError("Please write your name!");
                } else {
                    edtLayoutName.setErrorEnabled(false);
                    return true;
                }
                break;
            case 2:
                if (username.isEmpty()) {
                    edtLayoutUsername.setError("Please write your username!");
                } else {
                    edtLayoutUsername.setErrorEnabled(false);
                    return true;
                }
                break;
            case 3:
                if (email.isEmpty()) {
                    edtLayoutEmail.setError("Please write your email!");
                } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+")) {
                    edtLayoutEmail.setError("Your email is invalid!");
                } else {
                    edtLayoutEmail.setErrorEnabled(false);
                    return true;
                }
                break;
            case 4:
                if (password.isEmpty()) {
                    edtLayoutPassword.setError("Please write your password!");
                } else {
                    edtLayoutPassword.setErrorEnabled(false);
                    return true;
                }
                break;
            case 5:
                if (passwordConf.isEmpty()) {
                    edtLayoutPasswordConf.setError("Please write your password confirmation!");
                } else if (!passwordConf.equals(password)) {
                    edtLayoutPasswordConf.setError("Password confirmation doesn't match the password");
                } else {
                    edtLayoutPasswordConf.setErrorEnabled(false);
                    return true;
                }
                break;
        }
        return false;
    }
}
