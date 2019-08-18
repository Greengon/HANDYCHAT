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

/*
Model class is responsible only on image saving and loading
 */
public class Model {
    final public static Model instance = new Model();
    static ModelSql modelSql;
    static ModelFirebase modelFirebase;


    private Model(){
        modelSql = new ModelSql();
        modelFirebase = new ModelFirebase();
    }

    /******** Image saving *********/
    public interface SaveImageListener{
        void onComplete(String url);
    }

    public void saveImage(final Bitmap imageBitmap, final SaveImageListener viewListener){
        // This function is divided to two steps.
        // Step 1: save the image remotely
        modelFirebase.saveImage(imageBitmap, url ->  {
            // Step 2: saving the file locally
            String localName = URLUtil.guessFileName(url,null,null);
            saveImageToFile(imageBitmap, localName); // Synchronously save image locally
            Log.d("TAG","Cached image:" + localName);
            // Return the result url to the view to save for in imageUrl field
            viewListener.onComplete(url);
        });
    }

    // Next function will save the image locally
    private static void saveImageToFile(Bitmap imageBitmap, String imageFileName) {
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

            // Add the picture to the gallery so we don't need to manage the cache size
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            MyApplication.getContext().sendBroadcast(mediaScanIntent);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /******** Image saving *********/

    /******** Image loading *********/
    public interface GetImageListener {
        void onSuccess(File imageFile);
    }

    public static void loadImage(final String url, final GetImageListener listener){
        // We divide this function to 3 steps
        // Step 1: Try to find the image on the device, else download it.
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String localFileName = URLUtil.guessFileName(url,null,null);
        String path = folder.getAbsolutePath() + "/" + localFileName;
        File imageFile = new File(path);
        if (!imageFile.exists()){ // if image not found - try downloading it from FireBase
            modelFirebase.getImage(url, imageFromRemote -> {
                // Step 2: save the image to local database
                Log.d("TAG","save image to cache " + localFileName);
                saveImageToFile(imageFromRemote,localFileName);

                // Step 3: return the image using the listener
                listener.onSuccess(new File(localFileName));
            });
        }else{
            Log.d("TAG","OK reading cache image: " + localFileName);
            listener.onSuccess(imageFile);
        }
    }
    /******** Image loading *********/
}
