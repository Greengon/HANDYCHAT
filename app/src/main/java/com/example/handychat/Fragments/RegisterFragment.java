package com.example.handychat.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.handychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword"; // Used for Logging
    EditText mEmailField;
    EditText mPasswordField;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        mEmailField = view.findViewById(R.id.editTextEmail);
        mPasswordField = view.findViewById(R.id.editTextPassword);

        // Lets creates the categories categoriesSpinner.
        Spinner categoriesSpinner = (Spinner) view.findViewById(R.id.DropDownCategories);

        // Create an ArrayAdapter using the string array and a default categoriesSpinner layout
        ArrayAdapter<CharSequence> categoriesAdapter = ArrayAdapter.createFromResource(getContext(),R.array.categories,R.layout.support_simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        categoriesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // Apply the categoriesAdapter to the categoriesSpinner
        categoriesSpinner.setAdapter(categoriesAdapter);

        // And the same for areas
        Spinner areasSpinner = (Spinner) view.findViewById(R.id.DropDownAreas);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> areasAdapter = ArrayAdapter.createFromResource(getContext(),R.array.areas,R.layout.support_simple_spinner_dropdown_item);

        // Specify the layout to use when the list of choices appears
        areasAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

        // Apply the areasAdapter to the spinner
        areasSpinner.setAdapter(areasAdapter);

        // Create a listener to catch if someone pressed on to save.
        Button button = (Button) view.findViewById(R.id.saveBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount(mEmailField.getText().toString(),mPasswordField.getText().toString());
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

    /************************* Auth functions *******************/
    private void createAccount(String email, String password){
        Log.d(TAG, "createAccount:" + email);
        if (!validateFrom()){
            return;
        }

        // Start creating user with email
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            // Sign in success, Update UI with the singed-in user's information.
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else{
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getContext(),"Authentication failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
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
    private void updateUI(FirebaseUser user) {
        if (user != null) {
            /*
            Code when user is logged in.
             */
            /*********************** Test ************************/
            Toast.makeText(getContext(), "User created.", Toast.LENGTH_SHORT).show();
            /*********************** Test ************************/
        } else {
            /*
            Code when user is not logged in.
             */
            /*********************** Test ************************/
            Toast.makeText(getContext(), "Failed user created.", Toast.LENGTH_SHORT).show();
            /*********************** Test ************************/

        }
    }
}
