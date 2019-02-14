package com.intivestudio.ryppmusic.ui.Register;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.ApiDefault;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Login.LoginActivity;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, Validator.ValidationListener {

    /**
     * Daftar Sekarang
     */
    private TextView mBtnRegister;
    /**
     * Sudah punya akun ? Login
     */
    private TextView mBtnBackLogin;
    SimpleDateFormat dateParsingFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
    private Date dateParsed;
    private String dateString;
    /**
     * Nama Lengkap
     */
    @NotEmpty
    private EditText mEtName;
    /**
     * No. Telp
     */
    @NotEmpty
    private EditText mEtPhone;
    /**
     * Tanggal Lahir
     */
    @NotEmpty
    private EditText mEtBirthdate;
    /**
     * Email Address
     */
    @NotEmpty
    @Email
    private EditText mEtEmails;
    /**
     * Password
     */
    @NotEmpty
    private EditText mEtPasswd;

    private Validator validator;
    private SweetAlertDialog swal;
    /**
     * Laki - Laki
     */
    private RadioButton mRadioMale;
    /**
     * Perempuan
     */
    private RadioButton mRadioFemale;
    private RadioGroup mRadioSex;

    @Override
    protected void onCreate(Bundle savedInstancestate) {
        super.onCreate(savedInstancestate);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
        initView();

        dateString = "";
        dateParsed = new Date();

        validator = new Validator(this);
        validator.setValidationListener(this);

        swal = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        swal.setTitleText("Loading");
        swal.setContentText("Please wait...");
        swal.setCancelable(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        mBtnRegister = (TextView) findViewById(R.id.btnRegister);
        mBtnRegister.setOnClickListener(this);
        mBtnBackLogin = (TextView) findViewById(R.id.btnBackLogin);
        mBtnBackLogin.setOnClickListener(this);
        mEtName = (EditText) findViewById(R.id.etName);
        mEtPhone = (EditText) findViewById(R.id.etPhone);
        mEtBirthdate = (EditText) findViewById(R.id.etBirthdate);
        mEtBirthdate.setOnClickListener(this);
        mEtEmails = (EditText) findViewById(R.id.etEmails);
        mEtPasswd = (EditText) findViewById(R.id.etPasswd);
        mRadioMale = (RadioButton) findViewById(R.id.radioMale);
        mRadioFemale = (RadioButton) findViewById(R.id.radioFemale);
        mRadioSex = (RadioGroup) findViewById(R.id.radioSex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btnRegister:
                validator.validate();
                break;
            case R.id.btnBackLogin:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.etBirthdate:
                DatePickerDialog fromStartDate = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        try {
                            dateString = year + "-" + (((monthOfYear + 1) + "").length() == 1 ? ((monthOfYear + 1) + "") : ("0" + (monthOfYear + 1))) + "-" + dayOfMonth;
                            dateParsed = dateParsingFormat.parse(dateString);
                            mEtBirthdate.setText(dateFormat.format(dateParsed));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, Integer.parseInt(yearFormat.format(dateParsed)), Integer.parseInt(monthFormat.format(dateParsed)) - 1, Integer.parseInt(dayFormat.format(dateParsed)));
                fromStartDate.show();
                break;
        }
    }

    @Override
    public void onValidationSucceeded() {
        swal.show();
        String gender = "";
        if (mRadioMale.isChecked()) {
            gender = "L";
        } else if (mRadioFemale.isChecked()) {
            gender = "P";
        }
        ApiClient.get(this).doRegister(mEtName.getText().toString(), mEtEmails.getText().toString(), mEtPhone.getText().toString(), gender, mEtBirthdate.getText().toString(), mEtPasswd.getText().toString())
                .enqueue(new Callback<ApiDefault>() {
                    @Override
                    public void onResponse(Call<ApiDefault> call, Response<ApiDefault> response) {
                        try {
                            swal.dismissWithAnimation();
                            ApiDefault resp = response.body();
                            if (resp.getStatus()) {
                                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText(resp.getMessage())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            }
                                        })
                                        .show();
                            } else {
                                new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error")
                                        .setContentText(resp.getMessage())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
                                                finish();
                                            }
                                        })
                                        .show();
                            }
                        } catch (Exception e) {
                            swal.dismissWithAnimation();
                            e.printStackTrace();
                            new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            startActivity(new Intent(RegisterActivity.this, RegisterActivity.class));
                                            finish();
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiDefault> call, Throwable t) {
                        swal.dismissWithAnimation();
                        new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
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
}
