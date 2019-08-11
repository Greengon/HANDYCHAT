package com.example.handychat.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handychat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobRequestView extends Fragment {

    // The fragment initialization parameters
    private static final String JOB_ID = "jobID";

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_job_request_view, container, false);
    }

}
