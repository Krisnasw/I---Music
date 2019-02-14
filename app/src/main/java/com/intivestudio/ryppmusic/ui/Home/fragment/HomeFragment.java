package com.intivestudio.ryppmusic.ui.Home.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.ApiMusic;
import com.intivestudio.ryppmusic.data.remote.ApiPlaylist;
import com.intivestudio.ryppmusic.data.remote.Music;
import com.intivestudio.ryppmusic.data.remote.Playlist;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.ui.Home.adapter.MusicAdapter;
import com.intivestudio.ryppmusic.ui.Home.adapter.NewestPlaylistAdapter;
import com.intivestudio.ryppmusic.ui.Login.LoginActivity;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.intivestudio.ryppmusic.utils.AppDatabase;
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

public class HomeFragment extends Fragment {

    private RecyclerView rvMusic;
    private RecyclerView rvNewMusic;
    private ProgressBar progressBar, progressBar1;
    private MusicAdapter musicAdapter;
    private NewestPlaylistAdapter playlistAdapter;
    private List<Playlist> playlistList;
    private List<Music> musicList;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (!Prefs.getBoolean(AppConfig.IS_LOGIN, false)) {
            AppDatabase.getAppDatabase(getContext()).userDao().nukeTable();
            Prefs.clear();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }

        rvMusic = (RecyclerView) view.findViewById(R.id.rvMusic);
        rvNewMusic = (RecyclerView) view.findViewById(R.id.rvNewMusic);
        progressBar = view.findViewById(R.id.progressWheel);
        progressBar1 = view.findViewById(R.id.progressWheel1);

        // Adapter
        musicList = new ArrayList<>();
        musicAdapter = new MusicAdapter(getContext(), musicList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        rvMusic.setLayoutManager(mLayoutManager);
        rvMusic.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        rvMusic.setItemAnimator(new DefaultItemAnimator());
        rvMusic.setAdapter(musicAdapter);

        playlistList = new ArrayList<>();
        playlistAdapter = new NewestPlaylistAdapter(getContext(), playlistList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        rvNewMusic.setLayoutManager(layoutManager);
        rvNewMusic.setItemAnimator(new DefaultItemAnimator());
        rvNewMusic.setAdapter(playlistAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initMusic();
        initPlaylist();
    }

    private void initMusic() {
        ApiClient.get(getContext()).getMusic("Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""))
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
                            new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiMusic> call, Throwable t) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(t.getMessage())
                                .show();
                    }
                });
    }

    private void initPlaylist() {
        ApiClient.get(getContext()).getLatestPlaylist("Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""))
                .enqueue(new Callback<ApiPlaylist>() {
                    @Override
                    public void onResponse(Call<ApiPlaylist> call, Response<ApiPlaylist> response) {
                        try {
                            if (response.isSuccessful()) {
                                ApiPlaylist resp = response.body();
                                if (resp.getStatus()) {
                                    if (!resp.getData().isEmpty()) {
                                        playlistList.clear();
                                        progressBar1.setVisibility(View.GONE);
                                        rvNewMusic.setVisibility(View.VISIBLE);
                                        playlistList.addAll(resp.getData());
                                        playlistAdapter.notifyDataSetChanged();
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

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
