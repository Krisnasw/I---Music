package com.intivestudio.ryppmusic.ui.Home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.local.UserDAO;
import com.intivestudio.ryppmusic.ui.Login.LoginActivity;
import com.intivestudio.ryppmusic.ui.Profile.ProfileActivity;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.intivestudio.ryppmusic.utils.AppDatabase;
import com.pixplicity.easyprefs.library.Prefs;

import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment implements View.OnClickListener {

    LinearLayout mEditProfile, mTopup, mTransferSaldo, mKeluar;
    TextView mNamaProfile, mUserEmail;
    ImageView mAvatarImg;
    private UserDAO user;

    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        if (Prefs.getBoolean(AppConfig.IS_LOGIN, false)) {
            user = AppDatabase.getAppDatabase(getContext()).userDao().findByName("1");
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mEditProfile = (LinearLayout) view.findViewById(R.id.edit_profil);
        mTopup = (LinearLayout) view.findViewById(R.id.top_up);
        mTransferSaldo = (LinearLayout) view.findViewById(R.id.transfer_saldo);
        mKeluar = (LinearLayout) view.findViewById(R.id.keluar);
        mNamaProfile = (TextView) view.findViewById(R.id.nama_profil);
        mNamaProfile.setText(""+user.getName());
        mUserEmail = (TextView) view.findViewById(R.id.user_email);
        mUserEmail.setText(""+user.getEmail());
        mAvatarImg = (ImageView) view.findViewById(R.id.avatar_profile);
        Glide.with(getContext()).load("https://png.pngtree.com/svg/20170602/avatar_107646.png").into(mAvatarImg);

        mEditProfile.setOnClickListener(this);
        mKeluar.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profil:
                startActivity(new Intent(getContext(), ProfileActivity.class));
                break;
            case R.id.keluar:
                AppDatabase.getAppDatabase(getContext()).userDao().nukeTable();
                Prefs.clear();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
                break;
        }
    }
}
