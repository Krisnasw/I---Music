package com.intivestudio.ryppmusic.ui.PlaylistDetail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.ApiPlaylist;
import com.intivestudio.ryppmusic.data.remote.Playlist;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.ui.Home.adapter.AddPlaylistAdapter;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlaylistActivity extends AppCompatActivity {

    /**
     * - List Playlist
     */
    private TextView mTvPlaylist;
    private RecyclerView mRvPlaylist;
    private ProgressBar mProgressPlaylist;
    private AddPlaylistAdapter addPlaylistAdapter;
    private List<Playlist> playlistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_playlist);

        initView();

        getSupportActionBar().setTitle("Pilih Playlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        playlistList = new ArrayList<>();
        addPlaylistAdapter = new AddPlaylistAdapter(this, playlistList);
        mRvPlaylist.setHasFixedSize(true);
        LinearLayoutManager linearVertical = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mRvPlaylist.setLayoutManager(linearVertical);
        mRvPlaylist.setItemAnimator(new DefaultItemAnimator());
        mRvPlaylist.setAdapter(addPlaylistAdapter);
    }

    private void initView() {
        mTvPlaylist = (TextView) findViewById(R.id.tvPlaylist);
        mRvPlaylist = (RecyclerView) findViewById(R.id.rvPlaylist);
        mProgressPlaylist = (ProgressBar) findViewById(R.id.progressPlaylist);
    }

    private void initPlaylist() {
        ApiClient.get(this).getPlaylist("Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""))
                .enqueue(new Callback<ApiPlaylist>() {
                    @Override
                    public void onResponse(Call<ApiPlaylist> call, Response<ApiPlaylist> response) {
                        try {
                            if (response.isSuccessful()) {
                                ApiPlaylist resp = response.body();
                                if (resp.getStatus()) {
                                    if (!resp.getData().isEmpty()) {
                                        playlistList.clear();
                                        mProgressPlaylist.setVisibility(View.GONE);
                                        mRvPlaylist.setVisibility(View.VISIBLE);
                                        playlistList.addAll(resp.getData());
                                        addPlaylistAdapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            new SweetAlertDialog(AddPlaylistActivity.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiPlaylist> call, Throwable t) {
                        new SweetAlertDialog(AddPlaylistActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(t.getMessage())
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        startActivity(new Intent(AddPlaylistActivity.this, HomeActivity.class));
                                        finish();
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
