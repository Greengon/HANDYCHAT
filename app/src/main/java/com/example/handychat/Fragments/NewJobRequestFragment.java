package com.example.handychat.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.Model;
import com.example.handychat.Models.User;
import com.example.handychat.R;
import com.example.handychat.ViewModel.JobRequestViewModel;
import com.example.handychat.ViewModel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class NewJobRequestFragment extends Fragment {
    Bitmap imageBitmap;
    private View view;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private JobRequestViewModel mJobRequestViewModel;

    // Next int is used for takePic function
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // View components
    private ImageView imageView;
    private EditText addressEditText;
    private EditText descriptionEditText;
    private Button saveBtn;
    private ProgressBar progressBar;
    private PackageManager packageManager;


    public NewJobRequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJobRequestViewModel = ViewModelProviders.of(this).get(JobRequestViewModel.class);
        // Lets get the current user for his email and image
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_job_request, container, false);
        packageManager = view.getContext().getPackageManager();

        // Create references to view components
        imageView = view.findViewById(R.id.newJobImageView);
        addressEditText = view.findViewById(R.id.newJobEditTextAddress);
        descriptionEditText = view.findViewById(R.id.newFragmentEditTextDescription);
        saveBtn = view.findViewById(R.id.newJobSaveBtn);
        progressBar = view.findViewById(R.id.new_job_pb);
        progressBar.setVisibility(View.INVISIBLE);

        imageView.setOnClickListener(viewObject -> {
            takePic();
        });
        saveBtn.setOnClickListener(viewObject -> {
            save();
        });
        return view;
    }

    private void save() {
        // Active progress bar
        progressBar.setVisibility(View.VISIBLE);

        // save image

        // First let's make sure we have permission to access
        // the sd card
        boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }

        // TODO: Enable polling image from gallery
        if (imageBitmap != null){ // Checks if the user took a picture
            Model.instance.saveImage(imageBitmap, url ->  {
                // Lets create our new jobRequest object
                JobRequest jobRequest = new JobRequest(UUID.randomUUID().toString(),
                        url,
                        ((MainActivity)getActivity()).getUser().getEmail(),
                        ((MainActivity)getActivity()).getUser().getImage(),
                        Calendar.getInstance().getTime().toString(),
                        addressEditText.getText().toString(),
                        descriptionEditText.getText().toString());

                // Now let's save it to remote FireBase and locally
                mJobRequestViewModel.insert(jobRequest, () -> {
                    // Stop progress bar
                    progressBar.setVisibility(View.INVISIBLE);

                    // Close fragment and navigate back to list
                    Navigation.findNavController(getView()).popBackStack();
                });
            }); } else {
            Toast.makeText(getContext(),"Please take a picture in order to move forward.",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /*
    In takePic function we will create and start an intent
    to take a picture with the phones camera, after that we should
    create a function to catch the result of the activity created from
    the intent
     */
    private void takePic() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(packageManager) != null){
            startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
        }
    }

    /*
    onActivityResult catches the result of the camera activity for
    us getting the image taken
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}
