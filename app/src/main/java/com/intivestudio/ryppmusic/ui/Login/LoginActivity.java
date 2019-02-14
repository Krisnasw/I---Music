package com.intivestudio.ryppmusic.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.local.UserDAO;
import com.intivestudio.ryppmusic.data.remote.ApiLogin;
import com.intivestudio.ryppmusic.data.remote.User;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.ui.Register.RegisterActivity;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.intivestudio.ryppmusic.utils.AppDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    /**
     * LOGIN
     */
    private TextView mBtnLogin;
    private CardView mCardView;
    private LinearLayout mBtnRegist;
    @NotEmpty
    @Email
    private EditText etEmail;
    @NotEmpty
    private EditText etPassword;
    private SweetAlertDialog swal;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        if (Prefs.getBoolean(AppConfig.IS_LOGIN, false)) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        initView();

        swal = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        swal.setTitleText("Loading");
        swal.setContentText("Please wait...");
        swal.setCancelable(false);

        validator = new Validator(this);
        validator.setValidationListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mBtnLogin = (TextView) findViewById(R.id.btnLogin);
        mCardView = (CardView) findViewById(R.id.card_view);
        mBtnRegist = (LinearLayout) findViewById(R.id.btnRegist);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        mBtnLogin.setOnClickListener(this);
        mBtnRegist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btnLogin:
                validator.validate();
                break;
            case R.id.btnRegist:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        swal.show();
        ApiClient.get(this).doLogin(etEmail.getText().toString(), etPassword.getText().toString())
                .enqueue(new Callback<ApiLogin>() {
                    @Override
                    public void onResponse(Call<ApiLogin> call, Response<ApiLogin> response) {
                        try {
                            ApiLogin resp = response.body();
                            if (resp.getStatus()) {
                                getUserData(resp.getToken());
                            } else {
                                swal.dismissWithAnimation();
                                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error")
                                        .setContentText(resp.getMessage())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                            }
                                        })
                                        .show();
                            }
                        } catch (Exception e) {
                            swal.dismissWithAnimation();
                            e.printStackTrace();
                            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiLogin> call, Throwable t) {
                        swal.dismissWithAnimation();
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(t.getMessage())
                                .show();
                    }
                });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getUserData(final String token) {
        Prefs.putString(AppConfig.KEY_TOKEN, token);
        ApiClient.get(this).getUser("Bearer "+token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    swal.dismissWithAnimation();
                    User resp = response.body();
                    if (response.isSuccessful()) {
                        Prefs.putBoolean(AppConfig.IS_LOGIN, true);
                        initUserData(resp.getUuid(), resp.getName(), resp.getEmail(), resp.getPhone(), resp.getBirthdate(), resp.getGender(), String.valueOf(resp.getCreated_at()));
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Login Berhasil")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                        finish();
                                    }
                                })
                                .show();
                    }
                } catch (Exception e) {
                    swal.dismissWithAnimation();
                    e.printStackTrace();
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Error")
                            .setContentText(e.getMessage())
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                swal.dismissWithAnimation();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(t.getMessage())
                        .show();
            }
        });
    }

    private void initUserData(final String uid, final String name, final String email, final String phone, final String birthdate, final String gender, final String created_at) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserDAO userDAO = new UserDAO();
                userDAO.setUid(1);
                userDAO.setUuid(uid);
                userDAO.setName(name);
                userDAO.setEmail(email);
                userDAO.setPhone(phone);
                userDAO.setBirthdate(birthdate);
                userDAO.setGender(gender);
                AppDatabase.getAppDatabase(LoginActivity.this).userDao().insertAll(userDAO);
            }
        }).start();
    }
}
