package com.example.handychat.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.Model;
import com.example.handychat.MyApplication;
import com.example.handychat.R;
import com.example.handychat.ViewModel.JobRequestViewModel;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobRequestList extends Fragment {


    private RecyclerView jobRequestList;
    private RecyclerView.LayoutManager layoutManager;
    private jobRequestListAdapter adapter;
    private JobRequestViewModel viewModel;
    List<JobRequest> mJobRequestList;
    String userId;


    public JobRequestList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        userId = getArguments().getString(UID_KEY);

        /*
        Create a ViewModel the first time the system calls an
        Fragment onCreate() method.
        Re-created fragments recevie the viewModel instance created
        by the first fragment.
         */
        viewModel = ViewModelProviders.of(this).get(JobRequestViewModel.class);

        // Create the observer which updates the UI
        final Observer<List<JobRequest>> jobRequestListObserver = new Observer<List<JobRequest>>() {
            @Override
            public void onChanged(List<JobRequest> jobRequestList) {
                // update UI
                mJobRequestList = jobRequestList;
                adapter.setJobRequests(jobRequestList);
        }
        };

        viewModel.getmAllJobRequests().observe(this, jobRequestListObserver);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_request_list, container, false);

        // Lets create a reference to the recycleView
        jobRequestList = (RecyclerView) view.findViewById(R.id.job_request_list);

        /*
        use the next setting to improve performance if you know that changes
        in content do not change the layout size of the RecyclerView
         */
        jobRequestList.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        jobRequestList.setLayoutManager(layoutManager);

        // Create first instance of all jobRequest
        mJobRequestList = viewModel.getmAllJobRequests().getValue();

        // Specify an adapter
        adapter = new jobRequestListAdapter(mJobRequestList);
        adapter.setOnItemClickListener(new jobRequestListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Log.d("TAG","item click: " + position);
                // Lets move the the fragment of the selected request
                Bundle bundle = new Bundle();
                bundle.putString("jobID",mJobRequestList.get(position).getId());
                ((MainActivity)getActivity()).getNavController().navigate(R.id.action_jobRequestList_to_jobRequestView,bundle);
            }
        });

        jobRequestList.setAdapter(adapter);

        return view;
    }


    public static JobRequestList newInstance() {
        JobRequestList fragment = new JobRequestList();
        return fragment;
    }

}
