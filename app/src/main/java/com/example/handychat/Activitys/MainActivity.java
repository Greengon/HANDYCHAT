package com.example.handychat.Activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.example.handychat.Fragments.JobRequestList;
import com.example.handychat.Fragments.JobRequestView;
import com.example.handychat.Fragments.NewCommentFragment;
import com.example.handychat.Fragments.NewJobRequestFragment;
import com.example.handychat.R;
import com.example.handychat.ViewModel.JobRequestViewModel;

public class MainActivity extends AppCompatActivity implements JobRequestList.ListJobsListener,JobRequestView.UserPressedListener,JobRequestView.ListCommentListener {
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        if (savedInstanceState == null){
            JobRequestList jobRequestList = JobRequestList.newInstance();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.main_fragment_container,jobRequestList);
            transaction.commit();
        }
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
            case R.id.imageButtonAdd:
                Toast.makeText(this,"Add button was pressed.",Toast.LENGTH_SHORT).show();
                NewJobRequestFragment newJobRequestFragment = NewJobRequestFragment.newInstance();
                FragmentTransaction tranAdd = getSupportFragmentManager().beginTransaction();
                tranAdd.replace(R.id.main_fragment_container,newJobRequestFragment);
                tranAdd.addToBackStack("Add new request");
                tranAdd.commit();
                break;

        }
    }

    // Move to the fragment of the selected item
    public void onJobRequestSelected(String id) {
        JobRequestView jobRequestView = JobRequestView.newInstance(id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, jobRequestView);
        transaction.addToBackStack("A jobRequest as been selected");
        transaction.commit();
    }

    @Override
    public void OnCommentSelected(String id) {
        Toast.makeText(this,"Comment selected: " + id,Toast.LENGTH_SHORT);
    }

    @Override
    public void OnPressed(String id) {
        NewCommentFragment newCommentFragment = NewCommentFragment.newInstance(id);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_fragment_container, newCommentFragment);
        transaction.addToBackStack("Add comment as been selected");
        transaction.commit();
    }
    /************************* Activity functions *******************/
}
