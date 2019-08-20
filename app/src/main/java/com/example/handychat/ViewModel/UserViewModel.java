package com.example.handychat.ViewModel;

import android.app.Application;
import android.widget.ProgressBar;

import com.example.handychat.Models.User;
import com.example.handychat.Models.UserRepository;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;
    private MutableLiveData<User> mUser;

    public UserViewModel(Application application){
        super(application);
        mRepository = new UserRepository(application);
    }

    public void addUser(User user) {mRepository.addUser(user);}
    public MutableLiveData<User> getUser(String userEmail) {
        if (mUser == null) {
            mUser = new MutableLiveData<>();
        }
        mRepository.getUser(userEmail, new UserRepository.GetUserListener() {
            @Override
            public void onComplete(User user) {
                if (user != null){
                    mUser.postValue(user);
                }
            }
        });
        return mUser;
    }
}
