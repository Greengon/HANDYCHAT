package com.example.handychat.Fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.handychat.Models.Comment;
import com.example.handychat.Models.CommentRepository;
import com.example.handychat.R;
import com.example.handychat.ViewModel.CommentViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewCommentFragment extends Fragment {
    private static final String COMMENT_ID = "commentID";
    private Comment comment;
    private CommentViewModel commentViewModel;
    private String commentId;

    private ImageView userCreatedImage;
    private TextView userCreatedName;
    private TextView commentDate;
    private TextView commentText;


    public ViewCommentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            commentId = getArguments().getString(COMMENT_ID);
            commentViewModel = ViewModelProviders.of(this,new CommentViewModel(getActivity().getApplication(),commentId)).get(CommentViewModel.class);
            commentViewModel.getComment(commentId, new CommentRepository.GetCommentListener() {
                @Override
                public void onComplete(Comment data) {
                    comment = data;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_comment, container, false);

        // Create reference to all attribute
        userCreatedImage = (ImageView) view.findViewById(R.id.comment_created_user_image);
        userCreatedName = (TextView) view.findViewById(R.id.comment_user_name);
        commentDate = (TextView) view.findViewById(R.id.comment_date);
        commentText = (TextView) view.findViewById(R.id.comment_view_text);

        // Put the data in the attributes
        userCreatedImage.setImageResource(R.drawable.avatar);
        userCreatedName.setText(comment.getUserCreated());
        commentDate.setText(comment.getDate());
        commentText.setText(comment.getComment());
        return view;
    }

}
