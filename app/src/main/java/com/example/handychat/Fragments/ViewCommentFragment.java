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
import android.widget.Toast;
import com.example.handychat.Models.Comment;
import com.example.handychat.R;
import com.example.handychat.ViewModel.CommentViewModel;

public class ViewCommentFragment extends Fragment {
    private static final String COMMENT_ID = "commentID";
    private Comment mComment;
    private CommentViewModel commentViewModel;
    private String commentId;

    // Fragment view component
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

            // Let's get the comment we are showing
            commentViewModel.getComment(commentId, comment -> {
                    mComment = comment;
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_comment, container, false);

        // Create reference to all attribute
        userCreatedImage = view.findViewById(R.id.comment_created_user_image);
        userCreatedName = view.findViewById(R.id.comment_user_name);
        commentDate = view.findViewById(R.id.comment_date);
        commentText = view.findViewById(R.id.comment_view_text);

        // Put the data in the attributes
        if (mComment != null){
            // TODO: create the code to load user image here
            userCreatedImage.setImageResource(R.drawable.avatar);
            userCreatedName.setText(mComment.getUserCreated());
            commentDate.setText(mComment.getDate());
            commentText.setText(mComment.getComment());
        } else {
            Toast.makeText(getContext(),"Problem loading the comment",Toast.LENGTH_SHORT).show();
        }
        return view;
    }
}
