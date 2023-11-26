package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
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

public class Search {

    public static Single<Map<String, Object>> searchByText(AllFoodListAdapter allFoodListAdapter, String text) {
        return Single.<Map<String, Object>>create(emitter -> {
            List<FoodDomainRetrieval> foodDomainList = new LinkedList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> dataResults = new HashMap<>();


            db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME)
                    .where(Filter.or(
                            Filter.greaterThanOrEqualTo("title", text),
                            Filter.greaterThanOrEqualTo("description", text)
                    ))
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
                                        System.out.println("search title food is " + foodDomainRetrieval.getTitle());

                                    }
                                    System.out.println("food is found ");
                                    dataResults.put(Constants.DATA_RETRIEVAL_STATUS, "Success");
                                    dataResults.put("foodDomainList", foodDomainList);
                                    foodDomainList.stream().forEach(data -> System.out.println("searched foods are " + data.getTitle()));
                                    allFoodListAdapter.updateData(foodDomainList);
                                    dataResults.put("adapter", allFoodListAdapter);

                                    emitter.onSuccess(dataResults);
                                }
                            }

                    );

        }).observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread());

    }

}
