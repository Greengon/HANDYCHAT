package com.example.handychat.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.handychat.Fragments.JobRequestList;
import com.example.handychat.Fragments.JobRequestView;
import com.example.handychat.Fragments.NewCommentFragment;
import com.example.handychat.Fragments.NewJobRequestFragment;
import com.example.handychat.R;
import com.example.handychat.ViewModel.JobRequestViewModel;

public class MainActivity extends AppCompatActivity {
    private static Context context;
    private ImageButton addNewRequestBtn;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        if (savedInstanceState == null){
            JobRequestList jobRequestList = JobRequestList.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.nav_host_fragment,jobRequestList);
            transaction.commit();
        }

        navController = Navigation.findNavController(this,R.id.nav_host_fragment);

        // TODO: fix the exception that happens when someone press on add button out of
        addNewRequestBtn = (ImageButton) findViewById(R.id.imageButtonAdd);
        navController = Navigation.findNavController(this,R.id.nav_host_fragment);
        addNewRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNavController().navigate(R.id.action_jobRequestList_to_newJobRequestFragment);
            }
        });

    }

    public NavController getNavController() {
        return navController;
    }

    /************************* Activity functions *******************/
    public void userPressedMainActivity(View view) {
        switch(view.getId()){
            case R.id.imageButtonSearch:
                Toast.makeText(this,"Search button was pressed.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButtonUser:
                Toast.makeText(this,"User button was pressed.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.imageButtonGps:
                Toast.makeText(this,"GPS button was pressed.",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /************************* Activity functions *******************/
}
