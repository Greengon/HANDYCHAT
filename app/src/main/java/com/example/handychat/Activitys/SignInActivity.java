package com.example.handychat.Activitys;

import android.os.Bundle;
import com.example.handychat.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class SignInActivity extends AppCompatActivity {
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        navController = Navigation.findNavController(this,R.id.signin_nav_host_fragment);
    }

    // Getters
    public NavController getNavController() {
        return navController;
    }

}
