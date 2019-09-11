package com.keiken.controller;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

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
        bmp.compress(Bitmap.CompressFormat.JPEG, 25, outStream);
        outStream.flush();
        outStream.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

        return Uri.fromFile(outFile);
} //create imageFile from bitmap using createVoidImageFile()

    public static Uri createImageFileEsperienza(Bitmap bmp) {

        File outFile = createVoidImageFile();
        FileOutputStream outStream = null;

        try {
            outStream = new FileOutputStream(outFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 35, outStream); //valore quality agisce sul resize
            outStream.flush();
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Uri.fromFile(outFile);
    } //create imageFile from bitmap using createVoidImageFile()

    public static Uri createImageFileNoCompression(Bitmap bmp) {

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

        public DownloadImageFromInternet(ImageView imageView) {
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

    public static class SaveImageFromInternetToDB extends AsyncTask<String, Void, Bitmap> {

        Uri uri;

        public SaveImageFromInternetToDB(Uri uri) {
            this.uri = uri;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = uri.toString();
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
            uri = createImageFileNoCompression(result);
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(uri);
            Objects.requireNonNull(getApplicationContext()).sendBroadcast(mediaScanIntent);
            uploadProfileImage(uri);

        }
    }

    private static void uploadProfileImage(final Uri filePath) {
        //resize immagine before upload
        //Bitmap bitmap = getImageResized(getApplicationContext(), filePath);
        //Uri uriCompressed = createImageFile(bitmap);
        Uri uriCompressed = filePath;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();


        if (uriCompressed != null) {
            final StorageReference ref = storageReference.child("images/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/foto_profilo");
            ref.putFile(uriCompressed)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                            .setPhotoUri(uri).build();
                                    if (user != null) {
                                        user.updateProfile(profileUpdates);
                                    }
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });






            //UPDATE PHOTOURL
            final CollectionReference yourCollRef = db.collection("utenti");
            Query query = yourCollRef.whereEqualTo("id", mAuth.getCurrentUser().getUid());
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {

                        QuerySnapshot result = task.getResult();
                        try {
                            List<DocumentSnapshot> documents = result.getDocuments();
                            DocumentSnapshot document = documents.get(0);


                            Map<Object, String> map = new HashMap<>();
                            map.put("photoUrl", "images/" + Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()+"/foto_profilo");

                            yourCollRef.document(document.getId()).set(map, SetOptions.merge());


                        } catch (Exception e) {};
                    }
                }});
        }
    }


    public static void setProfilePic(ImageView photo) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user.getPhotoUrl() != null)
            new DownloadImageFromInternet(photo).execute(user.getPhotoUrl().toString());
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