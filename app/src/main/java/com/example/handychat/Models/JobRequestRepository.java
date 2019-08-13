package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class JobRequestRepository {
    private JobRequestDao mJobRequestDao;
    private JobRequestListData mAllJobRequests;
//    static ModelSql modelSql;
    static ModelFirebase modelFirebase;

    public JobRequestRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        mJobRequestDao = db.jobRequestDao();
        modelFirebase = new ModelFirebase();
        mAllJobRequests = new JobRequestListData();
    }

    public interface AddJobRequestListener{
        void onComplete(boolean success);
    }

    public void insert(JobRequest jobRequest, AddJobRequestListener listener){
        new insertAsyncTask(mJobRequestDao).execute(jobRequest);
        modelFirebase.addJobRequest(jobRequest,listener);
    }

    // Getters
    public JobRequestListData getmAllJobRequests(){
        return mAllJobRequests;
    }

    /******************* Get all ***********************/
    public interface GetAllJobRequestsListener{
        void onComplete(List<JobRequest> data);
    }

    public static void getAllJobRequests(final GetAllJobRequestsListener listener){
        JobRequestAsyncDao.getAllJobRequests(listener);
    }
    /******************* Get all ***********************/

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

    }

    /******************* Insert ***********************/
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

    public class JobRequestListData extends MutableLiveData<List<JobRequest>> {
        @Override
        protected void onActive() {
            super.onActive();
            modelFirebase.getAllJobRequest(new ModelFirebase.getAllJobRequestListener() {
                @Override
                public void OnSuccess(List<JobRequest> jobRequestList) {
                    Log.d("TAG","FB data = " + jobRequestList.size());
                    // SetValue invokes onChange in the observers listeners
                    setValue(jobRequestList);
                    for(JobRequest jobRequest: jobRequestList){
                        insert(jobRequest, new AddJobRequestListener() {
                            @Override
                            public void onComplete(boolean success) {
                                Log.d("TAG","New FB data as been added");
                            }
                        });
                    }
                }
            });
        }

        @Override
        protected void onInactive() {
            super.onInactive();
//            modelFirebase.cancelGetAllJobRequests();
            Log.d("TAG","cancelGetAllJobRequests");

        }

        public JobRequestListData(){
            super();
//            setValue(JobRequestRepository.getAllJobRequests(new GetAllJobRequestsListener() {
//                @Override
//                public void onComplete(List<JobRequest> data) {
//
//                }
//            }));
            setValue(new LinkedList<JobRequest>());
        }
    }
}
