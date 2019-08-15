package com.example.handychat.ViewModel;

import android.app.Application;

import com.example.handychat.Models.User;
import com.example.handychat.Models.UserRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;

    public UserViewModel(Application application){
        super(application);
        mRepository = new UserRepository(application);
    }

    public void insert(User user) {mRepository.insert(user);}
}
