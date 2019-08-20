package com.example.handychat.Fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.handychat.Models.Comment;
import com.example.handychat.Models.Model;
import com.example.handychat.R;
import com.example.handychat.ViewModel.CommentViewModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class ViewCommentFragment extends Fragment {
    private static final String COMMENT_ID = "commentID";
    private Comment mComment;
    private CommentViewModel commentViewModel;
    private String commentId;

    // Fragment view component
    private ImageView userCreatedImage;
    private TextView userCreatedName;
    private TextView commentDate;
    private TextView commentText;
    private Target target;

    public ViewCommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            commentId = getArguments().getString(COMMENT_ID);
            commentViewModel = ViewModelProviders.of(this,new CommentViewModel(getActivity().getApplication(),commentId)).get(CommentViewModel.class);

            // Create the observer which updates the UI
            final Observer<Comment> commentObserver = comment -> {
                mComment = comment;
                updateUI();
            };

            // Observe the LiveData, pass this fragment as the LifecycleOwner and the observer
            commentViewModel.getComment(commentId).observe(this,commentObserver);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_comment, container, false);

        // Create reference to all attribute
        userCreatedImage = view.findViewById(R.id.comment_created_user_image);
        userCreatedName = view.findViewById(R.id.comment_user_name);
        commentDate = view.findViewById(R.id.comment_date);
        commentText = view.findViewById(R.id.comment_view_text);

        updateUI();

        return view;
    }

    private void updateUI() {
        // Put the data in the attributes
        if (mComment != null) {
            /****** Get user image ********/
            // Image
            if (mComment.getUserImage() == null || mComment.getUserImage().isEmpty()) {
                userCreatedImage.setImageResource(R.drawable.avatar);
            } else {
                Picasso.get().setIndicatorsEnabled(true);
                Model.loadImage(mComment.getUserImage(), imageFile -> {
                    // We will prepare a Target object for Picasso
                    target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            userCreatedImage.setImageBitmap(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    };
                    if (imageFile.exists()) {
                        // Load the image with Picasso from local file
                        Picasso.get().load(imageFile).into(target);
                    } else {
                            /*
                             TODO: This isn't a good way of working, but it solve some async problems
                             it helps when the app wasn't fast enough to download the  image.
                             If there's time we should figure out a way to remove the next line and
                             solve the problem.
                              */
                        // Try load the image with Picasso from remote
                        Picasso.get().load(mComment.getUserImage()).into(target);
                    }
                });
            }
            /****** Get user image ********/

            userCreatedName.setText(mComment.getUserCreated());
            commentDate.setText(mComment.getDate());
            commentText.setText(mComment.getComment());
        }
    }
}
