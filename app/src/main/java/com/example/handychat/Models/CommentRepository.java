package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.common.base.Strings;

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class CommentRepository {
    private CommentDao mCommentDao;
    static ModelFirebase modelFirebase;

    public CommentRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        mCommentDao = db.commentDao();
        modelFirebase = new ModelFirebase();
    }


    public void getCommentFormRemote(String jobId) {
        Log.d("TAG","Fetching comments form firebase");
        modelFirebase.getAllCommentOfJob(jobId, new ModelFirebase.GetAllCommentListener() {
            @Override
            public void OnSuccess(String jobId, List<Comment> commentList) {
                if(commentList != null){
                    for (Comment comment: commentList){
                        if (!comment.getId().isEmpty()){
                            insert(comment);
                        }
                    }
                }
            }
        });
    }

    public interface AddCommentListener{
        void onComplete(boolean success);
    }

    public void insert(Comment comment){
        new insertAsyncTask(mCommentDao).execute(comment);
        modelFirebase.addComment(comment, new AddCommentListener() {
            @Override
            public void onComplete(boolean success) {
                Log.d("TAG","Added comment to firebase");
            }
        });
    }

    /******************* Get comment ***********************/
    public interface GetCommentListener{
        void onComplete(Comment data);
    }
    public void getComment(String commentId,final GetCommentListener listener){
        CommentAsyncDao.getComment(commentId,listener);
    }
    /******************* Get comment ***********************/

    /******************* Get all ***********************/
    public interface GetAllCommentsListener{
        void onComplete(List<Comment> data);
    }

    public void getAllCommentsOfJob(String jobId, final GetAllCommentsListener listener){
        getCommentFormRemote(jobId);
        CommentAsyncDao.getAllCommentsOfJob(jobId, listener);
    }
    /******************* Get all ***********************/

    /******************* Get Comment request ***********************/
    public interface GetCommentsListener{
        void onComplete(Comment data);
    }

    /******************* Get Comment request ***********************/

    public static class CommentAsyncDao {
        public static void getAllCommentsOfJob(String jobId, final GetAllCommentsListener listener) {
            new AsyncTask<Void, Void, List<Comment>>() {

                @Override
                protected List<Comment> doInBackground(Void... voids) {
                    List<Comment> list = ModelSql.INSTANCE.commentDao().getAllCommentsOfJob(jobId);
                    listener.onComplete(list);
                    return list;
                }

                @Override
                protected void onPostExecute(List<Comment> commentList) {
                    super.onPostExecute(commentList);
                    listener.onComplete(commentList);
                }
            }.execute();
        }

        public static void deleteAllCommentsOfJob(String jobId){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    ModelSql.INSTANCE.commentDao().deleteAllCommentsByJob(jobId);
                    return null;
                }
            }.execute();
        }

        public static void getComment(String commentId, final GetCommentListener listener){
            new AsyncTask<Void,Void,Comment>(){

                @Override
                protected Comment doInBackground(Void... voids) {
                    Comment comment = ModelSql.INSTANCE.commentDao().getComment(commentId);
                    listener.onComplete(comment);
                    return comment;
                }
            }.execute();
        }
    }

        /******************* Insert ***********************/
        // TODO: Merge next class with generic class CommentAsyncTask
        private class insertAsyncTask extends AsyncTask<Comment, Void, Void> {
            private CommentDao mAsyncTaskDao;

            public insertAsyncTask(CommentDao dao) {
                mAsyncTaskDao = dao;
            }

            @Override
            protected Void doInBackground(Comment... comments) {
                mAsyncTaskDao.insert(comments[0]);
                return null;
            }
        }
        /******************* Insert ***********************/

    /******************* Delete ***********************/
        public static void deleteAllCommentForJob(String jobId) {
            // Step 1: Delete the remote comments for the job
            modelFirebase.deleteAllCommentForJob(jobId);
            // Step 2: Delete the local comments for the job
            CommentAsyncDao.deleteAllCommentsOfJob(jobId);
        }
    /******************* Delete ***********************/


}