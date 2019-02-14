package com.intivestudio.ryppmusic.ui.Home.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.ApiDefault;
import com.intivestudio.ryppmusic.data.remote.Playlist;
import com.intivestudio.ryppmusic.data.services.ApiClient;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.ui.PlaylistDetail.AddPlaylistActivity;
import com.intivestudio.ryppmusic.utils.AppConfig;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPlaylistAdapter extends RecyclerView.Adapter<AddPlaylistAdapter.MyViewHolder> {

    private Context mContext;
    private List<Playlist> dataPlaylist;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public FrameLayout frameLayout;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.text);
            frameLayout = (FrameLayout) view.findViewById(R.id.frame);
        }

    }

    public AddPlaylistAdapter(Context mContext, List<Playlist> dataPlaylist) {
        this.mContext = mContext;
        this.dataPlaylist = dataPlaylist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_newest, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Playlist playlist = dataPlaylist.get(position);
        holder.title.setText(playlist.getPlaylistName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Playlist Dipilih")
                        .setContentText("Apakah anda yakin ?")
                        .setConfirmText("Ya")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = ((Activity) mContext).getIntent();
                                addToPlaylist(String.valueOf(playlist.getId()), intent.getStringExtra("music_id"));
                            }
                        })
                        .setCancelText("Tidak")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPlaylist.size();
    }

    private void addToPlaylist(String play_id, String music_id) {
        ApiClient.get(mContext).addToPlaylist("Bearer "+Prefs.getString(AppConfig.KEY_TOKEN, ""), music_id, play_id)
                .enqueue(new Callback<ApiDefault>() {
                    @Override
                    public void onResponse(Call<ApiDefault> call, Response<ApiDefault> response) {
                        try {
                            ApiDefault resp = response.body();
                            if (resp.getStatus()) {
                                new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success")
                                        .setContentText(resp.getMessage())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                mContext.startActivity(new Intent(mContext, HomeActivity.class));
                                                ((Activity) mContext).finish();
                                            }
                                        })
                                        .show();
                            } else {
                                new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Error")
                                        .setContentText(resp.getMessage())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                sweetAlertDialog.dismissWithAnimation();
                                                mContext.startActivity(new Intent(mContext, HomeActivity.class));
                                                ((Activity) mContext).finish();
                                            }
                                        })
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Error")
                                    .setContentText(e.getMessage())
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                            sweetAlertDialog.dismissWithAnimation();
                                            mContext.startActivity(new Intent(mContext, HomeActivity.class));
                                            ((Activity) mContext).finish();
                                        }
                                    })
                                    .show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiDefault> call, Throwable t) {
                        new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Error")
                                .setContentText(t.getMessage())
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                })
                                .show();
                    }
                });
    }
}