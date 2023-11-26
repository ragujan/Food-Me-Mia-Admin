package com.rag.foodMeMiaAdmin.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.rag.foodMeMiaAdmin.databinding.ActivityViewFoodItemsBinding;
import com.rag.foodMeMiaAdmin.fragments.FoodItemDisplayFragment;
import com.rag.foodMeMiaAdmin.R;
import com.rag.foodMeMiaAdmin.adapters.AllFoodListAdapter;
import com.rag.foodMeMiaAdmin.domain.FoodDomainRetrieval;
import com.rag.foodMeMiaAdmin.helpers.FoodItemRetrievelViewModel;
import com.rag.foodMeMiaAdmin.util.Constants;
import com.rag.foodMeMiaAdmin.util.SortByOptions;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.FoodListRetrieval;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.Search;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

public class ViewFoodItemsActivity extends AppCompatActivity {
    private FoodItemRetrievelViewModel viewModel;
    ActivityViewFoodItemsBinding binding;
    boolean isTyping;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewFoodItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(FoodItemRetrievelViewModel.class);
        viewFoodItems();
        loadCategorySpinner();

        binding.searchTextField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println("hey " + editable.toString());
                String searchText = editable.toString();

                viewFoodItemsBySearch(searchText);
            }
        });
        binding.searchTextField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                isTyping = b;
                if (b) {
                    binding.closeIconTextView.setVisibility(View.VISIBLE);
                } else {
                    binding.closeIconTextView.setVisibility(View.GONE);
                }
            }
        });

        binding.closeIconTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchTextField.getText().clear();
                viewFoodItems();
                binding.searchTextField.clearFocus();
            }
        });


        binding.sortByCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                String selectedItem = parentView.getSelectedItem().toString();

                if (selectedItem.equals(SortByOptions.ALL.toString())) {
                    viewFoodItems();
                }
                if (selectedItem.equals(SortByOptions.AVAILABLE.toString())) {
                    viewAvailableFoodItems(true);
                }
                if (selectedItem.equals(SortByOptions.UNAVAILABLE.toString())) {
                    viewAvailableFoodItems(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });


        loadFragment(new FoodItemDisplayFragment());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("CheckResult")
    public void viewAvailableFoodItems(boolean status) {
        AllFoodListAdapter recyclerViewAdapter = new AllFoodListAdapter(new LinkedList<>());
        FoodListRetrieval.getAvailableFoodItemsOnly(recyclerViewAdapter, status)
                .subscribe(
                        resultsSet -> {

                            if (resultsSet.get(Constants.DATA_RETRIEVAL_STATUS).equals("Success")) {
                                List<FoodDomainRetrieval> foodDomainList = (List<FoodDomainRetrieval>) resultsSet.get("foodDomainList");
                                AllFoodListAdapter adapter = (AllFoodListAdapter) resultsSet.get("adapter");
                                Map<String, Object> map = new HashMap<>();
                                map.put("foodDomainList", foodDomainList);
                                map.put("adapter", adapter);
                                viewModel.setFoodItemsRetrieved(map);
                            }
                        },
                        throwable -> {

                        }
                );
    }

    @SuppressLint("CheckResult")
    public void viewFoodItemsBySearch(String searchText) {

        if (searchText.isEmpty()) return;
        AllFoodListAdapter recyclerViewAdapter = new AllFoodListAdapter(new LinkedList<>());
        Search.searchByText(recyclerViewAdapter, searchText).observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        resultsSet -> {

                            if (resultsSet.get(Constants.DATA_RETRIEVAL_STATUS).equals("Success")) {
                                List<FoodDomainRetrieval> foodDomainList = (List<FoodDomainRetrieval>) resultsSet.get("foodDomainList");
                                AllFoodListAdapter adapter = (AllFoodListAdapter) resultsSet.get("adapter");
                                Map<String, Object> map = new HashMap<>();
                                map.put("foodDomainList", foodDomainList);
                                map.put("adapter", adapter);
                                viewModel.setFoodItemsRetrieved(map);
                            }
                        },
                        throwable -> {
                            throwable.printStackTrace();
                        }
                );
    }

    @SuppressLint("CheckResult")
    public void viewFoodItems() {
        AllFoodListAdapter recyclerViewAdapter = new AllFoodListAdapter(new LinkedList<>());
        FoodListRetrieval.getAllFoods(recyclerViewAdapter)
                .subscribe(
                        resultsSet -> {

                            if (resultsSet.get(Constants.DATA_RETRIEVAL_STATUS).equals("Success")) {
                                List<FoodDomainRetrieval> foodDomainList = (List<FoodDomainRetrieval>) resultsSet.get("foodDomainList");
                                AllFoodListAdapter adapter = (AllFoodListAdapter) resultsSet.get("adapter");
                                Map<String, Object> map = new HashMap<>();
                                map.put("foodDomainList", foodDomainList);
                                map.put("adapter", adapter);
                                viewModel.setFoodItemsRetrieved(map);
                            }
                        },
                        throwable -> {

                        }
                );

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.foodItemViewFrameLayout, fragment.getClass(), null)
                .setReorderingAllowed(true)
                .commit();
    }

    public void loadCategorySpinner() {
        List<String> categoryNames = Stream.of(SortByOptions.values()).map(SortByOptions::name).collect(Collectors.toList());

        Spinner spinner = binding.sortByCategorySpinner;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNames);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }


}