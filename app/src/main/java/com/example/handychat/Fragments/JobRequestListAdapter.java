package com.example.handychat.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class JobRequestListAdapter extends RecyclerView.Adapter<JobRequestListAdapter.jobRequestViewHolder> {
    List<JobRequest> mData;
    private OnItemClickListener mListener;

    // Constructor to the adapter that receive the list of objects to display
    public JobRequestListAdapter(List<JobRequest> data){
        mData = data;
    }

    // Setters
    void setJobRequests(List<JobRequest> data){
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public jobRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.job_request_row,parent,false);
        jobRequestViewHolder viewHolder = new jobRequestViewHolder(view, mListener);
        return viewHolder;
    }

    /*
    This method set the view holder properties according to the
    object it displays
     */
    @Override
    public void onBindViewHolder(@NonNull jobRequestViewHolder holder, int position) {
        JobRequest jobRequest = mData.get(position);
        holder.bind(jobRequest);
    }

    // Returns the number of objects to display in the list
    @Override
    public int getItemCount() {
        if (mData == null){
            return 0;
        }
        return mData.size();
    }

    static class jobRequestViewHolder extends RecyclerView.ViewHolder{
        // Reference to the views for each data item
        ImageView jobImage;
        ImageView userImage;
        TextView dateText;
        TextView descriptionText;
        ProgressBar jobImageProgressBar;
        Target target;

        public jobRequestViewHolder(@NonNull View itemView,final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onClick(position);
                    }
                }
            });
            jobImage = itemView.findViewById(R.id.jobImageInRow);
            userImage = itemView.findViewById(R.id.userImageInRow);
            dateText = itemView.findViewById(R.id.dateTextViewInRow);
            descriptionText = itemView.findViewById(R.id.descriptionTextViewInRow);
            jobImageProgressBar = itemView.findViewById(R.id.jobImageProgressBar);
        }

        // Binding all attributes to the view
        public void bind(final JobRequest jobRequest) {
            dateText.setText(jobRequest.date);
            descriptionText.setText(jobRequest.description);

            /****** Get job request image ********/
            if (jobRequest.getImageUrl() != null){
                Picasso.get().setIndicatorsEnabled(true);
                target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        jobImage.setImageBitmap(bitmap);
                        jobImageProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        jobImageProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        jobImageProgressBar.setVisibility(View.VISIBLE);
                    }
                };
                // Step 1: Try to look for the image locally
                File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                String name = URLUtil.guessFileName(jobRequest.getImageUrl(),null,null);
                String path = folder.getAbsolutePath() + "/" + name;
                File localImage = new File(path);
                if (localImage.exists()){ // Check if we can find our image in the local storage
                    Picasso.get().load(localImage).into(target);
                } else {
                    // Step 2: Try to look for the image on FireBase
                    Picasso.get().load(jobRequest.getImageUrl()).into(target);
                }
            }else{
                jobImageProgressBar.setVisibility(View.INVISIBLE);
            }
            /****** Get job request image ********/

            /****** Get user image ********/
            // TODO: Here we should load the user image, next line is only temporary
            userImage.setImageResource(R.drawable.avatar);
            /****** Get user image ********/
        }
    }

    /************** On Item Click ***************/

    interface OnItemClickListener{
        void onClick(int position);
    }

    void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    /************** On Item Click ***************/
}
