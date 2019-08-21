package com.example.handychat.Fragments;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.Comment;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.R;
import com.example.handychat.ViewModel.CommentViewModel;
import com.example.handychat.ViewModel.JobRequestViewModel;
import com.squareup.picasso.Target;

import java.util.Calendar;

public class EditCommentFragment extends Fragment {
    private static final String Comment_ID = "commentID";
    private Comment mComment;
    private CommentViewModel commentViewModel;
    public String commentId;

    // View components
    private EditText commentEditText;
    private Button saveBtn;
    private ProgressBar progressBar;

    public EditCommentFragment(){};
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            commentId = getArguments().getString(Comment_ID);
            commentViewModel = ViewModelProviders.of(this,new CommentViewModel(getActivity().getApplication(),commentId)).get(CommentViewModel.class);
            // Create the observer which updates the UI
            final Observer<Comment> commentObserver = comment -> {
                mComment = comment;
                commentEditText.setText(mComment.getComment());
            };

            // Observe the LiveData, pass this fragment as the LifecycleOwner and the observer
            commentViewModel.getComment(commentId).observe(this,commentObserver);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.edit_comment_fragment, container, false);
        // Get reference to all the view components
        commentEditText =  view.findViewById(R.id.newFragmentEditTextDescription);
        progressBar = view.findViewById(R.id.new_comment_pb);
        progressBar.setVisibility(View.INVISIBLE);

        saveBtn = view.findViewById(R.id.newCommentSaveBtn);
        saveBtn.setOnClickListener(viewObject -> {
            // Active progress bar
            progressBar.setVisibility(View.VISIBLE);
            save();
        });
        return view;
    }

    private void save() {
        // Lets create our new jobRequest object
        Comment newComment = new Comment(
                mComment.getId(),
                mComment.getJobRequestId(),
                mComment.getUserCreated(),
                mComment.getUserImage(),
                Calendar.getInstance().getTime().toString(),
                commentEditText.getText().toString()
        );

        // Now let's save it to remote FireBase and locally
        commentViewModel.update(newComment, () -> {
            // Stop progress bar
            progressBar.setVisibility(View.INVISIBLE);

            // Close fragment and navigate back to list
            Navigation.findNavController(getView()).popBackStack(R.id.viewCommentFragment,true);
        });
    }

}
