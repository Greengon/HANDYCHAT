package com.example.handychat.Fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.handychat.Models.Comment;
import com.example.handychat.R;

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

        public CommentListViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onClick(position);
                        }
                    }
                }
            });

            userImage = (ImageView) itemView.findViewById(R.id.commentUserImageInRow);
            userName = (TextView) itemView.findViewById(R.id.commentNameTextViewInRow);
            commentText = (TextView) itemView.findViewById(R.id.commentTextView);
            commentProgressBar = (ProgressBar) itemView.findViewById(R.id.commentImageProgressBar);
        }

        public void bind(Comment comment) {
            userName.setText("User name needs to come here");
            commentText.setText(comment.comment);
            userImage.setImageResource(R.drawable.avatar);
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
