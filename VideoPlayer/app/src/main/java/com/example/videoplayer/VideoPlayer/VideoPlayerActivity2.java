package com.example.videoplayer.VideoPlayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.videoplayer.R;
import com.example.videoplayer.Service.FloatingVideoService;
import com.example.videoplayer.databinding.ActivityVideoPlayer2Binding;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.util.Util;

public class VideoPlayerActivity2 extends AppCompatActivity
{
    private static final String TAG = VideoPlayerActivity2.class.getName();

    private PlaybackStateListener playbackStateListener;

    private static final String PLAY_WHEN_READY = "playback_when_ready";
    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_WINDOW = "current_window";
    public static final String VIDEO_URI = "video_uri";

    private ActivityVideoPlayer2Binding videoPlayerBinding;
    private Uri videoUri;

    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        videoPlayerBinding = ActivityVideoPlayer2Binding.inflate(getLayoutInflater());
        setContentView(videoPlayerBinding.getRoot());

        playbackStateListener = new PlaybackStateListener();

        Intent intent = getIntent();

        if(savedInstanceState != null)
        {
            playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
            playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
            currentWindow = savedInstanceState.getInt(CURRENT_WINDOW);
        }

        if(intent != null)
        {
            String uriStr = intent.getStringExtra("video_uri");
            videoUri = Uri.parse(uriStr);
        }
    }

    // Adaptive streaming is a technique for streaming media by varying the quality of the stream based on the available network bandwidth. This allows the
    // user to experience the best-quality media that their bandwidth allows. Typically, the same media content is split into multiple tracks with different
    // qualities (bit rates and resolutions). The player chooses a track based on the available network bandwidth. Each track is split into chunks of a given
    // duration, typically between 2 and 10 seconds. This allows the player to quickly switch between tracks as available bandwidth changes. The player is
    // responsible for stitching these chunks together for seamless playback.
    private void initializePlayer()
    {
        if(player == null)
        {
            // Track selection determines which of the available media tracks are played by the player. Track selection is the responsibility of a TrackSelector.
            // First, create a DefaultTrackSelector, which is responsible for choosing tracks in the media item. Then, tell your trackSelector to only pick
            // tracks of standard definition or lower—a good way of saving your user's data at the expense of quality. Lastly, pass your trackSelector to your
            // builder so that it is used when building the SimpleExoPlayer instance.

            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);

            trackSelector.setParameters(
                trackSelector
                    .buildUponParameters()
                    .setMaxVideoSizeSd()
            );

            player = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build();
        }

        videoPlayerBinding.exoPlayer.setPlayer(player);

        // DASH is a widely used adaptive streaming format. To stream DASH content, you need to create a MediaItem as before. However, this time, we must
        // use a MediaItem.Builder rather than fromUri. This is because fromUri uses the file extension to determine the underlying media format but our
        // DASH URI does not have a file extension so we must supply a MIME type of APPLICATION_MPD when constructing the MediaItem.

        // MediaItem.Builder allows you to create MediaItems with a number of additional properties, including:
        // 1) The MIME type of the media content.
        // 2) Protected content properties including the DRM type, license server URI and license request headers.
        // 3) Side-loaded subtitle files to use during playback.
        // 4) Clipping start and end positions.
        // 5) An advert tag URI for advert insertion.

        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        // If your URI does not end with .mpd, you can pass MimeTypes.APPLICATION_MPD to setMimeType of MediaItem.Builder to explicitly indicate the type
        // of the content. In this comment above MediaItem and uncomment the below.
//         MediaItem mediaItem = new MediaItem.Builder()
//            .setUri(videoUri)
//            .setMimeType(MimeTypes.APPLICATION_MPD)
//            .build();

        player.setMediaItem(mediaItem);

        // Register the listener before the play is prepared.
        player.addListener(playbackStateListener);

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

            player.removeListener(playbackStateListener);

            player.release();

            player = null;
        }
    }

    // ... onPause()-> onSaveInstanceState() -> onStop() -> onDestroy()
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        super.onSaveInstanceState(outState);

        outState.putBoolean(PLAY_WHEN_READY, playWhenReady);
        outState.putLong(PLAYBACK_POSITION, playbackPosition);
        outState.putInt(CURRENT_WINDOW, currentWindow);
    }

    // onCreate()-> onStart()-> onRestoreInstanceState()-> onResume()-> onPostCreate(Bundle) ...
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        // playWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY);
        // playbackPosition = savedInstanceState.getLong(PLAYBACK_POSITION);
        // currentWindow = savedInstanceState.getInt(CURRENT_WINDOW);
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

        if(Util.SDK_INT >= 24)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.video_player_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.float_video :
                Intent serviceIntent = new Intent(VideoPlayerActivity2.this, FloatingVideoService.class);
                serviceIntent.putExtra(VIDEO_URI, videoUri.toString());
                startService(serviceIntent);

                return true;

            default:
                return false;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        if(Util.SDK_INT >= 24)
        {
            releasePlayer();
        }
    }
}