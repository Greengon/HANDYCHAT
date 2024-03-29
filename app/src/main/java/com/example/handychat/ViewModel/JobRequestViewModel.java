package com.example.handychat.ViewModel;

import android.app.Application;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.JobRequestRepository;
import java.util.LinkedList;
import java.util.List;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class JobRequestViewModel extends AndroidViewModel {
    private JobRequestRepository mRepository;
    private MutableLiveData<List<JobRequest>> mAllJobRequests;
    private MutableLiveData<JobRequest> mJobRequest;

    public JobRequestViewModel(Application application){
        super(application);
        mRepository = new JobRequestRepository(application);
    }

    public MutableLiveData<List<JobRequest>> getAllJobRequestsByQuery(String query) {
        if (mAllJobRequests == null){
            mAllJobRequests = new MutableLiveData<>();
        }
        mRepository.getAllJobRequests(data ->  {
            if (data != null){
                // Two option for this listener
                if (query == null || query.isEmpty()) {
                    // One: there is a null/empty query which mean we want all jobs
                    mAllJobRequests.postValue(data);
                } else if(query.startsWith("HANDYMAN|")){
                    // Get the value needed
                    String category = query.split("\\|")[1];
                    String area = query.split("\\|")[2];
                    String email = query.split("\\|")[3];

                    // Create the list from data
                    List<JobRequest> list = new LinkedList<>();
                    for (JobRequest jobRequest: data){
                        if ((jobRequest.getArea().equals(area) && jobRequest.getCategory().equals(category)) || jobRequest.getUserCreated().equals(email)
                        ) {
                            list.add(jobRequest);
                        }
                    }
                    mAllJobRequests.postValue(list);
                } else {
                    // There was a query and we want to look for something
                    List<JobRequest> list = new LinkedList<>();
                    for (JobRequest jobRequest: data){
                        if ( // Look for given text in the job request
                                jobRequest.getDate().contains(query) ||
                                jobRequest.getUserCreated().contains(query) ||
                                jobRequest.getAddress().contains(query) ||
                                jobRequest.getDescription().contains(query)
                        ){
                            list.add(jobRequest);
                        }
                    }
                    mAllJobRequests.postValue(list);
                }
            }
        });
        return mAllJobRequests;
    }

    public MutableLiveData<JobRequest> getJobRequest(String id){
        if (mJobRequest == null){
            mJobRequest = new MutableLiveData<>();
        }

        mRepository.getJobRequest(id,data -> {
            if (data != null){
                mJobRequest.postValue(data);
            }
        });
        return mJobRequest;
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

