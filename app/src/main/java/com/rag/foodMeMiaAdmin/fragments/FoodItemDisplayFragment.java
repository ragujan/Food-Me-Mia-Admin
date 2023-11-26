package com.rag.foodMeMiaAdmin.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.rag.foodMeMiaAdmin.R;
import com.rag.foodMeMiaAdmin.adapters.AllFoodListAdapter;
import com.rag.foodMeMiaAdmin.databinding.FragmentFoodItemDisplayBinding;
import com.rag.foodMeMiaAdmin.helpers.FoodItemRetrievelViewModel;


public class FoodItemDisplayFragment extends Fragment {
    private RecyclerView allFoodRecyclerView;
    private AllFoodListAdapter allFoodRecyclerViewAdapter;
    FragmentFoodItemDisplayBinding binding;
    private FoodItemRetrievelViewModel itemViewModel;

    public FoodItemDisplayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("on create, step 1");

    }

    @SuppressLint("CheckResult")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFoodItemDisplayBinding.inflate(getLayoutInflater());
        itemViewModel = new ViewModelProvider(requireActivity()).get(FoodItemRetrievelViewModel.class);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        itemViewModel.getFoodItemRetrieved().observe(getViewLifecycleOwner(), item -> {

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2, RecyclerView.VERTICAL, false);
            allFoodRecyclerView = getActivity().findViewById(R.id.recyclerViewFoodItemList);
            allFoodRecyclerView.setLayoutManager(gridLayoutManager);


            allFoodRecyclerViewAdapter = (AllFoodListAdapter) item.get("adapter");
            allFoodRecyclerView.setAdapter(allFoodRecyclerViewAdapter);


        });


        return binding.getRoot();
    }


}