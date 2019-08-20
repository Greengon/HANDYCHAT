package com.example.handychat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.handychat.Activitys.SignInActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.handychat.R;
import com.example.handychat.ViewModel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPassFragment extends Fragment {
    UserViewModel viewModel;
    private FirebaseAuth mAuth;
    private View view;
    private EditText mEmailField;
    private Button mSendEmailBtn;

    public forgotPassFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        view = inflater.inflate(R.layout.fragment_forgot_pass, container, false);
        mEmailField=(EditText)view.findViewById(R.id.editTextEmail);
        mSendEmailBtn=(Button)view.findViewById(R.id.send_email_btn);

        mSendEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail=mEmailField.getText().toString();

                if(TextUtils.isEmpty(userEmail)){
                    Toast.makeText(getActivity(),"please right provide your valid email first",Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getActivity(),"please check your email account",Toast.LENGTH_SHORT).show();
                                ((SignInActivity)getActivity()).getNavController().navigate(R.id.action_forgotPassFragment_to_logInFragment);
                            }
                            else{
                                String message=task.getException().getMessage();
                                Toast.makeText(getActivity(),"error Occurred: " + message,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }



}
