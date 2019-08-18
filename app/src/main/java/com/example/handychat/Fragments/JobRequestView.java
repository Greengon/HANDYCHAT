package com.example.handychat.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.handychat.Models.Comment;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.Model;
import com.example.handychat.R;
import com.example.handychat.ViewModel.CommentViewModel;
import com.example.handychat.ViewModel.JobRequestViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.List;

public class JobRequestView extends Fragment {
    // Test

    // The fragment initialization parameters
    private static final String JOB_ID = "jobID";
    private JobRequestViewModel jobRequestViewModel;
    private CommentViewModel commentViewModel;
    private CommentListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    // The fragment main variables
    public String jobId;
    private JobRequest mJobRequest;

    // Fragment view variables
    private ImageView jobImage;
    private ImageView userImage;
    private ImageButton deleteJob;
    private ImageButton editJob;
    private TextView date;
    private TextView address;
    private TextView description;
    private RecyclerView commentList;
    private ProgressBar progressBar;
    private Button addCommentBtn;
    private Target target;


    public JobRequestView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Job request preparing *******/
        jobRequestViewModel = ViewModelProviders.of(this).get(JobRequestViewModel.class);

        // Get a copy of the job request that's been viewed by the id given from the list
        if (getArguments() != null){
            jobId = getArguments().getString(JOB_ID);
            jobRequestViewModel.getJobRequest(jobId, jobRequest -> {
                mJobRequest = jobRequest;
            });
        }
        /******* Job request preparing *******/

        /******* Comment list preparing *******/
        commentViewModel = ViewModelProviders.of(this,new CommentViewModel(getActivity().getApplication(),jobId)).get(CommentViewModel.class);

        // Create the observer which updates the UI
        final Observer<List<Comment>> commentListObserver = commentList -> {
            // update UI
            adapter.setComments(commentList);
        };

        // Observe the LiveData, pass this fragment as the LifecycleOwner and the observer
        commentViewModel.getmCommentList().observe(this,commentListObserver);
        /******* Comment list preparing *******/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_request_view, container, false);

        if (getArguments() != null){
            /************ Job request Section ***************/
            // Lets create reference to all view objects
            jobImage = view.findViewById(R.id.job_view_image);
            userImage = view.findViewById(R.id.job_view_user_image);
            date = view.findViewById(R.id.job_view_date_value);
            address = view.findViewById(R.id.job_view_address_value);
            description = view.findViewById(R.id.job_view_description_value);

            if (mJobRequest.getImageUrl() != null){
                Picasso.get().setIndicatorsEnabled(true);
                Model.loadImage(mJobRequest.getImageUrl(), imageFile -> {
                    target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            jobImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    };
                    Picasso.get().load(imageFile).into(target);
                });
            }
            userImage.setImageResource(R.drawable.avatar);
            date.setText(mJobRequest.date);
            address.setText("Address: " + mJobRequest.address);
            description.setText(mJobRequest.description);

            /************ Comments Section ***************/

            // Lets create a reference to the recycleView
            commentList = view.findViewById(R.id.job_request_view_recyclerview);

            /*
            use the next setting to improve performance if you know that changes
            in content do not change the layout size of the RecyclerView
             */
            commentList.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(getContext());
            commentList.setLayoutManager(layoutManager);

            // Specify an adapter
            adapter = new CommentListAdapter(commentViewModel.getmCommentList().getValue());
            //TODO: Create delete comment option, not a must...
            adapter.setOnItemClickListener(position ->  {
                Bundle bundle = new Bundle();
                bundle.putString("commentID",commentViewModel.getmCommentList().getValue().get(position).getId());
                Navigation.findNavController(getView()).navigate(R.id.action_jobRequestView_to_viewCommentFragment,bundle);
            });

            commentList.setAdapter(adapter);
            /************ Comments Section ***************/

            /************ Buttons Section ***************/
            progressBar = view.findViewById(R.id.job_request_view_pb);
            progressBar.setVisibility(View.INVISIBLE);

            addCommentBtn = view.findViewById(R.id.add_comment_button);
            addCommentBtn.setOnClickListener(viewObject -> {
                Bundle bundle = new Bundle();
                bundle.putString("jobID",jobId);
                Navigation.findNavController(getView()).navigate(R.id.action_jobRequestView_to_newCommentFragment,bundle);
            });

            deleteJob = view.findViewById(R.id.delete_job_request);
            deleteJob.setOnClickListener(viewObject -> {
                if (hasPermission()){
                    progressBar.setVisibility(View.VISIBLE);
                    jobRequestViewModel.delete(jobId, () -> {
                        Navigation.findNavController(getView()).popBackStack();
                    });
                }else{
                    Toast.makeText(getContext(),"Only the created user can do that.",Toast.LENGTH_SHORT).show();
                }
            });

            editJob = view.findViewById(R.id.edit_job_request);
            editJob.setOnClickListener(viewObject -> {
                if (hasPermission()){
                    Bundle bundle = new Bundle();
                    bundle.putString("jobID",jobId);
                    Navigation.findNavController(getView()).navigate(R.id.action_jobRequestView_to_editJobRequestFragment,bundle);
                }else{
                    Toast.makeText(getContext(),"Only the created user can do that.",Toast.LENGTH_SHORT).show();
                }
            });
            /************ Buttons Section ***************/

        }
        return view;
    }

    private boolean hasPermission() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getEmail().equals(mJobRequest.getUserCreated())){
            return true;
        }
        return false;
    }

}
