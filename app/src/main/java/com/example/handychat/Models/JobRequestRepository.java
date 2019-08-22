package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;
import java.util.List;

public class JobRequestRepository {
    static ModelFirebase modelFirebase;

    public JobRequestRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        modelFirebase = new ModelFirebase();
    }

    /******************* Update ***********************/
    public interface UpdateJobRequestListener {
        public void onComplete();
    }

    public void update(JobRequest jobRequest, UpdateJobRequestListener listener) {
        modelFirebase.UpdateJobRequest(jobRequest,listener);
        JobRequestAsyncDao.updateJob(jobRequest);
    }
    /******************* Update ***********************/

    /******************* Delete ***********************/
    public interface JobDeletedListener{
        void onComplete();
    }

    public void delete(String jobId) {
        // Step 1+2: Delete the remote and local comments for the job
        CommentRepository.deleteAllCommentForJob(jobId);

        // Step 3: Delete the job in the remote Database
        modelFirebase.deleteJob(jobId,()->{
            // Step 4: Delete the job in the local Database
            JobRequestAsyncDao.deleteJob(jobId);
        });
    }
    /******************* Delete ***********************/

    /******************* Insert ***********************/
    public interface AddJobRequestListener{
        void onComplete();
    }

    public void insert(JobRequest jobRequest, AddJobRequestListener listener){
        JobRequestAsyncDao.insertJob(jobRequest);
        modelFirebase.addJobRequest(jobRequest,listener);
    }
    /******************* Insert ***********************/

    /******************* Get all ***********************/
    public interface GetAllJobRequestsListener{
        void onComplete(List<JobRequest> data);
    }

    public void getAllJobRequests(final GetAllJobRequestsListener listener){
        /*
        Here we won't use local data get all jobRequest that to avoid Async issues
        If we want our app to work with out internet, an option for loading form
        local db should be here.
          */
        modelFirebase.getAllJobRequest(jobRequestList -> {
            if(jobRequestList != null){
                for (JobRequest jobRequest: jobRequestList){
                    if (!jobRequest.getId().isEmpty()){
                        JobRequestAsyncDao.insertJob(jobRequest);
                    }
                }
                listener.onComplete(jobRequestList);
            }
        });
    }

    /******************* Get all ***********************/

    /******************* Get Job request ***********************/
    public interface GetJobRequestsListener{
        void onComplete(JobRequest data);
    }

    public void getJobRequest(String id,final GetJobRequestsListener listener){
        JobRequestAsyncDao.getJobRequests(id,listener);
    }
    /******************* Get Job request ***********************/

    // This nested class is responsible for the local database handling
    public static class JobRequestAsyncDao {
        public static void getJobRequests(String id,GetJobRequestsListener listener) {
            new AsyncTask<Void,Void,JobRequest>(){

                @Override
                protected JobRequest doInBackground(Void... voids) {
                    JobRequest jobRequest = ModelSql.INSTANCE.jobRequestDao().getJobRequest(id);
                    listener.onComplete(jobRequest);
                    return jobRequest;
                }
            }.execute();
        }

        public static void deleteJob(String jobId) {
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    ModelSql.INSTANCE.jobRequestDao().DeleteById(jobId);
                    return null;
                }
            }.execute();
        }

        public static void updateJob(JobRequest jobRequest){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... Voids) {
                    ModelSql.INSTANCE.jobRequestDao().update(jobRequest);
                    return null;
                }
            }.execute();
        }

        public static void insertJob(JobRequest jobRequest){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... Voids) {
                    ModelSql.INSTANCE.jobRequestDao().insert(jobRequest);
                    return null;
                }
            }.execute();
        }
    }
}
