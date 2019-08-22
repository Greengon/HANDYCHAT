package com.example.handychat.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.R;
import com.example.handychat.ViewModel.JobRequestViewModel;
import java.util.List;

public class JobRequestList extends Fragment {

    private RecyclerView jobRequestList;
    private RecyclerView.LayoutManager layoutManager;
    private JobRequestListAdapter adapter;
    private JobRequestViewModel viewModel;
    private String query;


    public JobRequestList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d("TAG","JobRequestList onCreate");
        super.onCreate(savedInstanceState);

        /*
        Create a ViewModel the first time the system calls an
        Fragment onCreate() method.
        Re-created fragments receive the viewModel instance created
        by the first fragment.
         */
        viewModel = ViewModelProviders.of(this).get(JobRequestViewModel.class);

        // Create the observer which updates the UI
        final Observer<List<JobRequest>> jobRequestListObserver = jobRequestList ->  {
            // Update UI
            Log.d("TAG","LiveData of job list activated on change");
            adapter.setJobRequests(jobRequestList);
        };

        query = ((MainActivity)getActivity()).getQuery();

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getAllJobRequestsByQuery(query).observe(this, jobRequestListObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("TAG","JobRequestList onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_job_request_list, container, false);

        // Lets create a reference to the recycleView
        jobRequestList = view.findViewById(R.id.job_request_list);

        /*
        use the next setting to improve performance if you know that changes
        in content do not change the layout size of the RecyclerView
         */
        jobRequestList.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getContext());
        jobRequestList.setLayoutManager(layoutManager);

        // Specify an adapter
        adapter = new JobRequestListAdapter(viewModel.getAllJobRequestsByQuery(query).getValue());
        adapter.setOnItemClickListener(position ->  {
            Log.d("TAG","item click: " + position);
            // Lets move the the fragment of the selected request
            Bundle bundle = new Bundle();
            bundle.putString("jobID",viewModel.getAllJobRequestsByQuery(query).getValue().get(position).getId());
            Navigation.findNavController(getView()).navigate(R.id.action_jobRequestList_to_jobRequestView,bundle);
        });

        jobRequestList.setAdapter(adapter);

        return view;
    }
}
