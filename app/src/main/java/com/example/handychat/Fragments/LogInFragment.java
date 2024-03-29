package com.example.handychat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.handychat.Activitys.MainActivity;
import com.example.handychat.R;
import com.google.firebase.auth.FirebaseAuth;

public class LogInFragment extends Fragment {
    private EditText mEmailField;
    private EditText mPasswordField;
    private ProgressBar mProgressBar;
    private Button mLoginBtn;
    private Button mRegisterBtn;
    private FirebaseAuth mAuth;
    private TextView mForgotPassTxt;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        // Lets find all our view components
        mEmailField = view.findViewById(R.id.editTextEmail);
        mPasswordField = view.findViewById(R.id.editTextPassword);
        mProgressBar = view.findViewById(R.id.signin_progressbar);
        mForgotPassTxt=view.findViewById(R.id.forget_password_text);
        mLoginBtn = view.findViewById(R.id.signin_login_btn);

        mForgotPassTxt.setOnClickListener( viewObject -> {
            Navigation.findNavController(getView()).navigate(R.id.action_logInFragment_to_forgotPassFragment);
        });

        mLoginBtn.setOnClickListener( viewObject -> {
            signin(mEmailField.getText().toString(),mPasswordField.getText().toString());
        });

        mRegisterBtn = view.findViewById(R.id.signin_register_btn);
        mRegisterBtn.setOnClickListener( viewObject -> {
            Navigation.findNavController(getView()).navigate(R.id.action_logInFragment_to_registerFragment);
        });

        return view;
    }

    private void signin(String email, String password){
        Log.d("TAG", "signIn:" + email);
        if (!validateFrom()){
            return;
        }

        // Show progress bar for the user
        mProgressBar.setVisibility(View.VISIBLE);

        // Start signing in user with email
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener( task -> {
                    if(task.isSuccessful()){
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG", "signInWithEmail:success");
                        mProgressBar.setVisibility(View.GONE);
                        onLoginSuccess();
                    }else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG","signInWithEmail:failure",task.getException());
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(),"Either email or password is incorrect, please try again.",Toast.LENGTH_SHORT).show();
                    }
                });
        // End of signing in user with email
    }

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

    public void onLoginSuccess(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
