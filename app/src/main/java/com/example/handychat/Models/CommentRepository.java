package com.example.handychat.Models;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import java.util.List;

public class CommentRepository {
    static ModelFirebase modelFirebase;

    public CommentRepository(Application application){
        AppLocalDbRepository db = ModelSql.getDatabase(application);
        modelFirebase = new ModelFirebase();
    }

    /******************* Add comment ***********************/
    public interface AddCommentListener{
        void onComplete(boolean success);
    }

    public void insert(Comment comment,final AddCommentListener listener){
        CommentAsyncDao.insertComment(comment,listener);
        modelFirebase.addComment(comment);
    }
    /******************* Add comment ***********************/

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
        Log.d("TAG","Fetching comments form firebase");
        modelFirebase.getAllCommentOfJob(jobId, commentList -> {
            if(commentList != null){
                for (Comment comment: commentList){
                    if (!comment.getId().isEmpty()){
                        insert(comment,success -> {
                            Log.d("TAG","Inserted comment:" + comment.getId());
                        });
                    }
                }
            }
            CommentAsyncDao.getAllCommentsOfJob(jobId, listener);
        });
    }
    /******************* Get all ***********************/

    /******************* Delete ***********************/
    public static void deleteAllCommentForJob(String jobId) {
        // Step 1: Delete the remote comments for the job
        modelFirebase.deleteAllCommentForJob(jobId);
        // Step 2: Delete the local comments for the job
        CommentAsyncDao.deleteAllCommentsOfJob(jobId);
    }
    /******************* Delete ***********************/

    // This nested class is responsible for the local database handling
    private static class CommentAsyncDao {
        private static void getAllCommentsOfJob(String jobId, final GetAllCommentsListener listener) {
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

        private static void deleteAllCommentsOfJob(String jobId){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    ModelSql.INSTANCE.commentDao().deleteAllCommentsByJob(jobId);
                    return null;
                }
            }.execute();
        }

        private static void getComment(String commentId, final GetCommentListener listener){
            new AsyncTask<Void,Void,Comment>(){

                @Override
                protected Comment doInBackground(Void... voids) {
                    Comment comment = ModelSql.INSTANCE.commentDao().getComment(commentId);
                    listener.onComplete(comment);
                    return comment;
                }
            }.execute();
        }

        private static void insertComment(Comment comment,AddCommentListener listener){
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... voids) {
                    ModelSql.INSTANCE.commentDao().insert(comment);
                    // Notify the view that the comment was saved at least locally
                    listener.onComplete(true);
                    return null;
                }
            }.execute();
        }
    }


}
