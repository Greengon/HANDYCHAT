package com.example.handychat.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.handychat.Models.Comment;
import com.example.handychat.Models.Model;
import com.example.handychat.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.CommentListViewHolder> {
    List<Comment> mData;
    private OnItemClickListener mListener;

    // Constructor to the adapter that receive the list of objects to display
    public CommentListAdapter(List<Comment> data) {
        mData = data;
    }

    public void setComments(List<Comment> data){
        mData = data;
        notifyDataSetChanged();
    }

    static class CommentListViewHolder extends RecyclerView.ViewHolder {
        // Reference to the views for each data item
        private ImageView userImage;
        private TextView userName;
        private TextView commentText;
        private ProgressBar commentProgressBar;
        private Target target;

        public CommentListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(view -> {
                if (listener != null){
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.onClick(position);
                    }
                }
            });

            userImage = itemView.findViewById(R.id.commentUserImageInRow);
            userName = itemView.findViewById(R.id.commentNameTextViewInRow);
            commentText = itemView.findViewById(R.id.commentTextView);
            commentProgressBar = itemView.findViewById(R.id.commentImageProgressBar);
        }

        public void bind(Comment comment) {
            userName.setText(comment.getUserCreated());
            commentText.setText(comment.comment);
            /****** Get user image ********/
            // Image
            if (comment.getUserImage() == null || comment.getUserImage().isEmpty()) {
                userImage.setImageResource(R.drawable.avatar);
            } else {
                Picasso.get().setIndicatorsEnabled(true);
                Model.loadImage(comment.getUserImage(), imageFile -> {
                    // We will prepare a Target object for Picasso
                    target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            userImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    };
                    if (imageFile.exists()) {
                        // Load the image with Picasso from local file
                        Picasso.get().load(imageFile).into(target);
                    } else {
                        /*
                         TODO: This isn't a good way of working, but it solve some async problems
                         it helps when the app wasn't fast enough to download the  image.
                         If there's time we should figure out a way to remove the next line and
                         solve the problem.
                          */
                        // Try load the image with Picasso from remote
                        Picasso.get().load(comment.getUserImage()).into(target);
                    }
                });
                /****** Get user image ********/
            }
            commentProgressBar.setVisibility(View.INVISIBLE);
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

    @NonNull
    @Override
    public CommentListAdapter.CommentListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_row,parent,false);
        CommentListViewHolder viewHolder = new CommentListViewHolder(view, mListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentListAdapter.CommentListViewHolder holder, int position) {
        Comment comment = mData.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        if (mData == null){
            return 0;
        }
        return mData.size();
    }
}
