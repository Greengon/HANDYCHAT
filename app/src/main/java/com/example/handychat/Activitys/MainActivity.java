package com.example.handychat.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.handychat.Fragments.JobRequestList;
import com.example.handychat.Models.User;
import com.example.handychat.R;
import com.example.handychat.ViewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ImageButton searchBtn;
    private ImageButton userBtn;
    private ImageButton gpsBtn;
    private ImageButton addNewRequestBtn;
    private ImageButton homeBtn;
    private NavController navController;
    private String query = "";
    private User user;
    private UserViewModel mUserViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        // Create the observer which updates the UI
        final Observer<User> userObserver = resultUser ->  {
            // Update UI
            Log.d("TAG","LiveData of user activated on change");
            user = resultUser;
            // Only when we will get the user from the db then we should go to the main fragment.
            // Let's build our query
            // First let's check if user used the search option and the query isn't empty
            // it this case we would do nothing.
            if (query.isEmpty()){
                // Lets check if our user is a handyman, if not do nothing.
                if (user.handyMan){
                    query += "HANDYMAN|";
                    query += user.getCategory();
                    query += "|";
                    query += user.getArea();
                    query += "|";
                    query += user.getEmail();
                }
            }
            Toast.makeText(this,"Loading job request that may be interesting for you",Toast.LENGTH_SHORT).show();
            navController.navigate(R.id.jobRequestList);
        };

        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        mUserViewModel.getUser(userEmail).observe(this,userObserver);


        // Try to catch a search query if it was created
        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            query = intent.getStringExtra(SearchManager.QUERY);
        }

        // Lets create a reference to all the bar's button
        searchBtn = findViewById(R.id.imageButtonSearch);
        userBtn = findViewById(R.id.imageButtonUser);
        gpsBtn = findViewById(R.id.imageButtonGps);
        addNewRequestBtn = findViewById(R.id.imageButtonAdd);
        homeBtn=findViewById(R.id.imageButtonHome);

        // Lets now set on click listener on each of them
        searchBtn.setOnClickListener(view -> {
            onSearchRequested();
        });
        userBtn.setOnClickListener(view -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Bundle bundle = new Bundle();
            bundle.putString("userEmail",user.getEmail());
            navController.navigate(R.id.action_global_imageButtonUser,bundle);
        });
        gpsBtn.setOnClickListener(view -> {
            // TODO: Create this listener for what happens when we press the gps button
            Toast.makeText(this,"On construction, stay tuned",Toast.LENGTH_SHORT).show();
        });
        addNewRequestBtn.setOnClickListener(view -> {
            navController.navigate(R.id.action_global_imageButtonAdd);
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_global_imageButtonHome);
            }
        });
    };

    public String getQuery() {
        return query;
    }

    public User getUser() {
        return user;
    }
}
