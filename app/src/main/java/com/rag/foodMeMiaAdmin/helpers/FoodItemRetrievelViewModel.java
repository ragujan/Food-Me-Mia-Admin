package com.rag.foodMeMiaAdmin.helpers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Map;

public class FoodItemRetrievelViewModel extends ViewModel {
    private final MutableLiveData<Map<String, Object>> foodItemsRetrieved = new MutableLiveData<>();

    public void setFoodItemsRetrieved(Map<String, Object> data){
        foodItemsRetrieved.postValue(data);
    }

    public LiveData<Map<String, Object>> getFoodItemRetrieved(){
        return foodItemsRetrieved;
    }



}
