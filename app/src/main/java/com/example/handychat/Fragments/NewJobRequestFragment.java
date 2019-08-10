package com.example.handychat.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.Model;
import com.example.handychat.R;


import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NewJobRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewJobRequestFragment extends Fragment {

    private View view;
    private ImageView imageView;
    private EditText addressEditText;
    private EditText descriptionEditText;
    Bitmap imageBitmap;
    private Button saveBtn;
    private ProgressBar progressBar;
    private PackageManager packageManager;
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Next int is used for takePic function
    static final int REQUEST_IMAGE_CAPTURE = 1;

    public NewJobRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewJobRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewJobRequestFragment newInstance() {
        NewJobRequestFragment fragment = new NewJobRequestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_new_job_request, container, false);
        imageView = (ImageView) view.findViewById(R.id.newJobImageView);
        addressEditText = (EditText) view.findViewById(R.id.newJobEditTextAddress);
        descriptionEditText = (EditText) view.findViewById(R.id.newFragmentEditTextDescription);
        packageManager = view.getContext().getPackageManager();
        saveBtn = (Button) view.findViewById(R.id.newJobSaveBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.new_job_pb);
        progressBar.setVisibility(View.INVISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
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

        Model.instance.saveImage(imageBitmap, new Model.SaveImageListener() {
            @Override
            public void onComplete(String url) {

                // Lets create our new jobRequest object
                JobRequest jobRequest = new JobRequest(UUID.randomUUID().toString(),
                        url,"",
                        Calendar.getInstance().getTime().toString(),
                        addressEditText.getText().toString(),
                        descriptionEditText.getText().toString());

                // Now let's save it to remote firebase and locally
                Model.instance.addJobRequest(jobRequest, new Model.AddJobRequestListener() {
                    @Override
                    public void onComplete(boolean success) {
                        // Stop progress bar
                        progressBar.setVisibility(View.INVISIBLE);

                        // Close fragment
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                });

            }

            @Override
            public void fail() {
                Toast.makeText(getContext(),"Failed to save image",Toast.LENGTH_SHORT).show();
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
            imageView.setImageBitmap(imageBitmap);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
