package com.example.handychat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NewJobRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewJobRequestFragment extends Fragment {

    private View view;
    private ImageButton imageButton;
    Bitmap imageBitmap;
    private PackageManager packageManager;


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
        imageButton = (ImageButton) view.findViewById(R.id.newJobImageButton);
        packageManager = view.getContext().getPackageManager();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        return view;
    }

    private void openCamera() {
        // First lets check if there is a camera on the device.
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // Lets create camera intent and fire it out.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(packageManager) != null){

                // Randomly chosen request code for the intent.
                startActivityForResult(takePictureIntent, 111);
            }
        } else { // If no camera is found.
            Toast.makeText(view.getContext(),"Device has no camera.",Toast.LENGTH_SHORT).show();
        }
    }

    // This function will catch the returned image from the camera.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111){
            // Lets retrieve the image and display it.
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageButton.setImageBitmap(imageBitmap);
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
