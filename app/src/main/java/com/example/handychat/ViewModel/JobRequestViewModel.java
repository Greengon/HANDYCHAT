package com.example.handychat.ViewModel;

import android.app.Application;

import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.JobRequestRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class JobRequestViewModel extends AndroidViewModel {
    private JobRequestRepository mRepository;
    private JobRequestRepository.JobRequestListData mAllJobRequests;

    public JobRequestViewModel(Application application){
        super(application);
        mRepository = new JobRequestRepository(application);
        mAllJobRequests = mRepository.getmAllJobRequests();
    }

    public MutableLiveData<List<JobRequest>> getmAllJobRequests() {return mAllJobRequests;}

    public void insert(JobRequest jobRequest, JobRequestRepository.AddJobRequestListener listener) {
        mRepository.insert(jobRequest, listener);
    }
}
