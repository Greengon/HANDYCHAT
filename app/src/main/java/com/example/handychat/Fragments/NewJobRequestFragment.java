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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.Model;
import com.example.handychat.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    Bitmap imageBitmap;
    private ProgressBar progressBar;
    private PackageManager packageManager;

    // Next to int are used for takePic function
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
        packageManager = view.getContext().getPackageManager();
        progressBar = (ProgressBar) view.findViewById(R.id.new_job_pb);
        progressBar.setVisibility(View.INVISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });
        return view;
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

            // Save image
            progressBar.setVisibility(View.VISIBLE);
            Model.instance.saveImage(imageBitmap, new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
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
