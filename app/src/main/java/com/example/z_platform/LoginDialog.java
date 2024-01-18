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
import android.view.Window;
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
    Activity activity;
    public ApiHandler apiHandler;
    public HashMap data = new HashMap();

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
            changeSystemColor();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.login_dialog, container, false);

        mContext = getContext();
        activity = (Activity) mContext;

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

    public void changeSystemColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getColor(R.color.dark_primary));
            window.setNavigationBarColor(activity.getColor(R.color.dark_primary));
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
