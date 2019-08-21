package com.example.handychat.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.handychat.Activitys.SignInActivity;
import com.example.handychat.Models.Model;
import com.example.handychat.Models.User;
import com.example.handychat.R;
import com.example.handychat.ViewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private static int RESULT_LOAD_IMAGE = 1;
    private static final int GALLERY_REQUEST_CODE = 100;
    UserViewModel viewModel;

    // For image adding
    Bitmap imageBitmap;

    private FirebaseAuth mAuth;
    private PackageManager packageManager;
    private static final String TAG = "EmailPassword"; // Used for Logging
    private ProgressBar progressBar;
    private View view;
    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mAddressField;
    private RadioButton mCustomerRadioButton;
    private RadioButton mHandyManRadioButton;
    private Spinner mCategoriesSpinner;
    private Spinner mAreasSpinner;
    private Button saveBtn;
    private Button addPhotoBtn;
    private ImageView avatarImgView;
    private static final int REQUEST_WRITE_STORAGE = 112;

    public RegisterFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);

        // For image adding
        packageManager = view.getContext().getPackageManager();

        // Lets find all our view components
        mNameField = (EditText) view.findViewById(R.id.editTextName);
        mEmailField = (EditText) view.findViewById(R.id.editTextEmail);
        mPasswordField = (EditText) view.findViewById(R.id.editTextPassword);
        mAddressField = (EditText) view.findViewById(R.id.editTextAddress);
        mCustomerRadioButton = (RadioButton) view.findViewById(R.id.CustomerRadioButton);
        mHandyManRadioButton = (RadioButton) view.findViewById(R.id.HandyManRadioButton);
        mCategoriesSpinner = (Spinner) view.findViewById(R.id.DropDownCategories);
        mAreasSpinner = (Spinner) view.findViewById(R.id.DropDownAreas);
        saveBtn = (Button) view.findViewById(R.id.saveBtn);
        addPhotoBtn = (Button) view.findViewById(R.id.addPictureBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        avatarImgView=(ImageView)  view.findViewById(R.id.avatarImgView);

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

        // Create a listener to catch if someone pressed on the save.
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateFrom()){
                    return;
                }

                save();
            }
        });
        // Let's create a listener for adding photo
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                if (photoPickerIntent.resolveActivity( packageManager) != null){
                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
                }
            }
        });

        mCustomerRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCustomerRadioButton.isSelected()){
                    mCustomerRadioButton.setChecked(false);
                    mCustomerRadioButton.setSelected(false);
                }
                else{
                    mCustomerRadioButton.setChecked(true);
                    mCustomerRadioButton.setSelected(true);
                }
            }
        });

        mHandyManRadioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mHandyManRadioButton.isSelected()){
                    mHandyManRadioButton.setChecked(false);
                    mHandyManRadioButton.setSelected(false);
                }
                else{
                    mHandyManRadioButton.setChecked(true);
                    mHandyManRadioButton.setSelected(true);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;

    }
        private void save()
        {
            // Active progress bar
            progressBar.setVisibility(View.VISIBLE);

            // save image

            // First let's make sure we have permission to access
            // the sd card
            boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
            }
            Model.instance.saveImage(imageBitmap, new Model.SaveImageListener() {
                @Override
                public void onComplete(String url) {
                    createAccount(mEmailField.getText().toString(),mPasswordField.getText().toString(),url);
                }
            });
        }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    /************************* Auth functions *******************/
    private void createAccount(final String email, String password,String imageUrl){
        Log.d(TAG, "createAccount:" + email);


        // Show Progress bar to user
        progressBar.setVisibility(View.VISIBLE);
        // Start creating user with email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, Update UI with the singed-in user's information.
                            Log.d(TAG, "createUserWithEmail:success");

                            // Create user object
                            //TODO: Get user image url
                            User userObject = new User(
                                    mNameField.getText().toString(),
                                    mEmailField.getText().toString(),
                                    imageUrl,
                                    mAddressField.getText().toString(),
                                    mCustomerRadioButton.isChecked(),
                                    mHandyManRadioButton.isChecked(),
                                    mCategoriesSpinner.getSelectedItem().toString(),
                                    mAreasSpinner.getSelectedItem().toString()
                                    );
                            viewModel.addUser(userObject);
                            FirebaseUser user = mAuth.getCurrentUser();
                            ((SignInActivity)getActivity()).getNavController().popBackStack();
                        } else{
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(),"Authentication failed.",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        // End of creating user with email
    }
    /************************* Auth functions *******************/

    /************************* Validation functions *******************/
    // This will validate user filled the form correctly.
    private boolean validateFrom() {
        boolean valid = true;

        // Validate email
        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)){
            mEmailField.setError("Required.");
            valid = false;
        }
        else if (!isValidEmailId(email.trim())){
            mEmailField.setError("Email address is not valid.");
            valid = false;
        }
        else {
            mEmailField.setError(null);
        }

        // Validate password
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        }
        else if(password.length()<6 &&!isValidPassword(password)){
            mPasswordField.setError("password must be at least 6 characters or password not valid");
            valid = false;
        }
        else {
            mPasswordField.setError(null);
        }
        if (imageBitmap== null){
            Toast.makeText(getActivity(),"must add picture ",Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

        /*
    onActivityResult catches the result of the image picker activity for
    us getting the image taken
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
          //  Bundle extras = data.getExtras();
          //  imageBitmap = (Bitmap) extras.get("data");
          ContentResolver result = (ContentResolver)view.getContext().getContentResolver();
          Uri imageUri = data.getData();
             try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(result, imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        if (imageBitmap!= null){
            avatarImgView.setImageBitmap(imageBitmap);
        }


    }

    public static boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

}
