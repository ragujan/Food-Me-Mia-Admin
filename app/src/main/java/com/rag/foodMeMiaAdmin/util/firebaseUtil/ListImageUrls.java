package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import static com.rag.foodMeMiaAdmin.util.Constants.FOOD_IMAGE_FOLDER_PATH;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListImageUrls {
    public static Single<List<Uri>> getAllImageUrls() {
        return Single.<List<Uri>>create(emitter -> {
            List<String> imageNames = new LinkedList<>();
            List<Uri> imageUrls = new LinkedList<>();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference listRef = storage.getReference().child(FOOD_IMAGE_FOLDER_PATH);
            listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {

                @SuppressLint("CheckResult")
                @Override
                public void onSuccess(ListResult listResult) {


                    for (StorageReference item : listResult.getItems()) {
                        imageNames.add(item.getName());
                    }
                    Observable.fromIterable(imageNames)
                            .flatMapSingle(imageName->getImageUrl(storageRef,imageName))
                            .toList()
                            .subscribe(
                               urls->{
                                   imageUrls.addAll(urls);
                                   emitter.onSuccess(imageUrls);
                               },
                               throwable -> emitter.onError(throwable)
                            );

//                    for (String imageName : imageNames) {
//                        final StorageReference storageReference = storageRef.child("foodImages/"+imageName);
//                          storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                              @Override
//                              public void onSuccess(Uri uri) {
//                                  Uri myUri = uri;
//                                  Log.d("Test uri ",myUri.toString());
//
//
//                              }
//                          });
//                    }

                }
            });


        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());

    }

    private static Single<Uri> getImageUrl(StorageReference storageRef, String imageName) {
        String path = FOOD_IMAGE_FOLDER_PATH + "/" + imageName;
        final StorageReference myRef = storageRef.child("foodImages/"+imageName);

        return Single.create(emitter -> {
            myRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
//                    System.out.println(uri.getHost() + " " + uri.getEncodedPath());
                    emitter.onSuccess(uri);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    emitter.onError(exception);
                }
            });
        });
    }
}
