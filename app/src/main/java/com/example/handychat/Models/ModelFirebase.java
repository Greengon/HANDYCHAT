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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.LinkedList;

import javax.annotation.Nullable;

import androidx.annotation.NonNull;

public class ModelFirebase {
    FirebaseFirestore db;

    public ModelFirebase(){
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build();
        db.setFirestoreSettings(settings);
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
    public void addJobRequest(JobRequest jobRequest,final Model.AddJobRequestListener listener){
        db.collection("jobRequests").document(jobRequest.id)
                .set(jobRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onComplete(task.isSuccessful());
            }
        });
    }


    public void getJobRequest(String jobId, final Model.GetJobListener listener){
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

    public void getAllJobRequest(final Model.GetAllJobRequestListener listener) {
        db.collection("jobRequests").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                LinkedList<JobRequest> data = new LinkedList<>();
                if (e != null) {
                    listener.onComplete(data);
                    return;
                }
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    JobRequest jobRequest = doc.toObject(JobRequest.class);
                    data.add(jobRequest);
                }
                listener.onComplete(data);
            }
        });
    }
    /****************** JobRequest handling ********************/

    /******** Image saving *********/

    public void saveImage(Bitmap imageBitmap, final Model.SaveImageListener listener) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

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
        FirebaseStorage storage = FirebaseStorage.getInstance();
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
}
