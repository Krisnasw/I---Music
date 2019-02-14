package com.intivestudio.ryppmusic.ui.Profile;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.local.UserDAO;
import com.intivestudio.ryppmusic.data.remote.ApiDefault;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.intivestudio.ryppmusic.utils.AppDatabase;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.pixplicity.easyprefs.library.Prefs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    private ImageView mIvAvatarRegister;
    private RelativeLayout mChangeAvatarRegister;
    /**
     * Nama Lengkap
     */
    @NotEmpty
    private EditText mEtNama;
    /**
     * Alamat E - Mail
     */
    @NotEmpty
    @Email
    private EditText mEtEmail;
    /**
     * No. Telp
     */
    @NotEmpty
    private EditText mEtPhone;
    /**
     * Tanggal Lahir
     */
    @NotEmpty
    private TextView mEtTanggalLahir;
    /**
     * Ubah Profile
     */
    private Button mBtnSubmit;
    private UserDAO user;
    private Validator validator;
    private SweetAlertDialog swal;

    SimpleDateFormat dateParsingFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private Date dateParsed;
    private String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (Prefs.getBoolean(AppConfig.IS_LOGIN, false)) {
            user = AppDatabase.getAppDatabase(this).userDao().findByName("1");
        }

        initView();

        dateString = "";
        dateParsed = new Date();

        validator = new Validator(this);
        validator.setValidationListener(this);

        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swal = new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        swal.setTitleText("Loading");
        swal.setContentText("Please wait...");
        swal.setCancelable(false);
    }

    private void initView() {
        mIvAvatarRegister = (ImageView) findViewById(R.id.iv_avatar_register);
        Glide.with(this).load("https://png.pngtree.com/svg/20170602/avatar_107646.png").into(mIvAvatarRegister);
        mChangeAvatarRegister = (RelativeLayout) findViewById(R.id.change_avatar_register);
        mEtNama = (EditText) findViewById(R.id.et_nama);
        mEtNama.setText(user.getName());
        mEtEmail = (EditText) findViewById(R.id.et_email);
        mEtEmail.setFocusable(false);
        mEtEmail.setText(user.getEmail());
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mEtPhone.setText(user.getPhone());
        mEtTanggalLahir = (TextView) findViewById(R.id.et_tanggal_lahir);
        mEtTanggalLahir.setText(user.getBirthdate());
        mEtTanggalLahir.setOnClickListener(this);
        mBtnSubmit = (Button) findViewById(R.id.btnSubmit);
        mBtnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.et_tanggal_lahir:
                DatePickerDialog fromStartDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            dateString = year + "-" + (((monthOfYear + 1) + "").length() == 1 ? ((monthOfYear + 1) + "") : ("0" + (monthOfYear + 1))) + "-" + dayOfMonth;
                            dateParsed = dateParsingFormat.parse(dateString);
                            mEtTanggalLahir.setText(dateFormat.format(dateParsed));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, Integer.parseInt(yearFormat.format(dateParsed)), Integer.parseInt(monthFormat.format(dateParsed)) - 1, Integer.parseInt(dayFormat.format(dateParsed)));
                fromStartDate.show();
                break;
            case R.id.btnSubmit:
                validator.validate();
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        swal.show();
        ApiClient.get(this).doUpdateProfile(mEtNama.getText().toString(), mEtPhone.getText().toString(), mEtTanggalLahir.getText().toString(), "Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""))
                .enqueue(new Callback<ApiDefault>() {
                    @Override
                    public void onResponse(Call<ApiDefault> call, Response<ApiDefault> response) {
                        swal.dismissWithAnimation();
                        try {
                            ApiDefault resp = response.body();
                            if (resp.getStatus()) {
                                new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText(resp.getMessage())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                                                finish();
                                            }
                                        })
                                        .show();
                            } else {
                                new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error")
                                        .setContentText(resp.getMessage())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiDefault> call, Throwable t) {
                        swal.dismissWithAnimation();
                        new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(t.getMessage())
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        finish();
                                    }
                                })
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
