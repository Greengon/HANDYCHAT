package com.example.handychat.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.Model;
import com.example.handychat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobRequestView extends Fragment {

    // The fragment initialization parameters
    private static final String JOB_ID = "jobID";

    // The fragment main variables
    private String jobId;
    private JobRequest mJobRequest;
    private ImageView jobImage;
    private ImageView userImage;
    private TextView date;
    private TextView address;
    private TextView description;


    public JobRequestView() {
        // Required empty public constructor
    }

    /*
    Use this factory method to create a new instance of
    this fragment using the provided parameters
     */

    public static JobRequestView newInstance(String jobID) {

        JobRequestView fragment = new JobRequestView();

        Bundle args = new Bundle();

        args.putString(JOB_ID, jobID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            jobId = getArguments().getString(JOB_ID);
            Model.instance.getJobRequest(jobId, new Model.GetJobListener() {
                @Override
                public void onComplete(JobRequest jobRequest) {
                    mJobRequest = jobRequest;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_request_view, container, false);

        Bundle myBundle = this.getArguments();
        jobId = getArguments().getString(JOB_ID);
        Model.instance.getJobRequest(jobId, new Model.GetJobListener() {
            @Override
            public void onComplete(JobRequest jobRequest) {
                mJobRequest = jobRequest;
            }
        });

        if (myBundle != null){
            // Lets create reference to all view objects
            jobImage = (ImageView) view.findViewById(R.id.job_view_image);
            userImage = (ImageView) view.findViewById(R.id.job_view_user_image);
            date = (TextView) view.findViewById(R.id.job_view_date_value);
            address = (TextView) view.findViewById(R.id.job_view_address_value);
            description = (TextView) view.findViewById(R.id.job_view_description_value);

            userImage.setImageResource(R.drawable.avatar);
            date.setText(mJobRequest.date);
            address.setText(mJobRequest.address);
            description.setText(mJobRequest.description);

            if (mJobRequest.getImageUrl() != null && mJobRequest.getImageUrl().isEmpty() && mJobRequest.getImageUrl() != ""){
                Model.instance.loadImage(mJobRequest.getImageUrl(), new Model.GetImageListener() {
                    @Override
                    public void onSuccess(Bitmap image) {
                        jobImage.setImageBitmap(image);
                    }

                    @Override
                    public void onFail() {
                        Log.d("TAG","failed to get image");
                    }
                });
            }
        }
        return view;
    }
}
