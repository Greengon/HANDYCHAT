package com.example.handychat.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.Models.JobRequest;
import com.example.handychat.Models.Model;
import com.example.handychat.R;
import com.example.handychat.ViewModel.JobRequestViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class EditJobRequestFragment extends Fragment {
    private static final String JOB_ID = "jobID";
    private JobRequest mJobRequest;
    private JobRequestViewModel jobRequestViewModel;
    public String jobId;
    public String imageUrl;
    Bitmap imageBitmap;
    Target target;

    private PackageManager packageManager;
    private static final int REQUEST_WRITE_STORAGE = 112;

    // Next int is used for takePic function
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // View components
    private ImageView jobImage;
    private EditText addressEditText;
    private EditText descriptionEditText;
    private Button saveBtn;
    private Spinner mCategoriesSpinner;
    private Spinner mAreasSpinner;
    private ProgressBar progressBar;

    public EditJobRequestFragment() {
        // Required empty public constructor
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobRequestViewModel = ViewModelProviders.of(this).get(JobRequestViewModel.class);
        if (getArguments() != null) {
            jobId = getArguments().getString(JOB_ID);
            // Create the observer which updates the UI
            final Observer<JobRequest> jobRequestObserver = jobRequest -> {
                mJobRequest = jobRequest;
                createJobView();
            };

            // Observe the LiveData, pass this fragment as the LifecycleOwner and the observer
            jobRequestViewModel.getJobRequest(jobId).observe(this,jobRequestObserver);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_job_request, container, false);
        packageManager = view.getContext().getPackageManager();

        // Create reference to the view components
        jobImage = view.findViewById(R.id.editJobImageView);
        addressEditText = view.findViewById(R.id.editJobEditTextAddress);
        descriptionEditText = view.findViewById(R.id.editFragmentEditTextDescription);
        mCategoriesSpinner = view.findViewById(R.id.DropDownCategories);
        mAreasSpinner = view.findViewById(R.id.DropDownAreas);
        saveBtn = view.findViewById(R.id.editJobSaveBtn);
        progressBar = view.findViewById(R.id.edit_job_pb);
        progressBar.setVisibility(View.INVISIBLE);


        // TODO: Enable polling image from gallery
        jobImage.setOnClickListener(viewObject -> {
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

        // First let's make sure we have permission to access
        // the sd card
        boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission){
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }

        // Lets create our new jobRequest object
        JobRequest newJobRequest = new JobRequest(
                mJobRequest.getId(),
                imageUrl,
                ((MainActivity)getActivity()).getUser().getEmail(),
                ((MainActivity)getActivity()).getUser().getImage(),
                Calendar.getInstance().getTime().toString(),
                addressEditText.getText().toString(),
                descriptionEditText.getText().toString(),
                mCategoriesSpinner.getSelectedItem().toString(),
                mAreasSpinner.getSelectedItem().toString()
        );

        // Now let's save it to remote FireBase and locally
        jobRequestViewModel.update(newJobRequest, () -> {
            // Stop progress bar
            progressBar.setVisibility(View.INVISIBLE);

            // Close fragment and navigate back to list
            Navigation.findNavController(getView()).popBackStack(R.id.jobRequestView,true);
        });
    }

    /*
    In takePic function we will create and start an intent
    to take a picture with the phones camera, after that we should
    create a function to catch the result of the activity created from
    the intent
     */
    private void takePic() {
        progressBar.setVisibility(View.VISIBLE);
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
            Model.saveImage(imageBitmap, url ->  {
                imageUrl = url;
            });
            progressBar.setVisibility(View.INVISIBLE);
            jobImage.setImageBitmap(imageBitmap);
        }
    }

    private void createJobView() {
        if (mJobRequest != null){
            addressEditText.setText(mJobRequest.getAddress(), TextView.BufferType.EDITABLE);
            descriptionEditText.setText(mJobRequest.getDescription(), TextView.BufferType.EDITABLE);
            // Lets creates the categories mCategoriesSpinner.
            // Create an ArrayAdapter using the string array and a default mCategoriesSpinner layout
            ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, R.layout.support_simple_spinner_dropdown_item);

            // Specify the layout to use when the list of choices appears
            categoriesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            // Apply the categoriesAdapter to the mCategoriesSpinner
            mCategoriesSpinner.setAdapter(categoriesAdapter);

            // And the same for areas
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> areasAdapter = ArrayAdapter.createFromResource(getContext(), R.array.areas, R.layout.support_simple_spinner_dropdown_item);

            // Specify the layout to use when the list of choices appears
            areasAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            // Apply the areasAdapter to the spinner
            mAreasSpinner.setAdapter(areasAdapter);

            /****** Get job request image ********/
            if (mJobRequest.getImageUrl() != null){
                imageUrl = mJobRequest.getImageUrl();
                Picasso.get().setIndicatorsEnabled(true);
                Model.loadImage(imageUrl, imageFile -> {
                    target = new Target() {
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
                    Picasso.get().load(imageFile).into(target);
                });
            }
        }
    }
}


