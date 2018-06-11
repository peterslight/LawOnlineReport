package com.peterstev.lawonlinereportnigeria.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.peterstev.lawonlinereportnigeria.R;
import com.peterstev.lawonlinereportnigeria.interfaces.ApiInterface;
import com.peterstev.lawonlinereportnigeria.models.login.LoginModel;
import com.peterstev.lawonlinereportnigeria.services.chrome.CustomTabsActivityHelper;
import com.peterstev.lawonlinereportnigeria.utils.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Peterstev on 25/05/2018.
 * for LawOnlineReport
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText etUsername, etPassword;
    private Button btLogin, btRegister;
    private CustomTabsActivityHelper tabsActivityHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tabsActivityHelper = new CustomTabsActivityHelper();

        etUsername = findViewById(R.id.login_username);
        etPassword = findViewById(R.id.login_password);
        btLogin = findViewById(R.id.btn_login);
        btRegister = findViewById(R.id.btn_register);

        btRegister.setOnClickListener(this);
        btLogin.setOnClickListener(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        tabsActivityHelper.bindCustomTabsService(this);
        setUpCustomTab();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tabsActivityHelper.unBindCustomTabsService(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btRegister) {
            launchCustomTabs(Utils.REGISTER_URL);
        } else if (view == btLogin) {
            String username = etUsername.getText().toString().trim().toLowerCase();
            String password = etPassword.getText().toString().trim().toLowerCase();

            if (TextUtils.isEmpty(username)) {
                etUsername.setError("email is empty");
            } else if (TextUtils.isEmpty(password)) {
                etPassword.setError("password is empty");
            } else if (!username.contains(".com") || !username.contains("@")) {
                etUsername.setError("invalid email");
            } else {
                attemptLogin(username, password);
            }
        }
    }

    private void attemptLogin(String username, String password) {
        Toast.makeText(this, "attempting", Toast.LENGTH_SHORT).show();
        ApiInterface apiInterface = Utils.getRetrofitGson(this, Utils.BASE_URL);
        Call<LoginModel> call = apiInterface.loginUser(username, password);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(Call<LoginModel> call, Response<LoginModel> response) {
                Toast.makeText(LoginActivity.this, "OnResponse", Toast.LENGTH_SHORT).show();
                if (response.isSuccessful()) {
                    LoginModel model = response.body();
                    if (model != null) {
                        Toast.makeText(LoginActivity.this, "model is not null", Toast.LENGTH_SHORT).show();

                        Toast.makeText(LoginActivity.this, model.getData().getUserLogin(), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid login ", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginModel> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "OnFailure : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
