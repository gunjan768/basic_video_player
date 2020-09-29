package com.example.videoplayer.VideoPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.example.videoplayer.databinding.ActivityVideoPlayerBinding;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity extends AppCompatActivity
{
    private static final String TAG = "VideoPlayerActivity";
    private static final String APPLICATION_NAME = "Video_Player";

    private ActivityVideoPlayerBinding videoPlayerBinding;
    private Uri videoUri;
    private ExoPlayer exoPlayer;
    private ExtractorsFactory extractorsFactory;

    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        videoPlayerBinding = ActivityVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(videoPlayerBinding.getRoot());

        Intent intent = getIntent();

        if(intent != null)
        {
            String uriStr = intent.getStringExtra("video_uri");
            videoUri = Uri.parse(uriStr);
        }
    }

    // Without using the bandwidth selector.
    private void initializePlayer()
    {
        player = new SimpleExoPlayer.Builder(this).build();
        videoPlayerBinding.exoPlayer.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);

        // MediaItem secondMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3));
        // player.addMediaItem(secondMediaItem);

        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare();
    }

    private void hideSystemUi()
    {
        videoPlayerBinding.exoPlayer.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void releasePlayer()
    {
        if(player != null)
        {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();

            player.release();

            player = null;
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        if(Util.SDK_INT >= 24)
        {
            initializePlayer();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onResume()
    {
        super.onResume();

        hideSystemUi();

        if(Util.SDK_INT < 24 || player == null)
        {
            initializePlayer();
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onPause()
    {
        super.onPause();

        if(Util.SDK_INT < 24)
        {
            releasePlayer();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();

        if(Util.SDK_INT >= 24)
        {
            releasePlayer();
        }
    }
}