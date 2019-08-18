package com.example.handychat.ViewModel;

import android.app.Application;

import com.example.handychat.Models.User;
import com.example.handychat.Models.UserRepository;
import androidx.lifecycle.AndroidViewModel;

public class UserViewModel extends AndroidViewModel {
    private UserRepository mRepository;

    public UserViewModel(Application application){
        super(application);
        mRepository = new UserRepository(application);
    }

    public void addUser(User user) {mRepository.insert(user);}
}
