package com.example.handychat.Fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Calendar;
import java.util.UUID;

public class NewCommentFragment extends Fragment {

    private static final String JOB_ID = "jobID";
    private String jobId;
    private View view;
    private EditText commentEditText;
    private Button saveBtn;
    private ProgressBar progressBar;
    private PackageManager packageManager;
    private CommentViewModel mCommentViewModel;


    public NewCommentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewCommentFragment.
     */
    public static NewCommentFragment newInstance(String jobID) {
        NewCommentFragment fragment = new NewCommentFragment();
        Bundle args = new Bundle();
        args.putString(JOB_ID,jobID);
        fragment.setArguments(args);
        return fragment;
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
        commentEditText = (EditText) view.findViewById(R.id.newFragmentEditTextDescription);
        packageManager = view.getContext().getPackageManager();
        saveBtn = (Button) view.findViewById(R.id.newCommentSaveBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.new_comment_pb);
        progressBar.setVisibility(View.INVISIBLE);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        return view;
    }

    private void save() {
        // Active progress bar
        progressBar.setVisibility(View.VISIBLE);


        // Lets get the current user for his email
        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();

        // Lets create our new comment object
        Comment comment = new Comment(UUID.randomUUID().toString(),jobId
                ,user.getEmail()
                , Calendar.getInstance().getTime().toString(),
                commentEditText.getText().toString());
        // Now let's save it to remote firebase and locally
        mCommentViewModel.insert(comment,success -> {
            /*
             We will continue when the comment was saved
             At least locally
              */

            // Close fragment and return to job view
            ((MainActivity)getActivity()).getNavController().popBackStack();
        });
    }
}
