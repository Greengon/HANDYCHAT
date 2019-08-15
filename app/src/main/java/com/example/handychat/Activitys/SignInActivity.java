package com.example.handychat.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.handychat.Fragments.RegisterFragment;
import com.example.handychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class SignInActivity extends AppCompatActivity {


    private static final String TAG = "EmailPassword"; // Used for Logging
    public FirebaseAuth mAuth;
    private NavController navController;

    /************************* Activity functions *******************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get shared instance of the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_signin);
        navController = Navigation.findNavController(this,R.id.signin_nav_host_fragment);
    }

    /************************* Activity functions *******************/


    // Getters
    public NavController getNavController() {
        return navController;
    }

}
