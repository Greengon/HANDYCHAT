package com.example.handychat.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.JobRequestRepository;
import com.example.handychat.Models.Model;
import com.example.handychat.Models.ModelFirebase;
import com.example.handychat.R;
import com.example.handychat.ViewModel.JobRequestViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditJobRequestFragment extends Fragment {
    private static final String JOB_ID = "jobID";
    private JobRequest jobRequest;
    private JobRequestViewModel jobRequestViewModel;
    public String jobId;
    public String resultUrl;

    private ImageView jobImage;
    private EditText addressEditText;
    private EditText descriptionEditText;
    Bitmap imageBitmap;
    private Button saveBtn;
    private ProgressBar progressBar;
    private PackageManager packageManager;
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Next int is used for takePic function
    static final int REQUEST_IMAGE_CAPTURE = 1;


    public EditJobRequestFragment() {
        // Required empty public constructor
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobRequestViewModel = ViewModelProviders.of(this).get(JobRequestViewModel.class);
        if (getArguments() != null) {
            jobId = getArguments().getString(JOB_ID);
            jobRequestViewModel.getJobRequest(jobId, new JobRequestRepository.GetJobRequestsListener() {
                @Override
                public void onComplete(JobRequest data) {
                    jobRequest = data;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_job_request, container, false);

        jobImage = (ImageView) view.findViewById(R.id.editJobImageView);
        addressEditText = (EditText) view.findViewById(R.id.editJobEditTextAddress);
        descriptionEditText = (EditText) view.findViewById(R.id.editFragmentEditTextDescription);
        packageManager = view.getContext().getPackageManager();
        saveBtn = (Button) view.findViewById(R.id.editJobSaveBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.edit_job_pb);
        progressBar.setVisibility(View.INVISIBLE);

        resultUrl = jobRequest.getImageUrl();
        addressEditText.setText(jobRequest.getAddress(), TextView.BufferType.EDITABLE);
        descriptionEditText.setText(jobRequest.getDescription(), TextView.BufferType.EDITABLE);

        /****** Get job request image ********/
        if (jobRequest.getImageUrl() != null){
            Picasso.get().setIndicatorsEnabled(true);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    jobImage.setImageBitmap(bitmap);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.get().load(jobRequest.getImageUrl()).into(target);
        }

        jobImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
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

        // TODO: Do something when user press save without taking a picture
        // TODO: Enable polling image from gallery

        if (imageBitmap != null){
            Model.instance.saveImage(imageBitmap, new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    resultUrl = url;
                    saveNewJobInDB();
                }
            });
        }else {
            saveNewJobInDB();
        }



    }

    private void saveNewJobInDB() {
        // Lets get the current user for his email
        FirebaseUser user  = FirebaseAuth.getInstance().getCurrentUser();
        // Lets create our new jobRequest object
        JobRequest newJobRequest = new JobRequest(
                jobRequest.getId(),
                resultUrl,
                user.getEmail(),
                Calendar.getInstance().getTime().toString(),
                addressEditText.getText().toString(),
                descriptionEditText.getText().toString()
        );

        // Now let's save it to remote firebase and locally
        jobRequestViewModel.update(newJobRequest, new ModelFirebase.UpdateJobRequestListener() {
            @Override
            public void OnSuccess(boolean result) {
                // Stop progress bar
                progressBar.setVisibility(View.INVISIBLE);

                // Close fragment and navigate back to list
                ((MainActivity)getActivity()).getNavController().popBackStack(R.id.jobRequestView,true);

            }
        });

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
            jobImage.setImageBitmap(imageBitmap);
        }
    }
}


