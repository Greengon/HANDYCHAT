package com.example.handychat.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.Model;
import com.example.handychat.R;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class JobRequestList extends Fragment {


    private RecyclerView jobRequestList;
    private RecyclerView.LayoutManager layoutManager;
    private jobRequestListAdapter adapter;
    private List<JobRequest> mData = new LinkedList<>();
    ListJobsListener listJobsListener;


    public JobRequestList() {
        // Required empty public constructor
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

        // Specify an adapter
        adapter = new jobRequestListAdapter(mData);
        adapter.setOnItemClickListener(new jobRequestListAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Log.d("TAG","item click: " + position);
                // Lets move the the fragment of the selected request
                listJobsListener.onJobRequestSelected(mData.get(position).getId());
            }
        });
        jobRequestList.setAdapter(adapter);

        Model.instance.getAllJobRequest(new Model.GetAllJobRequestListener() {
            @Override
            public void onComplete(List<JobRequest> data) {
                mData = data;
                adapter.mData = data;
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        /*
         Lets connect to our main activity so
         we could handle user pressing on job
         in the view
          */
        if (context instanceof ListJobsListener){
            listJobsListener = (ListJobsListener) context;
        }
    }

    public interface ListJobsListener{
        void onJobRequestSelected(String jobId);
    }

    public static JobRequestList newInstance() {
        JobRequestList fragment = new JobRequestList();
        return fragment;
    }

}
