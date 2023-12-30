package com.example.z_platform;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.android.volley.RequestQueue;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

public class LoginDialog extends DialogFragment {
    public static final String TAG = "editDialog";
    private RequestQueue requestQueue;
    private Toolbar toolbar;
    private Dialog dialog;
    private TextInputLayout edtLayoutEmail, edtLayoutPassword;
    private TextInputEditText edtEmail, edtPassword;
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

        requestQueue = VolleySingleton.getmInstance(mContext).getRequestQueue();

        toolbar = view.findViewById(R.id.toolbar);
        edtEmail = view.findViewById(R.id.edtEmailLogin);
        edtPassword = view.findViewById(R.id.edtPasswordLogin);

        SharedPreferences sh = mContext.getSharedPreferences("LoginInfo", Context.MODE_PRIVATE);
        userId = sh.getString("userId", "");
        apiHandler = new ApiHandler(mContext);

        data.put("userId", userId);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        toolbar.setNavigationOnClickListener(v -> dismiss());
        toolbar.setTitle("Edit Profile");
        toolbar.inflateMenu(R.menu.dialog_menu);
        toolbar.setOnMenuItemClickListener(item -> {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            data.put("userId", userId);
            data.put("email", email);
            data.put("password", password);

            apiHandler.editUser(data);
            dismiss();
            return true;
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
}
