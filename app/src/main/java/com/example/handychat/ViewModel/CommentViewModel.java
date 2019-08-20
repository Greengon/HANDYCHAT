package com.example.handychat.ViewModel;

import android.app.Application;
import com.example.handychat.Models.Comment;
import com.example.handychat.Models.CommentRepository;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CommentViewModel extends AndroidViewModel implements ViewModelProvider.Factory {
    private CommentRepository mRepository;
    private MutableLiveData<List<Comment>> mCommentList;
    private MutableLiveData<Comment> mComment;
    private Application mApplication;
    private String mParam;


    public CommentViewModel(Application application, String param){
        super(application);
        mRepository = new CommentRepository(application);
        mApplication = application;
        mParam = param;
    }

    // Next function is important for creating the class connected to specific job
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CommentViewModel(mApplication,mParam);
    }

    public MutableLiveData<List<Comment>> getCommentList(){
        if (mCommentList == null){
            mCommentList = new MutableLiveData<>();
        }
        mRepository.getAllCommentsOfJob(mParam, data ->  {
            if (data != null) {
                mCommentList.postValue(data);
            }
        });
        return mCommentList;
    }

    public MutableLiveData<Comment> getComment(String commentId){
        if (mComment == null){
            mComment = new MutableLiveData<>();
        }
        mRepository.getComment(commentId, data -> {
            if (data != null){
                mComment.postValue(data);
            }
        });
        return mComment;
    }

    public void insert(Comment comment, final CommentRepository.AddCommentListener listener) {
        mRepository.insert(comment,listener);
    }
}
