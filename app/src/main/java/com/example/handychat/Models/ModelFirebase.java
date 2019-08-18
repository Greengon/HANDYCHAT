package com.example.handychat.Models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

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
    public void addUser(User user,final Model.AddUserListener listener){
        db.collection("users").document(user.email)
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
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
                listener.onComplete(task.isSuccessful());
            }
        });
    }

    public void deleteJob(String jobId) {
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
                                db.collection("jobRequests").document(jobId).delete();
                                return;
                            }
                        }else{
                            Log.d("TAG","get failed with ", task.getException());
                        }
                    }
                });
    }

    public void UpdateJobRequest(JobRequest jobRequest,UpdateJobRequestListener listener){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", jobRequest.getId());
        result.put("date", jobRequest.getDate());
        result.put("address", jobRequest.getAddress());
        result.put("userCreated", jobRequest.getUserCreated());
        result.put("imageUrl", jobRequest.getImageUrl());
        result.put("description", jobRequest.getDescription());
        db.collection("jobRequests").document(jobRequest.getId()).update(result);
        listener.OnSuccess(true);
    }


    public interface GetJobRequestListener{
        void onComplete(JobRequest jobRequest);
    }

    public void getJobRequest(String jobId, final GetJobRequestListener listener){
        db.collection("jobRequests").document(jobId).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()){
                                JobRequest jobRequest = snapshot.toObject(JobRequest.class);
                                listener.onComplete(jobRequest);
                                return;
                            }
                        }else{
                            Log.d("TAG","get failed with ", task.getException());
                        }
                        listener.onComplete(null);
                    }
                });
    }

    // Testing only
    public static void getRemoteData(String jobId, final JobRequestRepository.GetJobRequestsListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
         db.collection("jobRequests").document(jobId).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()){
                                JobRequest jobRequest = snapshot.toObject(JobRequest.class);
                                listener.onComplete(jobRequest);
                                return;
                            }
                        }else{
                            Log.d("TAG","get failed with ", task.getException());
                        }
                        listener.onComplete(null);
                    }
                });
    }


    public void getAllJobRequest(getAllJobRequestListener listener) {
        db.collection("jobRequests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<JobRequest> data = new LinkedList<>();
                if (e != null) {
                    Log.d("TAG","Failed getting the comments from remote.");
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    JobRequest jobRequest = doc.toObject(JobRequest.class);
                    data.add(jobRequest);
                }
                listener.OnSuccess(data);
            }
        });
    }

    public interface getAllJobRequestListener {
        void OnSuccess(List<JobRequest> jobRequestList);
    }

    public interface UpdateJobRequestListener{
        void OnSuccess(boolean result);
    }
    /****************** JobRequest handling ********************/

    /****************** Comment handling ********************/
     public void addComment(Comment comment,final CommentRepository.AddCommentListener listener){
        db.collection("comments").document(comment.id)
                .set(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG","Model fire base addComment returned success");
                listener.onComplete(task.isSuccessful());
            }
        });
    }


    public interface GetCommentListener{
        void onComplete(Comment comment);
    }

    public void getComment(String commentId, final GetCommentListener listener){
        db.collection("comments").document(commentId).get().
                addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists()){
                                Comment comment = snapshot.toObject(Comment.class);
                                listener.onComplete(comment);
                                return;
                            }
                        }else{
                            Log.d("TAG","get failed with ", task.getException());
                        }
                        listener.onComplete(null);
                    }
                });
    }

    public void getAllCommentOfJob(String jobId,GetAllCommentListener listener) {
        db.collection("comments").whereEqualTo("job_request_id",jobId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("TAG","Failed getting the comments from remote.");
                    return;
                }
                LinkedList<Comment> data = new LinkedList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    Comment comment = doc.toObject(Comment.class);
                    data.add(comment);
                }
                listener.OnSuccess(jobId,data);
            }
        });
    }

    public interface GetAllCommentListener {
        void OnSuccess(String jobId, List<Comment> commentList);
    }

    public void deleteAllCommentForJob(String jobId) {
         db.collection("comments").whereEqualTo("job_request_id",jobId).addSnapshotListener(new EventListener<QuerySnapshot>() {
             @Override
             public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                 if (e != null) {
                     Log.d("TAG","Failed getting the comments from remote.");
                     return;
                 }
                 for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                     db.collection("comments").document(doc.getId()).delete();
                 }
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

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if(!task.isSuccessful()){
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    Uri downloadUri = task.getResult();
                    listener.onComplete(downloadUri.toString());
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }
    /******** Image saving *********/

    /******** Image loading *********/

    public void getImage(String url, final Model.GetImageListener listener){
        // Create a reference to the image
        StorageReference httpReference = storage.getReferenceFromUrl(url);
        final long ONE_MEGABYTE = 1024 * 1024;
        httpReference.getBytes(3 * ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap image = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                listener.onSuccess(image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG",e.getMessage());
                listener.onFail();
            }
        });
    }
    /******** Image loading *********/

    /******** Image deleting *********/
    private void deleteImageOfJob(String imageUrl) {
        // Create a reference to the image
        StorageReference httpReference = storage.getReferenceFromUrl(imageUrl);
        httpReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","Success in deleting the image");
            }
        });
    }
    /******** Image deleting *********/
}
