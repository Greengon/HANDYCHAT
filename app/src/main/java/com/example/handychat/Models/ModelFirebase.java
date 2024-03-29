package com.example.handychat.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import androidx.annotation.NonNull;

public class ModelFirebase {
    FirebaseFirestore db;
    FirebaseStorage storage;

    public ModelFirebase(){
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
        storage = FirebaseStorage.getInstance();
    }


    /****************** User handling ********************/
    public void addUser(User user){
        db.collection("users").document(user.email)
                .set(user).addOnCompleteListener(task -> {
            Log.d("TAG","Finish adding user to the database");
        });
    }

    public void getUser(String userEmail, final UserRepository.GetUserListener listener){
        db.collection("users").whereEqualTo("email",userEmail).addSnapshotListener((queryDocumentSnapshots,error) -> {
            if (error != null) {
                Log.d("TAG","Failed getting the user from remote.");
                return;
            }
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                User user = doc.toObject(User.class);
                listener.onComplete(user);
                UserRepository.insert(user);
            }
        });
    }
    /****************** User handling ********************/

    /****************** JobRequest handling ********************/
    public void addJobRequest(JobRequest jobRequest,final JobRequestRepository.AddJobRequestListener listener){
        db.collection("jobRequests").document(jobRequest.id)
                .set(jobRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete();
            }
        });
    }

    public void deleteJob(String jobId,final JobRequestRepository.JobDeletedListener listener) {
        // Step 1: Find the job
        db.collection("jobRequests").document(jobId).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()){
                                JobRequest jobRequest = snapshot.toObject(JobRequest.class);
                                // Step 2: Delete the image
                                if (jobRequest.getImageUrl() != null && !jobRequest.getImageUrl().isEmpty()){
                                    deleteImageOfJob(jobRequest.imageUrl);
                                }else{
                                    Log.d("TAG","No image url was saved for this job");
                                }
                                // Step 3: Delete job
                                db.collection("jobRequests").document(jobId).delete().addOnSuccessListener(aVoid -> {
                                    listener.onComplete();
                                });
                            }
                        }else{
                            Log.d("TAG","get failed with ", task.getException());
                        }
                    }
                });
    }

    public void UpdateJobRequest(JobRequest jobRequest,final JobRequestRepository.UpdateJobRequestListener listener){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", jobRequest.getId());
        result.put("date", jobRequest.getDate());
        result.put("address", jobRequest.getAddress());
        result.put("userCreated", jobRequest.getUserCreated());
        result.put("imageUrl", jobRequest.getImageUrl());
        result.put("description", jobRequest.getDescription());
        db.collection("jobRequests").document(jobRequest.getId()).update(result);
        listener.onComplete();
    }

    public void UpdateComment(Comment comment, CommentRepository.UpdateCommentListener listener) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", comment.getId());
        result.put("date", comment.getDate());
        result.put("job_request_id", comment.getJobRequestId());
        result.put("userCreated", comment.getUserCreated());
        result.put("userImage", comment.getUserImage());
        result.put("comment", comment.getComment());
        db.collection("comments").document(comment.getId()).update(result);
        listener.onComplete();
    }

    public interface getAllJobRequestListener {
        void OnSuccess(List<JobRequest> jobRequestList);
    }

    public void getAllJobRequest(getAllJobRequestListener listener) {
        db.collection("jobRequests").addSnapshotListener((queryDocumentSnapshots,error) -> {
            LinkedList<JobRequest> data = new LinkedList<>();
            if (error != null) {
                Log.d("TAG","Failed getting the jobs from remote.");
                return;
            }
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                JobRequest jobRequest = doc.toObject(JobRequest.class);
                data.add(jobRequest);
            }
            listener.OnSuccess(data);
        });
    }

    /****************** JobRequest handling ********************/

    /****************** Comment handling ********************/
    public void addComment(Comment comment){
        db.collection("comments").document(comment.id)
                .set(comment).addOnCompleteListener(task ->  {
            Log.d("TAG","Model fire base addComment returned success");
        });
    }

    public interface GetAllCommentListener {
        void OnSuccess(List<Comment> commentList);
    }

    public void getAllCommentOfJob(String jobId,GetAllCommentListener listener) {
        db.collection("comments").whereEqualTo("job_request_id",jobId).addSnapshotListener((queryDocumentSnapshots,error) -> {
            if (error != null) {
                Log.d("TAG","Failed getting the comments from remote.");
                return;
            }
            LinkedList<Comment> data = new LinkedList<>();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                Comment comment = doc.toObject(Comment.class);
                data.add(comment);
            }
            listener.OnSuccess(data);
        });
    }

    public void deleteAllCommentForJob(String jobId) {
         db.collection("comments").whereEqualTo("job_request_id",jobId).addSnapshotListener((queryDocumentSnapshots,error) -> {
             if (error != null) {
                 Log.d("TAG","Failed getting the comments from remote.");
                 return;
             }
             for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                 db.collection("comments").document(doc.getId()).delete();
             }
         });

    }
    /****************** Comment handling ********************/

    /******** Image saving *********/

    public void saveImage(Bitmap imageBitmap, final Model.SaveImageListener listener) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to the image
        Date d = new Date();
        final StorageReference imageStorageRef = storageRef.child("image_" + d.getTime() + ".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageStorageRef.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(task -> {

            if(!task.isSuccessful()){
                throw task.getException();
            }

            // Continue with the task to get the download URL
            return imageStorageRef.getDownloadUrl();

        }).addOnCompleteListener( task -> {
            // Return the result url to the viewModel
            if (task.isSuccessful()){
                Uri downloadUri = task.getResult();
                listener.onComplete(downloadUri.toString());
            } else {
                listener.onComplete(null);
            }
        });
    }
    /******** Image saving *********/

    /******** Image loading *********/

    public void getImage(String url, final GetImageFromRemoteListener listener){
        // Create a reference to the image
        StorageReference httpReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpReference.getBytes(3 * ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            listener.onSuccess(image);
        }).addOnFailureListener(error ->{
            Log.d("TAG",error.getMessage());
        });
    }

    public interface GetImageFromRemoteListener{
        void onSuccess(Bitmap image);
    }
    /******** Image loading *********/

    /******** Image deleting *********/
    private void deleteImageOfJob(String imageUrl) {
        // Create a reference to the image
        StorageReference httpReference = storage.getReferenceFromUrl(imageUrl);
        httpReference.delete().addOnSuccessListener(aVoid ->  {
            Log.d("TAG","Success in deleting the image");
        });
    }
    /******** Image deleting *********/
}
