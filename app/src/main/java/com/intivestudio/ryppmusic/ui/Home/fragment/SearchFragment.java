package com.intivestudio.ryppmusic.ui.Home.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.ApiMusic;
import com.intivestudio.ryppmusic.data.remote.Music;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.adapter.MusicAdapter;
import com.intivestudio.ryppmusic.ui.Home.adapter.PlaylistDetailAdapter;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView rvMusic;
    private ProgressBar progressBar;
    private EditText etSearch;
    private List<Music> musicList;
    private PlaylistDetailAdapter musicAdapter;

    public SearchFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        rvMusic = (RecyclerView) view.findViewById(R.id.rvMusic);
        progressBar = (ProgressBar) view.findViewById(R.id.progressWheel);
        etSearch = (EditText) view.findViewById(R.id.etPencarian);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etSearch.getText().length() >= 3) {
                    initSearch(etSearch.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        musicList = new ArrayList<>();
        musicAdapter = new PlaylistDetailAdapter(getContext(), musicList);
        rvMusic.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        rvMusic.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        rvMusic.setLayoutManager(mLayoutManager);
        rvMusic.setItemAnimator(new DefaultItemAnimator());
        rvMusic.setAdapter(musicAdapter);

        return view;
    }

    private void initSearch(String title) {
        ApiClient.get(getContext()).getSearchMusic(title,"Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""))
                .enqueue(new Callback<ApiMusic>() {
                    @Override
                    public void onResponse(Call<ApiMusic> call, Response<ApiMusic> response) {
                        try {
                            ApiMusic resp = response.body();
                            if (!resp.getData().isEmpty()) {
                                musicList.clear();
                                progressBar.setVisibility(View.GONE);
                                rvMusic.setVisibility(View.VISIBLE);
                                musicList.addAll(resp.getData());
                                musicAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiMusic> call, Throwable t) {
                        System.out.println("Error : "+t.getMessage());
                        Toast.makeText(getActivity(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
