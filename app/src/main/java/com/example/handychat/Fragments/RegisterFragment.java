package com.example.handychat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private static int RESULT_LOAD_IMAGE = 1;

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
        Bitmap imageBitmap;

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

        // Lets creates the categories mCategoriesSpinner.
        // Create an ArrayAdapter using the string array and a default mCategoriesSpinner layout
        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(getContext(),R.array.categories,R.layout.support_simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        categoriesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // Apply the categoriesAdapter to the mCategoriesSpinner
        mCategoriesSpinner.setAdapter(categoriesAdapter);

        // And the same for areas
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> areasAdapter = ArrayAdapter.createFromResource(getContext(),R.array.areas,R.layout.support_simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        areasAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // Apply the areasAdapter to the spinner
        mAreasSpinner.setAdapter(areasAdapter);

        // Create a listener to catch if someone pressed on the save.
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(mEmailField.getText().toString(),mPasswordField.getText().toString());
            }
        });

        // Let's create a listener for adding photo
        addPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                if (photoPickerIntent.resolveActivity(packageManager) != null){
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
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
    private void createAccount(final String email, String password){
        Log.d(TAG, "createAccount:" + email);
        if (!validateFrom()){
            return;
        }

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
                                    "",
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
        } else {
            mEmailField.setError(null);
        }

        // Validate password
        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)){
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

        /*
    onActivityResult catches the result of the image picker activity for
    us getting the image taken
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
    }
}
