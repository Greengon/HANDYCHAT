package com.example.handychat.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.Comment;
import com.example.handychat.Models.CommentRepository;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.JobRequestRepository;
import com.example.handychat.Models.Model;
import com.example.handychat.MyApplication;
import com.example.handychat.R;
import com.example.handychat.ViewModel.CommentViewModel;
import com.example.handychat.ViewModel.JobRequestViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobRequestView extends Fragment {

    // The fragment initialization parameters
    private static final String JOB_ID = "jobID";
    private JobRequestViewModel jobRequestViewModel;
    private CommentViewModel commentViewModel;
    private CommentListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private NavController navController;
    private ListCommentListener listCommentListener;
    private UserPressedListener userPressedListener;

    // The fragment main variables
    public String jobId;
    private List<Comment> mCommentList;
    private JobRequest mJobRequest;
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
        jobRequestViewModel = ViewModelProviders.of(this).get(JobRequestViewModel.class);
        if (getArguments() != null){
            jobId = getArguments().getString(JOB_ID);
            jobRequestViewModel.getJobRequest(jobId, new JobRequestRepository.GetJobRequestsListener() {
                @Override
                public void onComplete(JobRequest data) {
                    mJobRequest = data;
                }
            });
        }
        commentViewModel = ViewModelProviders.of(this,new CommentViewModel(getActivity().getApplication(),jobId)).get(CommentViewModel.class);

        // Create the observer which updates the UI
        final Observer<List<Comment>> commentListObserver = new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> commentList) {
                // update UI
                mCommentList = commentList;
                adapter.setComments(commentList);
            }
        };

        // Observe the LiveData, pass this fragment as the LifecycleOwner and the observer
        commentViewModel.getmCommentList().observe(this,commentListObserver);

        navController = ((MainActivity)getActivity()).getNavController();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_request_view, container, false);

        Bundle myBundle = this.getArguments();
        jobId = getArguments().getString(JOB_ID);

        jobRequestViewModel.getJobRequest(jobId, new JobRequestRepository.GetJobRequestsListener() {
            @Override
            public void onComplete(JobRequest data) {
                mJobRequest = data;
            }
        });


        if (myBundle != null){
            /************ Job request Section ***************/
            // Lets create reference to all view objects
            jobImage = (ImageView) view.findViewById(R.id.job_view_image);
            userImage = (ImageView) view.findViewById(R.id.job_view_user_image);
            date = (TextView) view.findViewById(R.id.job_view_date_value);
            address = (TextView) view.findViewById(R.id.job_view_address_value);
            description = (TextView) view.findViewById(R.id.job_view_description_value);



            createView();
            /************ Comments Section ***************/

            // Lets create a reference to the recycleView
            commentList = (RecyclerView) view.findViewById(R.id.job_request_view_recyclerview);

            /*
            use the next setting to improve performance if you know that changes
            in content do not change the layout size of the RecyclerView
             */
            commentList.setHasFixedSize(true);

            // use a linear layout manager
            layoutManager = new LinearLayoutManager(getContext());
            commentList.setLayoutManager(layoutManager);

            // Specify an adapter
            adapter = new CommentListAdapter(mCommentList);
            //TODO: Create delete comment option, not a must...
            adapter.setOnItemClickListener(new CommentListAdapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {
                    Bundle bundle = new Bundle();
                    bundle.putString("commentID",mCommentList.get(position).getId());
                    navController.navigate(R.id.action_jobRequestView_to_viewCommentFragment,bundle);
                }
            });

            commentList.setAdapter(adapter);
            /************ Comments Section ***************/

            /************ Buttons Section ***************/
            progressBar = (ProgressBar) view.findViewById(R.id.job_request_view_pb);
            progressBar.setVisibility(View.INVISIBLE);
            addCommentBtn = (Button) view.findViewById(R.id.add_comment_button);
            addCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("jobID",jobId);
                    navController.navigate(R.id.action_jobRequestView_to_newCommentFragment,bundle);
                }
            });

            deleteJob = (ImageButton) view.findViewById(R.id.delete_job_request);
            deleteJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasPermission()){
                        progressBar.setVisibility(View.VISIBLE);
                        jobRequestViewModel.delete(jobId, new JobRequestRepository.JobDeletedListener() {
                            @Override
                            public void onComplete() {
                                navController.popBackStack();
                            }
                        });
                    }else{
                        Toast.makeText(getContext(),"Only the created user can do that.",Toast.LENGTH_SHORT).show();
                    }
                }
            });


            editJob = (ImageButton) view.findViewById(R.id.edit_job_request);
            editJob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasPermission()){
                        progressBar.setVisibility(View.VISIBLE);
                        Bundle bundle = new Bundle();
                        bundle.putString("jobID",jobId);
                        navController.navigate(R.id.action_jobRequestView_to_editJobRequestFragment,bundle);
                    }else{
                        Toast.makeText(getContext(),"Only the created user can do that.",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            /************ Buttons Section ***************/

        }
        return view;
    }

    private void exitView() {
    }

    private void createView() {
        /****** Get job request image ********/
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

        /************ Job request Section ***************/

    }

    private boolean hasPermission() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user.getEmail().equals(mJobRequest.getUserCreated())){
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG","JobRequestView onResume");
        jobRequestViewModel.getJobRequest(jobId, new JobRequestRepository.GetJobRequestsListener() {
            @Override
            public void onComplete(JobRequest data) {
                mJobRequest = data;
            }
        });

        commentViewModel.getmCommentList();

        createView();
    }

    public interface ListCommentListener{
        void OnCommentSelected(String id);
    }

    public interface UserPressedListener{
        void OnPressed(String id);
    }

}
