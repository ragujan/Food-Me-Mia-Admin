package com.rag.foodMeMiaAdmin.util.firebaseUtil;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rag.foodMeMiaAdmin.domain.FoodDomain;
import com.rag.foodMeMiaAdmin.util.Constants;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UploadFoodItem {
    public static final Single<Map<String, Object>> observeFoodItemUploading(FoodDomain foodDomain) {
        return Single.<Map<String, Object>>create(emitter -> {
                    Map<String, Object> uploadedFoodItemData = new HashMap<>();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(Constants.FAST_FOOD_ITEMS_COLLECTION_NAME)
                            .document().set(foodDomain)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    uploadedFoodItemData.put(Constants.UPLOADED_STATUS_KEY,"success");
                                    emitter.onSuccess(uploadedFoodItemData);
                                    Log.d(TAG, "Food Item uploded successfully");
                                }
                            })
                    ;


                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
