package com.example.handychat.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.example.handychat.R;

public class MainActivity extends AppCompatActivity {
    private ImageButton searchBtn;
    private ImageButton userBtn;
    private ImageButton gpsBtn;
    private ImageButton addNewRequestBtn;
    private NavController navController;

    // Getters
    public NavController getNavController() {
        return navController;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        // Lets create a reference to all the bar's button
        searchBtn = findViewById(R.id.imageButtonSearch);
        userBtn = findViewById(R.id.imageButtonUser);
        gpsBtn = findViewById(R.id.imageButtonGps);
        addNewRequestBtn = findViewById(R.id.imageButtonAdd);

        // Lets now set on click listener on each of them
        searchBtn.setOnClickListener(view -> {
            // TODO: Create this listener for what happens when we press the search button
        });
        userBtn.setOnClickListener(view -> {
            // TODO: Create this listener for what happens when we press the user button
        });
        gpsBtn.setOnClickListener(view -> {
            // TODO: Create this listener for what happens when we press the gps button
        });
        addNewRequestBtn.setOnClickListener(view -> {
            navController.navigate(R.id.action_global_imageButtonAdd);
        });
    }
}
