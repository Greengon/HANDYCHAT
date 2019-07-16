package com.example.handychat.Activitys;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.handychat.Fragments.RegisterFragment;
import com.example.handychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class SignInActivity extends AppCompatActivity {


    private static final String TAG = "EmailPassword"; // Used for Logging
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;

    /************************* Activity functions *******************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get shared instance of the FirebaseAuth object
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_signin);
        mEmailField = findViewById(R.id.editTextEmail);
        mPasswordField = findViewById(R.id.editTextPassword);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void userPressed(View view) {
        // Let's check what was pressed and act accordingly
        int i = view.getId();
        switch (i){
            case R.id.signin_login_btn:
                Toast.makeText(this,"log in button was pressed",Toast.LENGTH_SHORT).show();
                signin(mEmailField.getText().toString(),mPasswordField.getText().toString());
                break;
            case R.id.signin_register_btn:
                Toast.makeText(this,"register button was pressed",Toast.LENGTH_SHORT).show();
                addRegisterFragment();
                break;
            case R.id.forget_password_text:
                Toast.makeText(this,"forget password was pressed",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void addRegisterFragment() {
        // Lets go to our register fragment
        RegisterFragment fragment = RegisterFragment.newInstance();
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_frame_layout,fragment);
        fragmentTransaction.addToBackStack("TAG");
        fragmentTransaction.commit();
    }

    /************************* Activity functions *******************/


    /************************* Auth functions *******************/
    private void signin(String email, String password){
        Log.d(TAG, "signIn:" + email);
        if (!validateFrom()){
            return;
        }

        // Start signing in user with email
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG,"signInWithEmail:failure",task.getException());
                            Toast.makeText(SignInActivity.this,"Authentication failed.",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
        // End of signing in user with email
    }

    private void updateUI(FirebaseUser user){
        if (user != null){
            /*
            Code when user is logged in.
             */
            /*********************** Test ************************/
            Toast.makeText(this,"user already logged in.",Toast.LENGTH_SHORT).show();
            /*********************** Test ************************/
        } else {
            /*
            Code when user is not logged in.
             */
            /*********************** Test ************************/
            Toast.makeText(this,"user not logged in.",Toast.LENGTH_SHORT).show();
            /*********************** Test ************************/

        }
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
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        return valid;
    }

}
