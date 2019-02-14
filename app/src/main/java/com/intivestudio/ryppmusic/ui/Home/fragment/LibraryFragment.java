package com.intivestudio.ryppmusic.ui.Home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.ApiDefault;
import com.intivestudio.ryppmusic.data.remote.ApiPlaylist;
import com.intivestudio.ryppmusic.data.remote.Playlist;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.ui.Home.adapter.NewestPlaylistAdapter;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryFragment extends Fragment {

    private RecyclerView rvPlaylist;
    private ProgressBar progressBar;
    private NewestPlaylistAdapter newestPlaylistAdapter;
    private FloatingActionButton floatingActionButton;
    private static final String TAG = LibraryFragment.class.getSimpleName();
    private List<Playlist> playlistList;

    public LibraryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_library, container, false);

        rvPlaylist = view.findViewById(R.id.rvPlaylist);
        progressBar = view.findViewById(R.id.progressPlaylist);
        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final View layout = getLayoutInflater().inflate(R.layout.dialog_add_playlist, null);
                final BottomSheetDialog dialog = new BottomSheetDialog(getContext());
                dialog.setContentView(layout);
                dialog.show();

                final EditText editText = (EditText) layout.findViewById(R.id.etPlaylistName);
                final Button btnSubmit = (Button) layout.findViewById(R.id.btnSubmit);
                final Button mBtnKembali = (Button) layout.findViewById(R.id.kembali);
                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editText.getText().equals("")) {
                            editText.setError("Tidak Boleh Kosong");
                        } else {
                            mBtnKembali.setEnabled(false);
                            btnSubmit.setEnabled(false);
                            btnSubmit.setText("Please wait...");
                            ApiClient.get(getContext()).addPlaylist(editText.getText().toString(), "Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""))
                                    .enqueue(new Callback<ApiDefault>() {
                                        @Override
                                        public void onResponse(Call<ApiDefault> call, Response<ApiDefault> response) {
                                            try {
                                                ApiDefault resp = response.body();
                                                if (resp.getStatus()) {
                                                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                            .setTitleText("Success")
                                                            .setContentText(resp.getMessage())
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    dialog.dismiss();
                                                                    sweetAlertDialog.dismissWithAnimation();
                                                                    initPlaylist();
                                                                }
                                                            })
                                                            .show();
                                                } else {
                                                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                            .setTitleText("Error")
                                                            .setContentText(resp.getMessage())
                                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                                @Override
                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                    dialog.dismiss();
                                                                    sweetAlertDialog.dismissWithAnimation();
                                                                    getActivity().startActivity(new Intent(getContext(), HomeActivity.class));
                                                                    getActivity().finish();
                                                                }
                                                            })
                                                            .show();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                        .setTitleText("Error")
                                                        .setContentText(e.getMessage())
                                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                            @Override
                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                                dialog.dismiss();
                                                                sweetAlertDialog.dismissWithAnimation();
                                                                getActivity().startActivity(new Intent(getContext(), HomeActivity.class));
                                                                getActivity().finish();
                                                            }
                                                        })
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ApiDefault> call, Throwable t) {
                                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                                    .setTitleText("Error")
                                                    .setContentText(t.getMessage())
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            dialog.dismiss();
                                                            sweetAlertDialog.dismissWithAnimation();
                                                            getActivity().startActivity(new Intent(getContext(), HomeActivity.class));
                                                            getActivity().finish();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    });
                        }
                    }
                });
                mBtnKembali.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        playlistList = new ArrayList<>();
        newestPlaylistAdapter = new NewestPlaylistAdapter(getContext(), playlistList);
        rvPlaylist.setHasFixedSize(true);
        LinearLayoutManager linearVertical = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        rvPlaylist.setLayoutManager(linearVertical);
        rvPlaylist.setItemAnimator(new DefaultItemAnimator());
        rvPlaylist.setAdapter(newestPlaylistAdapter);

        return view;
    }

    private void initPlaylist() {
        ApiClient.get(getContext()).getPlaylist("Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""))
                .enqueue(new Callback<ApiPlaylist>() {
                    @Override
                    public void onResponse(Call<ApiPlaylist> call, Response<ApiPlaylist> response) {
                        try {
                            if (response.isSuccessful()) {
                                ApiPlaylist resp = response.body();
                                if (resp.getStatus()) {
                                    if (!resp.getData().isEmpty()) {
                                        playlistList.clear();
                                        progressBar.setVisibility(View.GONE);
                                        rvPlaylist.setVisibility(View.VISIBLE);
                                        playlistList.addAll(resp.getData());
                                        newestPlaylistAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiPlaylist> call, Throwable t) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(t.getMessage())
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        getActivity().startActivity(new Intent(getContext(), HomeActivity.class));
                                        getActivity().finish();
                                    }
                                })
                                .show();
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        initPlaylist();
    }
}
