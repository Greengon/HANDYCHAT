package com.example.handychat.ViewModel;

import android.app.Application;
import android.widget.ProgressBar;

import com.example.handychat.Models.Comment;
import com.example.handychat.Models.CommentRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class CommentViewModel extends AndroidViewModel implements ViewModelProvider.Factory {
    private CommentRepository mRepository;
    private MutableLiveData<List<Comment>> mCommentList;
    private Application mApplication;
    private String mParam;


    public CommentViewModel(Application application, String param){
        super(application);
        mRepository = new CommentRepository(application);
        mApplication = application;
        mParam = param;
    }


    public MutableLiveData<List<Comment>> getmCommentList(){
        if (mCommentList == null){
            mCommentList = new MutableLiveData<List<Comment>>();
        }
        mRepository.getAllCommentsOfJob(mParam, new CommentRepository.GetAllCommentsListener() {
            @Override
            public void onComplete(List<Comment> data) {
                if (data != null) {
                    mCommentList.postValue(data);
                }
            }
        });
        return mCommentList;
    }

    public void getComment(String commentId, CommentRepository.GetCommentListener listener){
        mRepository.getComment(commentId, listener);
    }

    public void insert(Comment comment) {
        mRepository.insert(comment);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new CommentViewModel(mApplication,mParam);

    }
}
