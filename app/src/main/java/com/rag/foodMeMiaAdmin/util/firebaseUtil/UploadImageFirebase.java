package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import static com.rag.foodMeMiaAdmin.util.Constants.FOOD_IMAGE_FOLDER_PATH;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UploadImageFirebase {
    public static Single<Map<String,Object>> observeImageUploading(ImageView imageView, String uniqueImageName) {
        return Single.<Map<String,Object>>create(
                        emitter -> {
                            Map<String,Object> uploadedData = new HashMap<>();
                            imageView.setDrawingCacheEnabled(true);
                            imageView.buildDrawingCache();
                            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference myImageRef = storageRef.child(FOOD_IMAGE_FOLDER_PATH + "/" + uniqueImageName);


                            UploadTask uploadTask = myImageRef.putBytes(data);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    uploadedData.put("uploadStatus","fail");
                                    emitter.onSuccess(uploadedData);
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    myImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {

                                            System.out.println("download url is "+uri);
                                            uploadedData.put("url",uri);
                                            uploadedData.put("uploadStatus","success");
                                            emitter.onSuccess(uploadedData);
                                        }
                                    });

                                }
                            });
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

}
