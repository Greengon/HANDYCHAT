package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class CommentRepository {
    private CommentDao mCommentDao;
    private CommentListData mAllComments;
    static ModelFirebase modelFirebase;

    public CommentRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        mCommentDao = db.commentDao();
        modelFirebase = new ModelFirebase();
        mAllComments = new CommentListData();
    }

    public interface AddCommentListener{
        void onComplete(boolean success);
    }

    public void insert(Comment comment, AddCommentListener listener){
        new insertAsyncTask(mCommentDao).execute(comment);
        modelFirebase.addComment(comment,listener);
    }

    // Getters
    public CommentListData getmAllComments(){
        return mAllComments;
    }

    /******************* Get all ***********************/
    public interface GetAllCommentsListener{
        void onComplete(List<Comment> data);
    }

    public static void getAllCommentsOfJob(String jobId, final GetAllCommentsListener listener){
        CommentAsyncDao.getAllCommentsOfJob(jobId,listener);
    }
    /******************* Get all ***********************/

    /******************* Get Comment request ***********************/
    public interface GetCommentsListener{
        void onComplete(Comment data);
    }

    public void getComment(String id,final GetCommentsListener listener){
        CommentAsyncDao.getComments(id,listener);
    }
    /******************* Get Comment request ***********************/

    public static class CommentAsyncDao {
        public static void getAllCommentsOfJob(String jobId,final GetAllCommentsListener listener){
            new AsyncTask<Void,Void,List<Comment>>(){

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

        public static void getComments(String id,GetCommentsListener listener) {
            new AsyncTask<Void,Void,Comment>(){

                @Override
                protected Comment doInBackground(Void... voids) {
                    Comment comment = ModelSql.INSTANCE.commentDao().getComment(id);
                    listener.onComplete(comment);
                    return comment;
                }
            }.execute();
        }
    }

    /******************* Insert ***********************/
    private class insertAsyncTask extends AsyncTask<Comment,Void,Void> {
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

    public class CommentListData extends MutableLiveData<List<Comment>> {
        @Override
        protected void onActive() {
            super.onActive();
            GetCurrentList();
        }

        @Override
        protected void onInactive() {
            super.onInactive();
//            modelFirebase.cancelGetAllComments();
            Log.d("TAG","cancelGetAllComments");

        }

        public CommentListData(){
            super();
//            setValue(new LinkedList<Comment>());
            GetCurrentList();
        }

        private void GetCurrentList(){
            modelFirebase.getAllComment(new ModelFirebase.getAllCommentListener() {
                @Override
                public void OnSuccess(List<Comment> commentList) {
                    Log.d("TAG","FB data = " + commentList.size());
                    // SetValue invokes onChange in the observers listeners
                    setValue(commentList);
                    for(Comment comment: commentList){
                        insert(comment, new AddCommentListener() {
                            @Override
                            public void onComplete(boolean success) {
                                Log.d("TAG","New FB data with id: " + comment.getId());
                            }
                        });
                    }
                }
            });
        }
    }
}
