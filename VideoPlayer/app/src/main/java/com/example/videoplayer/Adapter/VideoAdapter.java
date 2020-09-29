package com.example.videoplayer.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.videoplayer.Models.VideoModel;
import com.example.videoplayer.R;
import com.example.videoplayer.databinding.MyRowBinding;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder>
{
    private static final String TAG = "VideoAdapter";
    private ArrayList<VideoModel> videoArrayList;
    private Context context;
    private OnItemClickListener listener;

    public VideoAdapter(Context context, ArrayList<VideoModel> videoArrayList)
    {
        this.context = context;
        this.videoArrayList = videoArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new MyViewHolder(MyRowBinding.inflate(LayoutInflater.from(context), parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position)
    {
        // Log.d(TAG, "onBindViewHolder:  " + videoArrayList.get(position).getVideoThumb());

        Glide.with(context)
            .load(videoArrayList.get(position).getVideoThumb())
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.myRowBinding.vidImage);

        holder.myRowBinding.vidName.setText(videoArrayList.get(position).getVideoName() + "." + videoArrayList.get(position).getVideoExtension());
        holder.myRowBinding.vidDuration.setText(videoArrayList.get(position).getVideoDuration());
    }

    @Override
    public int getItemCount() {
        return videoArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        private MyRowBinding myRowBinding;

        public MyViewHolder(@NonNull MyRowBinding myRowBinding)
        {
            super(myRowBinding.getRoot());

            this.myRowBinding = myRowBinding;

            itemView.setOnClickListener(view ->
            {
                int position = getAdapterPosition();

                if(listener != null && position != RecyclerView.NO_POSITION)
                {
                    listener.onItemClick(videoArrayList.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener
    {
        void onItemClick(VideoModel videoModel);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.listener = listener;
    }
}