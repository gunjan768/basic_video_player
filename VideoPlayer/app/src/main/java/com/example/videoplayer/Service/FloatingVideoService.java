package com.example.videoplayer.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.videoplayer.R;
import com.example.videoplayer.databinding.CustomPopUpWindowBinding;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import static com.example.videoplayer.VideoPlayer.VideoPlayerActivity2.VIDEO_URI;

public class FloatingVideoService extends Service
{
    private WindowManager windowManager;
    private View floatingVideoView;
    private SimpleExoPlayer player;
    private PlayerView playerView;

    private Uri videoUri;
    private Context context;

    private CustomPopUpWindowBinding customPopUpWindowBinding;

    public FloatingVideoService()
    {

    }

    public FloatingVideoService(Context context)
    {
        this.context = context;
        customPopUpWindowBinding = CustomPopUpWindowBinding.inflate(LayoutInflater.from(context), null, false);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if(intent != null)
        {
            String videoUriString = intent.getStringExtra(VIDEO_URI);
            videoUri = Uri.parse(videoUriString);

            if(windowManager != null && floatingVideoView.isShown() && player != null)
            {
                windowManager.removeView(floatingVideoView);
                floatingVideoView = null;
                windowManager = null;
                player.setPlayWhenReady(false);
                player.release();
                player = null;
            }

            final WindowManager.LayoutParams layoutParams;

            floatingVideoView = LayoutInflater.from(this).inflate(R.layout.custom_pop_up_window, null);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                layoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                );
            }
            else
            {
                // WindowManager.LayoutParams.TYPE_PHONE is deprecated.
                layoutParams = new WindowManager.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT
                );
            }

            layoutParams.gravity = Gravity.TOP | Gravity.START;
            layoutParams.x = 200;  layoutParams.y = 200;

            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.addView(floatingVideoView, layoutParams);

            customPopUpWindowBinding.imageViewMaximize.setOnClickListener(view ->
            {

            });

            customPopUpWindowBinding.imageViewDismiss.setOnClickListener(view ->
            {

            });

            initializePlayer();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void initializePlayer()
    {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if(floatingVideoView != null)
        {
            windowManager.removeView(floatingVideoView);
        }
    }
}
