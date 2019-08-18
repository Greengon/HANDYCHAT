package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Update;

public class JobRequestRepository {
    private JobRequestDao mJobRequestDao;
    static ModelFirebase modelFirebase;

    public JobRequestRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        mJobRequestDao = db.jobRequestDao();
        modelFirebase = new ModelFirebase();
    }

    public void GetRemoteJobRequestList(GetAllJobRequestsListener listener){
        modelFirebase.getAllJobRequest(new ModelFirebase.getAllJobRequestListener() {
            @Override
            public void OnSuccess(List<JobRequest> jobRequestList) {
                if(jobRequestList != null){
                    listener.onComplete(jobRequestList);
                    for (JobRequest jobRequest: jobRequestList){
                        if (!jobRequest.getId().isEmpty()){
                            insert(jobRequest, new AddJobRequestListener() {
                                @Override
                                public void onComplete(boolean success) {
                                    Log.d("TAG","Added new job request with id: " + jobRequest.getId());
                                }
                            });
                        }
                    }
                }
            }
        });
     }

    /******************* Update ***********************/
    public void update(JobRequest jobRequest, ModelFirebase.UpdateJobRequestListener listener) {
        modelFirebase.UpdateJobRequest(jobRequest,listener);
        JobRequestAsyncDao.updateJob(jobRequest);
    }

    public interface UpdateJobRequestListener {
        public void onComplete(boolean success);
    }

    /******************* Update ***********************/

    /******************* Delete ***********************/
    // TODO: Below solution didn't fix the problem
    /*
    Note that a listener was created here because
    deleting job takes time and happens async,
    so we want to force the user to wait for the final delete
    to avoid reinserting
     */
    public interface JobDeletedListener{
        void onComplete();
    }
    public void delete(String jobId, final JobDeletedListener listener) {
        // Step 1+2: Delete the remote and local comments for the job
        CommentRepository.deleteAllCommentForJob(jobId);

        // Step 3: Delete the job in the remote Database
        modelFirebase.deleteJob(jobId);

        // Step 4: Delete the job in the local Database
        // TODO: Check why Job..
        JobRequestAsyncDao.deleteJob(jobId,listener);
    }
    /******************* Delete ***********************/

    public interface AddJobRequestListener{
        void onComplete(boolean success);
    }

    public void insert(JobRequest jobRequest, AddJobRequestListener listener){
        new insertAsyncTask(mJobRequestDao).execute(jobRequest);
        modelFirebase.addJobRequest(jobRequest,listener);
    }

    /******************* Get all ***********************/
    public interface GetAllJobRequestsListener{
        void onComplete(List<JobRequest> data);
    }

    public void getAllJobRequests(final GetAllJobRequestsListener listener){
        GetRemoteJobRequestList(listener);
        // We can use the next method if we check before for internet connection else
        // it will create an async problem.
//        JobRequestAsyncDao.getAllJobRequests(listener);
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

    public static class JobRequestAsyncDao {
        public static void getAllJobRequests(final GetAllJobRequestsListener listener){
            new AsyncTask<Void,Void,List<JobRequest>>(){

                @Override
                protected List<JobRequest> doInBackground(Void... voids) {
                    List<JobRequest> list = ModelSql.INSTANCE.jobRequestDao().getAllJobRequests();
                    listener.onComplete(list);
                    return list;
                }

                @Override
                protected void onPostExecute(List<JobRequest> jobRequestList) {
                    super.onPostExecute(jobRequestList);
                    listener.onComplete(jobRequestList);
                }
            }.execute();
        }

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

        public static void deleteJob(String jobId,JobDeletedListener listener) {
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    ModelSql.INSTANCE.jobRequestDao().DeleteById(jobId);
                    listener.onComplete();
                    return null;
                }
            }.execute();
        }

        public static void updateJob(JobRequest jobRequest){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... Voids) {
                    ModelSql.INSTANCE.jobRequestDao().Update(jobRequest);
                    return null;
                }
            }.execute();
        }
    }

    /******************* Insert ***********************/
    // TODO: Merge next function with JobRequestAsyncDao
    private class insertAsyncTask extends AsyncTask<JobRequest,Void,Void> {
        private JobRequestDao mAsyncTaskDao;

        public insertAsyncTask(JobRequestDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(JobRequest... jobRequests) {
            mAsyncTaskDao.insert(jobRequests[0]);
            return null;
        }
    }
    /******************* Insert ***********************/

}
