package com.keiken.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class ImageController {

    public static File createVoidImageFile(){

        Uri uri = createImageUri();

        File photoFile = null;

        if (uri != null) {
            try {
                photoFile = new File(Objects.requireNonNull(uri.getPath()));
            } catch (Exception e) {
                Log.e("Error creating file: ", e.getMessage());
                e.printStackTrace();
            }
        }

        return photoFile;
    } //create voidImageFile at uri by createImageUri()

    private static Uri createImageUri() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "Keiken" + timeStamp;

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Keiken");

        storageDir.mkdir();

        return Uri.parse(storageDir.toString()+"/"+imageFileName+".jpg");
    } //create appImage dir and return uri for new imageFile


    public static Uri createImageFile(Bitmap bmp) {

        File outFile = createVoidImageFile();
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(outFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(outFile);
    } //create imageFile from bitmap using createVoidImageFile()

    public static class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        private DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public static void setProfilePic(ImageView photo) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        boolean externalProvider = false;
        if (user != null && user.getPhotoUrl() != null) {
            for (UserInfo info : user.getProviderData()) {
                if (info.getProviderId().equals("facebook.com")) {
                    new DownloadImageFromInternet(photo).execute(user.getPhotoUrl().toString() + "?type=large");
                    externalProvider = true;
                }
                if (info.getProviderId().equals("google.com")) {
                    new DownloadImageFromInternet(photo).execute(user.getPhotoUrl().toString() + "?sz=720");
                    externalProvider = true;
                }
            }
            if (!externalProvider)
                new DownloadImageFromInternet(photo).execute(user.getPhotoUrl().toString());
        }
    }

    public static void setProfilePic(ImageView photo, int size) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        boolean externalProvider = false;
        if (user != null && user.getPhotoUrl() != null) {
            for (UserInfo info : user .getProviderData()) {
                if (info.getProviderId().equals("facebook.com")) {
                    new DownloadImageFromInternet(photo).execute(user.getPhotoUrl().toString() + "?widht=" + size + "&height=" + size);
                    externalProvider = true;
                }
                if (info.getProviderId().equals("google.com")) {
                    new DownloadImageFromInternet(photo).execute(user.getPhotoUrl().toString() + "?sz=" + size);
                    externalProvider = true;
                }
            }
            if (!externalProvider)
                new DownloadImageFromInternet(photo).execute(user.getPhotoUrl().toString());
        }
    }
}