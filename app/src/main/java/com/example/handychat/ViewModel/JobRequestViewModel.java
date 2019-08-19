package com.example.handychat.ViewModel;

import android.app.Application;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.JobRequestRepository;
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

    public MutableLiveData<List<JobRequest>> getAllJobRequests() {
        if (mAllJobRequests == null){
            mAllJobRequests = new MutableLiveData<>();
        }
        mRepository.getAllJobRequests(data ->  {
            if (data != null){
                mAllJobRequests.postValue(data);
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

    public void delete(String jobId) {
        mRepository.delete(jobId);
    }

    public void update(JobRequest jobRequest, JobRequestRepository.UpdateJobRequestListener listener) {
        mRepository.update(jobRequest,listener);
    }

}

