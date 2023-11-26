package com.rag.foodMeMiaAdmin.testing;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.rag.foodMeMiaAdmin.R;
import com.rag.foodMeMiaAdmin.domain.FastFoodCategory;
import com.rag.foodMeMiaAdmin.domain.FoodDomain;

import java.util.HashMap;
import java.util.Map;

public class FireStoreTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_store_test);
//        addData();
        viewData();
    }

    public void viewData(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("fastFoodItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                Log.d(TAG, documentSnapshot.getId()+" "+documentSnapshot.get("title"));
                            }
                        }
                    }
                });





    }
    public void addData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String description = "Very good highly designed";
        FoodDomain foodDomain = new FoodDomain("Chees Pizza 3",Double.parseDouble("200.00"),description,50,5, FastFoodCategory.PIZZA.toString(), false,"empty url");
        Map<String, Object> fastFoodItem = new HashMap<>();
        fastFoodItem.put("title",foodDomain.getTitle());
        fastFoodItem.put("price",foodDomain.getPrice());
        fastFoodItem.put("description",foodDomain.getDescription());
        fastFoodItem.put("preparationTime",foodDomain.getPreparationTime());
        fastFoodItem.put("calories",foodDomain.getCalories());
        fastFoodItem.put("category",foodDomain.getFastFoodCategory());

        db.collection("fastFoodItems")
                .add(fastFoodItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "reference id is "+ documentReference.getId());
                    };
                });
    }
}