package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rag.foodMeMiaAdmin.domain.FoodDomain;
import com.rag.foodMeMiaAdmin.util.Constants;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UpdateData {
    public static Single<Map<String, Object>> updateFoodStatus(String id, boolean statusTo) {
        return Single.<Map<String, Object>>create(emitter -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference foodItems = db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME);
                    db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME).document(id)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                            System.out.println("from update title is " + document.get("title").toString());
                                            Map<String, Object> map = new HashMap<>();
                                            DocumentReference docRef = document.getReference();
                                            Boolean status = (Boolean) document.get("available");


                                            docRef.update("available", !status)
                                                    .addOnSuccessListener(
                                                            new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    map.put(Constants.UPDATE_STATUS, "Success");
                                                                    System.out.println("update success");
                                                                    emitter.onSuccess(map);
                                                                }
                                                            }
                                                    ).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);

                                                        }
                                                    });


                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });


                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

    public static Single<Map<String, Object>> updateFood(String id, FoodDomain foodDomain) {
        return Single.<Map<String, Object>>create(emitter -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    CollectionReference foodItems = db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME);
                    db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME).document(id)
                            .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Map<String, Object> map = new HashMap<>();
                                            DocumentReference docRef = document.getReference();
                                            String imageUrl = task.getResult().get("imageUrl").toString();

                                            if (foodDomain.getImageUrl() != null) {
                                                imageUrl = foodDomain.getImageUrl();
                                            }
//                                            System.out.println("new image url is " + imageUrl);
//                                            map.put(Constants.UPDATE_STATUS, "Success");
//                                            System.out.println("update success");
//                                            emitter.onSuccess(map);
                                            docRef.update("description", foodDomain.getDescription(), "imageUrl", imageUrl, "preparationTime", foodDomain.getPreparationTime(), "calories", foodDomain.getCalories())
                                                    .addOnSuccessListener(
                                                            new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {

                                                                    map.put(Constants.UPDATE_STATUS, "Success");
                                                                    System.out.println("update success");
                                                                    emitter.onSuccess(map);
                                                                }
                                                            }
                                                    ).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error updating document", e);
                                                            emitter.onError(e);

                                                        }
                                                    });


                                        } else {
                                            Log.d(TAG, "No such document");
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });


                })
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread());
    }

}
