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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Search {
    private static Set<FoodDomainRetrieval> addUniqueFoodRetrieval(Set<FoodDomainRetrieval> set, FoodDomainRetrieval foodDomainRetrieval) {
        boolean isFound = false;
        Iterator<FoodDomainRetrieval> iterator = set.iterator();

        while (iterator.hasNext()) {
            FoodDomainRetrieval food = iterator.next();
            if(food.getUniqueId().equals(foodDomainRetrieval.getUniqueId())){
                isFound = true;
                break;
            }
        }

        if(!isFound){
            set.add(foodDomainRetrieval);
        }

        return set;
    }
    public static Single<Map<String, Object>> searchByText(AllFoodListAdapter allFoodListAdapter, String text) {
        return Single.<Map<String, Object>>create(emitter -> {
            List<FoodDomainRetrieval> foodDomainList = new LinkedList<>();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            Map<String, Object> dataResults = new HashMap<>();
            Set<FoodDomainRetrieval> foodDomainRetrievalSet = new HashSet<>();


            db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME)
                    .orderBy("title")
                    .where(Filter.or(
                                    Filter.greaterThanOrEqualTo("title", text)
                            )

                    )
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
                                        addUniqueFoodRetrieval(foodDomainRetrievalSet,foodDomainRetrieval);

                                    }

                                }
                            }

                    );

            db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME)
                    .orderBy("description")
                    .where(Filter.or(
                                    Filter.greaterThanOrEqualTo("description", text)
                            )

                    )
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
                                        addUniqueFoodRetrieval(foodDomainRetrievalSet,foodDomainRetrieval);

                                    }
                                    dataResults.put(Constants.DATA_RETRIEVAL_STATUS, "Success");
                                    foodDomainList.clear();
                                    foodDomainList.addAll(foodDomainRetrievalSet);
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
