package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rag.foodMeMiaAdmin.adapters.AllFoodListAdapter;
import com.rag.foodMeMiaAdmin.domain.FoodDomainRetrieval;
import com.rag.foodMeMiaAdmin.util.Constants;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FoodListRetrieval {
    public static final Single<Map<String, Object>> getAllFoods(AllFoodListAdapter allFoodListAdapter) {
        return Single.<Map<String, Object>>create(emitter -> {

            List<FoodDomainRetrieval> foodDomainList = new LinkedList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> dataResuts = new HashMap<>();

            db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME)
                    .addSnapshotListener(
                            new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(TAG, "Listen failed.", e);
                                        emitter.onError(e);
                                        return;
                                    }
                                    foodDomainList.clear();
                                    for (QueryDocumentSnapshot document : value) {
                                        FoodDomainRetrieval foodDomainRetrieval = (FoodDomainRetrieval) document.toObject(FoodDomainRetrieval.class);
                                        String id = document.getId();
                                        foodDomainRetrieval.setUniqueId(id);
                                        foodDomainList.add(foodDomainRetrieval);
                                    }
                                    dataResuts.put(Constants.DATA_RETRIEVAL_STATUS, "Success");
                                    dataResuts.put("foodDomainList", foodDomainList);
                                    foodDomainList.stream().forEach(data -> System.out.println("newly updated " + data.getTitle()));

                                    allFoodListAdapter.updateData(foodDomainList);

                                    dataResuts.put("adapter", allFoodListAdapter);

                                    emitter.onSuccess(dataResuts);
                                }
                            }

                    );

        }).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }
    public static final Single<Map<String, Object>> getAvailableFoodItemsOnly(AllFoodListAdapter allFoodListAdapter, boolean status) {
        return Single.<Map<String, Object>>create(emitter -> {

            List<FoodDomainRetrieval> foodDomainList = new LinkedList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> dataResults = new HashMap<>();

            db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME)
                    .whereEqualTo("available",status)
                    .addSnapshotListener(
                            new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value,
                                                    @Nullable FirebaseFirestoreException e) {
                                    if (e != null) {
                                        Log.w(TAG, "Listen failed.", e);
                                        emitter.onError(e);
                                        return;
                                    }
                                    foodDomainList.clear();
                                    for (QueryDocumentSnapshot document : value) {
                                        FoodDomainRetrieval foodDomainRetrieval = (FoodDomainRetrieval) document.toObject(FoodDomainRetrieval.class);
                                        String id = document.getId();
                                        foodDomainRetrieval.setUniqueId(id);
                                        foodDomainList.add(foodDomainRetrieval);
                                    }
                                    dataResults.put(Constants.DATA_RETRIEVAL_STATUS, "Success");
                                    dataResults.put("foodDomainList", foodDomainList);
                                    foodDomainList.stream().forEach(data -> System.out.println("newly updated " + data.getTitle()));
                                    allFoodListAdapter.updateData(foodDomainList);
                                    dataResults.put("adapter", allFoodListAdapter);

                                    emitter.onSuccess(dataResults);
                                }
                            }

                    );

        }).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());
    }

    public static final Single<Map<String, Object>> getSingleFoodItem(String documentId) {
        return Single.<Map<String, Object>>create(emitter -> {
                    Map<String, Object> dataMap = new HashMap<>();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME).document(documentId)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        DocumentSnapshot docSnapShot = task.getResult();
                                        DocumentReference docRef = task.getResult().getReference();

                                        FoodDomainRetrieval foodDomainRetrieval = (FoodDomainRetrieval) docSnapShot.toObject(FoodDomainRetrieval.class);

//                                        System.out.println("food title is "+foodDomainRetrieval.getTitle());
                                        dataMap.put("foodDomainRetrieval",foodDomainRetrieval);
                                        dataMap.put(Constants.DATA_RETRIEVAL_STATUS,"Success");
                                        emitter.onSuccess(dataMap);

                                    }else{
                                        Exception exception = task.getException();

                                        emitter.onError(exception);

                                    }
                                }
                            });

                })
                .observeOn(Schedulers.single())
                .subscribeOn(AndroidSchedulers.mainThread());
    }



}
