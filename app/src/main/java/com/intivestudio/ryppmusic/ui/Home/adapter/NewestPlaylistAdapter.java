package com.intivestudio.ryppmusic.ui.Home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.Playlist;
import com.intivestudio.ryppmusic.ui.PlaylistDetail.PlaylistDetailActivity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NewestPlaylistAdapter extends RecyclerView.Adapter<NewestPlaylistAdapter.MyViewHolder> {

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

    public NewestPlaylistAdapter(Context mContext, List<Playlist> dataPlaylist) {
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
    public void onBindViewHolder(@NonNull NewestPlaylistAdapter.MyViewHolder holder, int position) {
        final Playlist playlist = dataPlaylist.get(position);
        holder.title.setText(playlist.getPlaylistName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlaylistDetailActivity.class);
                intent.putExtra("id", playlist.getUuid());
                intent.putExtra("title", playlist.getPlaylistName());
                intent.putExtra("name", playlist.getName());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataPlaylist.size();
    }
}
