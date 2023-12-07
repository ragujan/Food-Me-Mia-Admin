package com.rag.foodMeMiaAdmin.activity;

import static com.rag.foodMeMiaAdmin.util.firebaseUtil.UniqueNameGenerationFirebase.observeUniqueName;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.rag.foodMeMiaAdmin.databinding.ActivityUpdateFoodItemBinding;
import com.rag.foodMeMiaAdmin.domain.FastFoodCategory;
import com.rag.foodMeMiaAdmin.domain.FoodDomain;
import com.rag.foodMeMiaAdmin.domain.FoodDomainRetrieval;
import com.rag.foodMeMiaAdmin.util.Constants;
import com.rag.foodMeMiaAdmin.util.StringUtils;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.Delete;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.FoodListRetrieval;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.UpdateData;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.UploadImageFirebase;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class UpdateFoodItemActivity extends AppCompatActivity {
    private ActivityUpdateFoodItemBinding binding;

    private String foodItemDocumentId;

    private EditText foodTitle, foodPrice, foodDescription, preparationTime, calorieCount;
    private ImageView imageView;

    private List<String> categoryNames;
    private String uniqueImageName = "";
    private String olderImageUrl;
    private String olderImageName;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        binding = ActivityUpdateFoodItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        categoryNames = Stream.of(FastFoodCategory.values()).map(FastFoodCategory::name).collect(Collectors.toList());

        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                binding.chosenImageUpdate.setImageURI(uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        initView();

        loadData();

        binding.chooseImageUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build());
                observeUniqueName().subscribe(uniqueName -> {
                    System.out.println("uniqueName is " + uniqueName);
                    uniqueImageName = uniqueName;

                }, throwable -> {

                });
            }
        });

        binding.updateFoodBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {

                String description = binding.foodItemDescriptionTextFieldUpdate.getText().toString();
                String preparationTimeString = binding.preparationTimeTextFieldUpdate.getText().toString();
                String caloryCountString = binding.calorieCountUpdate.getText().toString();


                boolean isValid = true;
                StringBuilder validationMessage = new StringBuilder("Please fill in the required fields:\n");

//                if (binding.chosenImageUpdate.getDrawable() == null) {
//                    isValid = false;
//                    validationMessage.append("- please select an image\n");
//                }

                if (description.isEmpty()) {
                    isValid = false;
                    validationMessage.append("- Description\n");
                }

                // Validate preparation time
                if (preparationTimeString.isEmpty()) {
                    isValid = false;
                    validationMessage.append("- Preparation Time\n");
                } else {
                    try {
                        Integer preparationTime = Integer.parseInt(preparationTimeString);
                        if (preparationTime < 0) {
                            isValid = false;
                            validationMessage.append("- Preparation Time should be a positive integer\n");
                        }
                    } catch (NumberFormatException e) {
                        isValid = false;
                        validationMessage.append("- Invalid Preparation Time format\n");
                    }
                }

                // Validate calory count
                if (caloryCountString.isEmpty()) {
                    isValid = false;
                    validationMessage.append("- Calory Count\n");
                } else {
                    try {
                        Integer caloryCount = Integer.parseInt(caloryCountString);
                        if (caloryCount < 0) {
                            isValid = false;
                            validationMessage.append("- Calory Count should be a positive integer\n");
                        }
                    } catch (NumberFormatException e) {
                        isValid = false;
                        validationMessage.append("- Invalid Calory Count format\n");
                    }
                }

                if (isValid) {

                    if (uniqueImageName == "") {
//                        if the unique image name is empty which means, new image is not chosen, so no need to upload and update the image for the food item.

                        FoodDomain foodDomain = new FoodDomain();
                        foodDomain.setCalories(Integer.parseInt(caloryCountString));
                        foodDomain.setPreparationTime(Integer.parseInt(preparationTimeString));
                        foodDomain.setDescription(description);
                        foodDomain.setImageUrl(null);

                        UpdateData.updateFood(foodItemDocumentId, foodDomain)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        updateUpdateFoodStatus -> {


                                            if (updateUpdateFoodStatus.get(Constants.UPDATE_STATUS).equals("Success")) {


                                                binding.foodTitleFieldUpdate.setText("");
                                                binding.foodPriceFieldUpdate.setText("");
                                                binding.foodItemDescriptionTextFieldUpdate.setText("");
                                                binding.preparationTimeTextFieldUpdate.setText("");
                                                binding.calorieCountUpdate.setText("");
                                                binding.categorySpinnerUpdate.setSelection(0);
                                                uniqueImageName = "";
                                                binding.chosenImageUpdate.setImageDrawable(null);
                                                Toast.makeText(UpdateFoodItemActivity.this, "Fast Food Items updated Successfully", Toast.LENGTH_SHORT).show();
                                            }

                                        },
                                        throwable -> {

                                        }

                                );

                    } else {
//                        if the unique image name is not empty means, new image is chosen, so we have to update the image too
                        UploadImageFirebase.observeImageUploading(binding.chosenImageUpdate, uniqueImageName)
                                .subscribe(
                                        uploadedData -> {
                                            if (uploadedData.get("uploadStatus").equals("success")) {
                                                System.out.println("url is " + uploadedData.get("url"));
                                                FoodDomain foodDomain = new FoodDomain();
                                                foodDomain.setCalories(Integer.parseInt(caloryCountString));
                                                foodDomain.setPreparationTime(Integer.parseInt(preparationTimeString));
                                                foodDomain.setDescription(description);
                                                foodDomain.setImageUrl(uploadedData.get("url").toString());

                                                UpdateData.updateFood(foodItemDocumentId, foodDomain)
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(
                                                                updateUpdateFoodStatus -> {


                                                                    if (updateUpdateFoodStatus.get(Constants.UPDATE_STATUS).equals("Success")) {

                                                                        Delete.deleteFile(olderImageName).observeOn(AndroidSchedulers.mainThread()).subscribe(
                                                                                deletionStatus ->{
                                                                                    if(deletionStatus.get(Constants.DELETE_STATUS).equals("Success")){
                                                                                        binding.foodTitleFieldUpdate.setText("");
                                                                                        binding.foodPriceFieldUpdate.setText("");
                                                                                        binding.foodItemDescriptionTextFieldUpdate.setText("");
                                                                                        binding.preparationTimeTextFieldUpdate.setText("");
                                                                                        binding.calorieCountUpdate.setText("");
                                                                                        binding.categorySpinnerUpdate.setSelection(0);
                                                                                        uniqueImageName = "";
                                                                                        binding.chosenImageUpdate.setImageDrawable(null);
                                                                                        Toast.makeText(UpdateFoodItemActivity.this, "Fast Food Items updated Successfully", Toast.LENGTH_SHORT).show();
                                                                                        Intent intent = new Intent(UpdateFoodItemActivity.this,  ViewFoodItemsActivity.class);
                                                                                        startActivity(intent);
                                                                                    }else{
                                                                                    }

                                                                                },
                                                                                throwable -> {
                                                                                    throwable.printStackTrace();
                                                                                }

                                                                        );





                                                                    }

                                                                },
                                                                throwable -> {

                                                                }

                                                        );


                                            }
                                        },
                                        throwable -> {

                                        }
                                );
                    }


                } else {
                    Toast.makeText(UpdateFoodItemActivity.this, "Invalid details", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @SuppressLint("CheckResult")
    public void loadData() {
        Bundle extras = getIntent().getExtras();
        loadCategorySpinner();

        if (extras != null) {
            foodItemDocumentId = extras.getString("documentId");
            CompositeDisposable compositeDisposable = new CompositeDisposable();
            Disposable disposable = FoodListRetrieval.getSingleFoodItem(foodItemDocumentId).observeOn(AndroidSchedulers.mainThread()).subscribe(dataMap -> {
                if (dataMap.get(Constants.DATA_RETRIEVAL_STATUS).equals("Success")) {
                    FoodDomainRetrieval foodDomain = (FoodDomainRetrieval) dataMap.get("foodDomainRetrieval");
                    olderImageUrl = foodDomain.getImageUrl();

                    System.out.println("older image url is "+olderImageUrl);
                    olderImageName = StringUtils.getImageNameFromUrl(olderImageUrl);
                    System.out.println("older image name is "+ StringUtils.getImageNameFromUrl(olderImageUrl));
                    getBundle(foodDomain);
                } else {
                    System.out.println("error error wrong wrong");
                }
            }, throwable -> {
                throwable.printStackTrace();
            });
            compositeDisposable.add(disposable);

        }
    }

    private void getBundle(FoodDomainRetrieval foodDomain) {
        foodTitle.setText(foodDomain.getTitle());
        foodPrice.setText(foodDomain.getPrice().toString());
        foodDescription.setText(foodDomain.getDescription());
        calorieCount.setText(foodDomain.getCalories().toString());
        preparationTime.setText(foodDomain.getPreparationTime().toString());

        String category = foodDomain.getFastFoodCategory();


        Spinner spinner = binding.categorySpinnerUpdate;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        if (category != null) {
            int spinnerPosition = dataAdapter.getPosition(category);
            spinner.setSelection(spinnerPosition);
        }


        Glide.with(this).load(Uri.parse(foodDomain.getImageUrl())).into(binding.chosenImageUpdate);
    }

    public void loadCategorySpinner() {
        List<String> categoryNames = Stream.of(FastFoodCategory.values()).map(FastFoodCategory::name).collect(Collectors.toList());

        Spinner spinner = binding.categorySpinnerUpdate;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNames);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void initView() {

        foodTitle = binding.foodTitleFieldUpdate;
        foodPrice = binding.foodPriceFieldUpdate;
        foodDescription = binding.foodItemDescriptionTextFieldUpdate;
        calorieCount = binding.calorieCountUpdate;
        preparationTime = binding.preparationTimeTextFieldUpdate;
        imageView = binding.chosenImageUpdate;
        binding.foodTitleFieldUpdate.setEnabled(false);
        binding.foodPriceFieldUpdate.setEnabled(false);
        binding.categorySpinnerUpdate.setEnabled(false);

    }
}
