package com.example.handychat.ViewModel;


import android.app.Application;

import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.JobRequestRepository;
import com.example.handychat.Models.ModelFirebase;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class JobRequestViewModel extends AndroidViewModel {
    private JobRequestRepository mRepository;
    private MutableLiveData<List<JobRequest>> mAllJobRequests;

    public JobRequestViewModel(Application application){
        super(application);
        mRepository = new JobRequestRepository(application);
    }


    public MutableLiveData<List<JobRequest>> getmAllJobRequests() {
        if (mAllJobRequests == null){
            mAllJobRequests = new MutableLiveData<>();
        }
        mRepository.getAllJobRequests(new JobRequestRepository.GetAllJobRequestsListener() {
            @Override
            public void onComplete(List<JobRequest> data) {
                if (data != null){
                    mAllJobRequests.postValue(data);
                }
            }
        });
        return mAllJobRequests;
    }

    public void getJobRequest(String id, JobRequestRepository.GetJobRequestsListener listener){
        mRepository.getJobRequest(id,listener);
    }

    public void insert(JobRequest jobRequest, JobRequestRepository.AddJobRequestListener listener) {
        mRepository.insert(jobRequest, listener);
    }

    public void delete(String jobId, JobRequestRepository.JobDeletedListener listener) {
        mRepository.delete(jobId, listener);
    }

    public void update(JobRequest jobRequest, ModelFirebase.UpdateJobRequestListener listener) {
        mRepository.update(jobRequest,listener);
    }
}

