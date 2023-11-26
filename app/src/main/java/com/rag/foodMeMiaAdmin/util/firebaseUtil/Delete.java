package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rag.foodMeMiaAdmin.util.Constants;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Delete {
    public static Single<Map<String, Object>> deleteFile(String imageName) {
        return Single.<Map<String, Object>>create(emitter -> {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();

            Map<String,Object> resultData = new HashMap<>();

            if(imageName != null){
                System.out.println("previous image name "+imageName);
                StorageReference desertRef = storageRef.child(Constants.FOOD_IMAGE_FOLDER_PATH +"/"+imageName);

                System.out.println("download url from rag "+desertRef.toString());

                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully

                        resultData.put(Constants.DELETE_STATUS,"Success");
                        emitter.onSuccess(resultData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        emitter.onError(exception);
                    }
                });

            }

        }).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }



}
