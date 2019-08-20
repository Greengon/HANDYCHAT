package com.example.handychat.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.handychat.Models.Model;
import com.example.handychat.Models.User;
import com.example.handychat.R;
import com.example.handychat.ViewModel.UserViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class UserViewFragment extends Fragment {
    private static final String USER_EMAIL = "userEmail";
    private String mUserEMail;
    private UserViewModel userViewModel;
    private User mUser;

    // View components
    private ImageView userImage;
    private TextView userName;
    private TextView userEmail;
    private TextView userAddress;
    private TextView userRole;
    private TextView userCategory;
    private TextView userArea;
    private ProgressBar userImageProgressBar;
    private Target target;


    public UserViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /******* Job request preparing *******/
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        // Get the email of the user
        if (getArguments() != null) {
            mUserEMail = getArguments().getString(USER_EMAIL);
        }

        // Create the observer which updates the UI
        final Observer<User> userObserver = user ->  {
            // Update UI
            Log.d("TAG","LiveData of user activated on change");
            mUser = user;
            updateUI();
        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        userViewModel.getUser(mUserEMail).observe(this, userObserver);
    }

    private void updateUI() {
        if (mUser != null){
            // Image
            if (mUser.getImage()== null || mUser.getImage().isEmpty()){
                userImage.setImageResource(R.drawable.avatar);
            } else{
                Picasso.get().setIndicatorsEnabled(true);
                Model.loadImage(mUser.getImage(), imageFile -> {
                    // We will prepare a Target object for Picasso
                    target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            userImage.setImageBitmap(bitmap);
                            userImageProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            userImageProgressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            userImageProgressBar.setVisibility(View.VISIBLE);
                        }
                    };
                    if (imageFile.exists()){
                        // Load the image with Picasso from local file
                        Picasso.get().load(imageFile).into(target);
                    }else{
                        /*
                         TODO: This isn't a good way of working, but it solve some async problems
                         it helps when the app wasn't fast enough to download the  image.
                         If there's time we should figure out a way to remove the next line and
                         solve the problem.
                          */
                        // Try load the image with Picasso from remote
                        Picasso.get().load(mUser.getImage()).into(target);
                        userImageProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }

            userName.setText("Name: " + mUser.getName());
            userEmail.setText("Email: " + mUser.getEmail());
            userAddress.setText("Address: " + mUser.getAddress());
            String role = "";
            if (mUser.handyMan) role += "Handyman ";
            if (mUser.customer) role += "Customer ";
            userRole.setText("Role: " + role);
            userCategory.setText("Category: " + mUser.getCategory());
            userArea.setText("Area: " + mUser.getArea());
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_view, container, false);

        // Get reference to all the view components
        userImage = view.findViewById(R.id.user_view_created_user_image);
        userName = view.findViewById(R.id.user_view_user_name);
        userEmail = view.findViewById(R.id.user_view_email);
        userAddress = view.findViewById(R.id.user_view_address);
        userRole = view.findViewById(R.id.user_view_customer_or_handyman);
        userCategory = view.findViewById(R.id.user_view_category);
        userArea = view.findViewById(R.id.user_view_area);
        userImageProgressBar = view.findViewById(R.id.user_view_image_pb);
        userImageProgressBar.setVisibility(View.INVISIBLE);

        // Set view
        updateUI();

        return view;
    }
}
