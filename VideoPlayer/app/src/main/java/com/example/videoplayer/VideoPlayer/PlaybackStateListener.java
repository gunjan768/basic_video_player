package com.example.videoplayer.VideoPlayer;

import android.util.Log;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.analytics.PlaybackStatsListener;

// Implement the Player.EventListener interface. This is used to inform you about important player events, including errors and playback state changes.
public class PlaybackStateListener implements Player.EventListener
{
    private static final String TAG = PlaybackStatsListener.class.getName();

    // onPlaybackStateChanged() is called when the playback state changes. The new state is given by the playbackState parameter.
    // 1) ExoPlayer.STATE_IDLE : The player has been instantiated, but has not yet been prepared.
    // 2) ExoPlayer.STATE_BUFFERING : The player is not able to play from the current position because not enough data has been buffered.
    // 3) ExoPlayer.STATE_READY : The player is able to immediately play from the current position. This means the player will start playing media
    // automatically if the player's playWhenReady property is true. If it is false, the player is paused.
    // 4) ExoPlayer.STATE_ENDED : The player has finished playing the media.

    // How do you know if your player is actually playing media? Well, all of the following conditions must be met :
    // 1) playback state is STATE_READY.
    // 2) playWhenReady is true.
    // 3) playback is not suppressed for some other reason (for example, loss of audio focus).
    // Luckily, ExoPlayer provides a convenience method ExoPlayer.isPlaying for exactly this purpose! Or, if you want to be kept informed when isPlaying
    // changes, you can listen for onIsPlayingChanged.

    @Override
    public void onPlaybackStateChanged(int playbackState)
    {
        String stateString;

        switch(playbackState)
        {
            case ExoPlayer.STATE_IDLE:
                stateString = "ExoPlayer.STATE_IDLE      -";
                break;

            case ExoPlayer.STATE_BUFFERING:
                stateString = "ExoPlayer.STATE_BUFFERING -";
                break;

            case ExoPlayer.STATE_READY:
                stateString = "ExoPlayer.STATE_READY     -";
                break;

            case ExoPlayer.STATE_ENDED:
                stateString = "ExoPlayer.STATE_ENDED     -";
                break;

            default:
                stateString = "UNKNOWN_STATE             -";
                break;
        }

        Log.d(TAG, "changed state to " + stateString);
    }
}