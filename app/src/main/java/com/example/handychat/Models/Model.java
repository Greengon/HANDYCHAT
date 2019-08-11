package com.example.handychat.Models;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.handychat.MyApplication;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Model {
    final public static Model instance = new Model();
    ModelSql modelSql;
    ModelFirebase modelFirebase;

    private Model(){
        modelSql = new ModelSql();
        modelFirebase = new ModelFirebase();
    }

    /******** User handling **********/
    public interface AddUserListener{
        void onComplete(boolean success);
    }

    public void addUser(User user, AddUserListener listener){
        modelSql.addUser(user);
        modelFirebase.addUser(user,listener);
    }
    /******** User handling **********/

    /******** JobRequest handling **********/
    public interface AddJobRequestListener{
        void onComplete(boolean success);
    }

    public void addJobRequest(JobRequest jobRequest, AddJobRequestListener listener){
        modelSql.addJobRequest(jobRequest);
        modelFirebase.addJobRequest(jobRequest,listener);
    }

    public interface GetAllJobRequestListener{
        void onComplete(List<JobRequest> data);
    }

    public void getAllJobRequest(GetAllJobRequestListener listener) {
        modelFirebase.getAllJobRequest(listener);
    }

    public interface GetJobListener{
        void onComplete(JobRequest jobRequest);
    }

    public void getJobRequest(String jobId,GetJobListener listener) {
        // Lets first try to get it locally form SQLite
        JobRequest resultJobRequest = ModelSql.instance.getJobRequest(jobId);
        if (resultJobRequest == null){ // If we didn't find it locally we will download it from firebase
            modelFirebase.getJobRequest(jobId, new GetJobListener() {
                @Override
                public void onComplete(JobRequest jobRequest) {
                    modelSql.addJobRequest(jobRequest);
                }
            });
            resultJobRequest = ModelSql.instance.getJobRequest(jobId);
        }
        listener.onComplete(resultJobRequest);
    }
    /******** JobRequest handling **********/

    /******** Image saving *********/
    public interface SaveImageListener{
        void onComplete(String url);
        void fail();
    }

    public void saveImage(final Bitmap imageBitmap, final SaveImageListener listener){
        // This function is divided to three steps.
        // Step 1: save the image remotely
        modelFirebase.saveImage(imageBitmap, new SaveImageListener() {
            @Override
            public void onComplete(String url) {
                // Step 2: saving the file locally
                String localName = getLocalImageFileName(url);
                Log.d("TAG","cache image:" + localName);
                saveImageToFile(imageBitmap, localName); // Synchronously save image locally
                listener.onComplete(url);
            }

            @Override
            public void fail() {
                listener.fail();
            }
        });
    }

    // Next function will save the image locally
    private void saveImageToFile(Bitmap imageBitmap, String imageFileName) {
        try{
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!dir.exists()){
                dir.mkdir();
            }

            File imageFile = new File(dir, imageFileName);
            imageFile.createNewFile();

            OutputStream out = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100, out);
            out.close();

            addPictureToGallery(imageFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addPictureToGallery(File imageFile) {
        // Add the picture to the gallery so we don't need to manage the cache size
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        MyApplication.getContext().sendBroadcast(mediaScanIntent);
    }

    /******** Image saving *********/

    /******** Image loading *********/

    public void loadImage(final String url, final GetImageListener listener){
        // We divide this function to 3 steps
        // Step 1: Try to find the image on the device, else download it.
        String localFileName = getLocalImageFileName(url);
        Bitmap image = loadImageFormFile(localFileName);
        if (image == null){ // if image not found - try downloading it from parse
            modelFirebase.getImage(url, new GetImageListener() {
                @Override
                public void onSuccess(Bitmap image) {
                    // Step 2: save the image localy
                    String localFileName = getLocalImageFileName(url);
                    Log.d("TAG","save image to cache " + localFileName);
                    saveImageToFile(image,localFileName);

                    // Step 3: return the image using the listener
                    listener.onSuccess(image);
                }

                @Override
                public void onFail() {
                    listener.onFail();
                }
            });
        }else{
            Log.d("TAG","OK reading cache image: " + localFileName);
            listener.onSuccess(image);
        }
    }

    private Bitmap loadImageFormFile(String imageFileName){
        Bitmap bitmap = null;

        try {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File imageFile = new File(dir, imageFileName);
            InputStream inputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(inputStream);
            Log.d("TAG","got image from cache: " + imageFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public interface GetImageListener {
        void onSuccess(Bitmap image);
        void onFail();
    }

    /******** Image loading *********/

    private String getLocalImageFileName(String url) {
        String name = URLUtil.guessFileName(url,null,null);
        return name;
    }
}
