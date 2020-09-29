package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.videoplayer.VideoPlayer.VideoPlayerActivity2;
import com.example.videoplayer.Adapter.VideoAdapter;
import com.example.videoplayer.Models.VideoModel;
import com.example.videoplayer.databinding.ActivityMainBinding;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_CODE_EXTERNAL_STORAGE = 1;

    private ActivityMainBinding binding;
    private ArrayList<VideoModel> videoArrayList;
    private VideoAdapter videoAdapter;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        videoArrayList = new ArrayList<>();

        initRecyclerView();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_CODE_EXTERNAL_STORAGE);
        }
        else
        {
            displayVideos();
        }
    }

    private void initRecyclerView()
    {
        videoAdapter = new VideoAdapter(this, videoArrayList);

        binding.recyclerViewList.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerViewList.setHasFixedSize(true);

        binding.recyclerViewList.setAdapter(videoAdapter);

        videoAdapter.setOnItemClickListener(videoModel ->
        {
            // Intent intent = new Intent(this, VideoPlayerActivity.class);
            Intent intent = new Intent(this, VideoPlayerActivity2.class);
            intent.putExtra("video_uri", videoModel.getVideoUri());

            startActivity(intent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void displayVideos()
    {
        Uri uri;
        Cursor cursor;
        int columnIndexData, thumbnail;

        String absolutePathThumb = null;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        // MediaStore.MediaColumns.DATA, MediaStore.Video.Thumbnails.DATA are deprecated.
        String[] projections = { MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME, MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA };

        final String orderBy = MediaStore.Video.Media.DEFAULT_SORT_ORDER;

        cursor = getContentResolver().query(uri, projections, null, null, orderBy);

        assert cursor != null;
        columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

        thumbnail = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while(cursor.moveToNext())
        {
            absolutePathThumb = cursor.getString(columnIndexData);

            Uri thumbUri = Uri.fromFile(new File(absolutePathThumb));
            String cursorThumb = cursor.getString(thumbnail);

            String fileName = FilenameUtils.getBaseName(absolutePathThumb);
            String extension = FilenameUtils.getExtension(absolutePathThumb);
            String duration = getDuration(absolutePathThumb);

            VideoModel videoModel = new VideoModel();

            videoModel.setVideoDuration(duration);
            videoModel.setVideoName(fileName);
            videoModel.setVideoPath(absolutePathThumb);
            videoModel.setVideoUri(thumbUri.toString());
            videoModel.setVideoThumb(cursorThumb);
            videoModel.setVideoExtension(extension);

            if(duration != null)
            videoModel.setVideoDuration(duration);
            else
            videoModel.setVideoDuration("00:00");

            videoArrayList.add(videoModel);

            videoAdapter.notifyItemInserted(videoArrayList.size()-1);
        }
    }

    private String getDuration(String absolutePathThumb)
    {
        try
        {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(this, Uri.fromFile(new File(absolutePathThumb)));

            String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            assert time != null;
            long timeInMilliSec = Long.parseLong(time);

            mediaMetadataRetriever.release();

            return convertSecondsToHMmSs(timeInMilliSec/1000);
        }
        catch(Exception e)
        {
            e.printStackTrace();

            return null;
        }
    }

    @SuppressLint("DefaultLocale")
    public static String convertSecondsToHMmSs(long seconds)
    {
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == PERMISSION_CODE_EXTERNAL_STORAGE)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    displayVideos();
                }
            }
        }
    }
}