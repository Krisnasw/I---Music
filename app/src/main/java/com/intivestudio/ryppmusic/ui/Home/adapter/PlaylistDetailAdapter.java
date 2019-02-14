package com.intivestudio.ryppmusic.ui.Home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.Music;
import com.intivestudio.ryppmusic.ui.Music.MusicActivity;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class PlaylistDetailAdapter extends RecyclerView.Adapter<PlaylistDetailAdapter.MyViewHolder> {

    private Context mContext;
    private List<Music> albumList;
    private String musicAuthor, musicTitle, images, files, musicId;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.tvTitle);
        }
    }

    public PlaylistDetailAdapter(Context mContext, List<Music> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Music music = albumList.get(position);
        holder.title.setText(music.getMusic_title() + " - " + music.getMusic_author());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MusicActivity.class);
                intent.putExtra("musicAuthor", music.getMusic_author());
                intent.putExtra("musicTitle", music.getMusic_title());
                intent.putExtra("musicImage", music.getImages());
                intent.putExtra("musicFiles", music.getFiles());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }
}
