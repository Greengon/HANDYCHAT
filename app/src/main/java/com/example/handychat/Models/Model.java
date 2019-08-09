package com.example.handychat.Models;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.URLUtil;

import com.example.handychat.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

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

    private String getLocalImageFileName(String url) {
        String name = URLUtil.guessFileName(url,null,null);
        return name;
    }
    /******** Image saving *********/
}
