package com.intivestudio.ryppmusic.ui.Music;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.intivestudio.ryppmusic.R;
import com.intivestudio.ryppmusic.data.remote.Music;
import com.intivestudio.ryppmusic.ui.Home.HomeActivity;
import com.intivestudio.ryppmusic.utils.Utilities;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MusicActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnCompletionListener {

    private ImageView mImageMusic;
    private SeekBar mSeekBar;
    /**
     * Ready set Go
     */
    private TextView mMusicTitle;
    /**
     * Royal Tailor
     */
    private TextView mMusicAuthor;
    private TextView mStartDur;
    private TextView mEndDur;
    private ImageView mBtnRepeat;
    private ImageView mBtnStart;
    private ImageView mBtnShare;
    private MediaPlayer mediaPlayer;
    private Utilities utils;
    int progress = 0;
    private Handler mHandler = new Handler();
    long totalDuration;
    long currentDuration;
    private boolean isRepeat = false;
    public static final String TAG = "Player Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initView();

        getSupportActionBar().setTitle(getIntent().getStringExtra("musicTitle"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        final Intent intent = getIntent();

        mMusicTitle.setText(getIntent().getStringExtra("musicTitle"));
        mMusicAuthor.setText(getIntent().getStringExtra("musicAuthor"));
        Glide.with(this)
                .load("http://rypp.gabut.in/"+getIntent().getStringExtra("musicImage"))
                .into(mImageMusic);

        mediaPlayer = new MediaPlayer();
        utils = new Utilities();
        mSeekBar.setOnSeekBarChangeListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                        mBtnStart.setImageResource(R.drawable.ic_play_button);
                    }
                } else {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                        mBtnStart.setImageResource(R.drawable.ic_pause);
                    }
                }
            }
        });
        mBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                txtIntent .setType("text/plain");
                txtIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "RyppMusic");
                txtIntent .putExtra(android.content.Intent.EXTRA_TEXT, "Dengarkan Lagu "+getIntent().getStringExtra("musicTitle")+" Di RyppMusic");
                startActivity(Intent.createChooser(txtIntent ,"Share"));
            }
        });
        mBtnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRepeat){
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat);
                    mBtnRepeat.setColorFilter(ContextCompat.getColor(MusicActivity.this, R.color.colorPrimary));
                }else{
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    mBtnRepeat.setImageResource(R.drawable.ic_repeat);
                    mBtnRepeat.setColorFilter(Color.BLACK);
                }
            }
        });
    }

    private void initView() {
        mImageMusic = (ImageView) findViewById(R.id.imageMusic);
        mImageMusic.setOnClickListener(this);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnClickListener(this);
        mMusicTitle = (TextView) findViewById(R.id.musicTitle);
        mMusicAuthor = (TextView) findViewById(R.id.musicAuthor);
        mBtnRepeat = (ImageView) findViewById(R.id.btnRepeat);
        mBtnRepeat.setOnClickListener(this);
        mBtnStart = (ImageView) findViewById(R.id.btnStart);
        mBtnStart.setOnClickListener(this);
        mBtnShare = (ImageView) findViewById(R.id.btnShare);
        mBtnShare.setOnClickListener(this);
        mStartDur = (TextView) findViewById(R.id.startDur);
        mEndDur = (TextView) findViewById(R.id.endDur);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.imageMusic:
                break;
            case R.id.seekBar:
                break;
            case R.id.btnRepeat:
                break;
            case R.id.btnStart:
                break;
            case R.id.btnShare:
                break;
        }
    }

    private void playSong() {
        try {
            mediaPlayer.reset();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA) .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC) .build());
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(this, Uri.parse("https://rypp.gabut.in/"+getIntent().getStringExtra("musicFiles")));
            mediaPlayer.prepare();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    // Display error
                    return false;
                }
            });

            mBtnStart.setImageResource(R.drawable.ic_pause);
            mSeekBar.setProgress(0);
            mSeekBar.setMax(100);
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            try {
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                mEndDur.setText(""+utils.milliSecondsToTimer(totalDuration));
                mStartDur.setText(""+utils.milliSecondsToTimer(currentDuration));

                int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
                mSeekBar.setProgress(progress);
                mHandler.postDelayed(this, 100);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        mediaPlayer.seekTo(currentPosition);
        updateProgressBar();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(isRepeat) {
            playSong();
        } else {
            playSong();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.release();
                }
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
