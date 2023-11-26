package com.rag.foodMeMiaAdmin.helpers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {
    private final MutableLiveData<Boolean> userRegisterSuccessful = new MutableLiveData<Boolean>();

    public void setUserRegisterSuccessful(Boolean state){
        userRegisterSuccessful.setValue(state);
    }
    public LiveData<Boolean> getUserRegisterSuccessful(){
        return userRegisterSuccessful;
    }
}
