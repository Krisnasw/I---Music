package com.intivestudio.ryppmusic.ui.Home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;
import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.local.UserDAO;
import com.intivestudio.ryppmusic.data.remote.User;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.fragment.AccountFragment;
import com.intivestudio.ryppmusic.ui.Home.fragment.HomeFragment;
import com.intivestudio.ryppmusic.ui.Home.fragment.LibraryFragment;
import com.intivestudio.ryppmusic.ui.Home.fragment.SearchFragment;
import com.intivestudio.ryppmusic.ui.Login.LoginActivity;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.intivestudio.ryppmusic.utils.AppDatabase;
import com.pixplicity.easyprefs.library.Prefs;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private FrameLayout mContentContainer;
    private NestedScrollView mMyScrollingContent;
    private BottomBar mBottomBar;
    private RelativeLayout mLayout;
    private SweetAlertDialog swal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();

        swal = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        swal.setTitleText("Loading");
        swal.setContentText("Please wait...");
        swal.setCancelable(false);

        if (Prefs.getBoolean(AppConfig.IS_LOGIN, false)) {
            getUserData(Prefs.getString(AppConfig.KEY_TOKEN, ""));
        }

        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.home) {
                    attachFragment(new HomeFragment());
                } else if (tabId == R.id.search) {
                    attachFragment(new SearchFragment());
                } else if (tabId == R.id.library) {
                    attachFragment(new LibraryFragment());
                } else if (tabId == R.id.account) {
                    attachFragment(new AccountFragment());
                }
            }
        });
    }

    private void initView() {
        mLayout = (RelativeLayout) findViewById(R.id.layout);
        mContentContainer = (FrameLayout) findViewById(R.id.contentContainer);
//        mMyScrollingContent = (NestedScrollView) findViewById(R.id.myScrollingContent);
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
    }

    public void attachFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        trans.replace(R.id.contentContainer, fragment);
        trans.commit();
    }

    private void getUserData(final String token) {
        swal.show();
        Prefs.putString(AppConfig.KEY_TOKEN, token);
        ApiClient.get(this).getUser("Bearer "+token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    swal.dismissWithAnimation();
                    User resp = response.body();
                    if (response.isSuccessful()) {
                        Prefs.putBoolean(AppConfig.IS_LOGIN, true);
                        initUserData(resp.getUuid(), resp.getName(), resp.getEmail(), resp.getPhone(), resp.getBirthdate(), resp.getGender());
                    }
                } catch (Exception e) {
                    swal.dismissWithAnimation();
                    e.printStackTrace();
                    new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
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
                new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error")
                        .setContentText(t.getMessage())
                        .show();
            }
        });
    }

    private void initUserData(final String uid, final String name, final String email, final String phone, final String birthdate, final String gender) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase.getAppDatabase(HomeActivity.this).userDao().updateUser(name, phone, birthdate);
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        Snackbar snackbar = Snackbar
                .make(mLayout, "Apakah Anda Yakin Akan Keluar ?", Snackbar.LENGTH_LONG)
                .setAction("YA", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        Prefs.clear();
                        finish();
                    }
                });

        snackbar.show();
    }

}
