package com.example.handychat.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.Comment;
import com.example.handychat.R;
import com.example.handychat.ViewModel.CommentViewModel;
import java.util.Calendar;
import java.util.UUID;

public class NewCommentFragment extends Fragment {

    private static final String JOB_ID = "jobID";
    private String jobId;
    private CommentViewModel mCommentViewModel;

    // View components
    private View view;
    private EditText commentEditText;
    private Button saveBtn;
    private ProgressBar progressBar;


    public NewCommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!= null){
            jobId = getArguments().getString(JOB_ID);
        }
        mCommentViewModel = ViewModelProviders.of(this,new CommentViewModel(getActivity().getApplication(),jobId)).get(CommentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_comment, container, false);

        // Get reference to all the view components
        commentEditText =  view.findViewById(R.id.newFragmentEditTextDescription);
        progressBar = view.findViewById(R.id.new_comment_pb);
        progressBar.setVisibility(View.INVISIBLE);
        saveBtn = view.findViewById(R.id.newCommentSaveBtn);
        saveBtn.setOnClickListener(viewObject -> {
            // Active progress bar
            progressBar.setVisibility(View.VISIBLE);


            // Lets create our new comment object
            Comment comment = new Comment(
                    UUID.randomUUID().toString(),
                    jobId,
                    ((MainActivity)getActivity()).getUser().getEmail(),
                    ((MainActivity)getActivity()).getUser().getImage(),
                    Calendar.getInstance().getTime().toString(),
                    commentEditText.getText().toString()
            );

            // Now let's save it to remote FireBase and locally
            mCommentViewModel.insert(comment,success -> {
                /*
                 We will continue when the comment was saved
                 At least locally
                  */
                // Close fragment and return to job view
                Navigation.findNavController(getView()).popBackStack();
            });
        });
        return view;
    }
}
