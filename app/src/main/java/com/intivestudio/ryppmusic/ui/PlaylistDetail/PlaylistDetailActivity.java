package com.intivestudio.ryppmusic.ui.PlaylistDetail;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.ApiMusic;
import com.intivestudio.ryppmusic.data.remote.Music;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.adapter.MusicAdapter;
import com.intivestudio.ryppmusic.ui.Home.adapter.PlaylistDetailAdapter;
import com.intivestudio.ryppmusic.ui.Home.fragment.HomeFragment;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaylistDetailActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Loading...
     */
    private TextView mPlaylistTitle;
    /**
     * Loading...
     */
    private TextView mPlaylistBy;
    private RecyclerView mRvMusic;
    private ProgressBar mProgressWheel;
    private PlaylistDetailAdapter musicAdapter;
    private List<Music> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_detail);
        initView();

        getSupportActionBar().setTitle("Detail Playlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mPlaylistTitle.setText(intent.getStringExtra("title"));
        mPlaylistBy.setText(intent.getStringExtra("name"));

        musicList = new ArrayList<>();
        musicAdapter = new PlaylistDetailAdapter(this, musicList);
        mRvMusic.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRvMusic.setLayoutManager(mLayoutManager);
        mRvMusic.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        mRvMusic.setItemAnimator(new DefaultItemAnimator());
        mRvMusic.setAdapter(musicAdapter);
    }

    private void initView() {
        mPlaylistTitle = (TextView) findViewById(R.id.playlistTitle);
        mPlaylistBy = (TextView) findViewById(R.id.playlistBy);
        mRvMusic = (RecyclerView) findViewById(R.id.rvMusic);
        mProgressWheel = (ProgressBar) findViewById(R.id.progressWheel);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
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

    private void initMusic(String id) {
        ApiClient.get(this).getDetailPlaylist("Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""), id)
                .enqueue(new Callback<ApiMusic>() {
                    @Override
                    public void onResponse(Call<ApiMusic> call, Response<ApiMusic> response) {
                        try {
                            ApiMusic resp = response.body();
                            if (!resp.getData().isEmpty()) {
                                musicList.clear();
                                mProgressWheel.setVisibility(View.GONE);
                                mRvMusic.setVisibility(View.VISIBLE);
                                musicList.addAll(resp.getData());
                                musicAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            new SweetAlertDialog(PlaylistDetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiMusic> call, Throwable t) {
                        new SweetAlertDialog(PlaylistDetailActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(t.getMessage())
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

    @Override
    public void onStart() {
        super.onStart();
        initMusic(getIntent().getStringExtra("id"));
    }
}
