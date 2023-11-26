package com.rag.foodMeMiaAdmin.views;

import static android.content.ContentValues.TAG;
import static com.rag.foodMeMiaAdmin.util.firebaseUtil.UniqueNameGenerationFirebase.observeUniqueName;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia;

import com.rag.foodMeMiaAdmin.databinding.ActivityUploadFoodItemBinding;
import com.rag.foodMeMiaAdmin.domain.FastFoodCategory;
import com.rag.foodMeMiaAdmin.domain.FoodDomain;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.UploadFoodItem;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.UploadImageFirebase;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UploadFoodItemActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    ActivityUploadFoodItemBinding binding;
    String uniqueImageName = "";

    String selectedCategoryName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadFoodItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia = registerForActivityResult(new PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                binding.chosenImage.setImageURI(uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });


        loadCategorySpinner();
        binding.chooseImageBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                // Launch the photo picker and let the user choose only images.
                pickMedia.launch(new PickVisualMediaRequest.Builder().setMediaType(PickVisualMedia.ImageOnly.INSTANCE).build());
                observeUniqueName().subscribe(uniqueName -> {
                    System.out.println("uniqueName is " + uniqueName);
                    uniqueImageName = uniqueName;

                }, throwable -> {

                });

            }
        });
        binding.uploadFoodBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {

                String title = binding.foodTitleField.getText().toString();
                String price = binding.foodPriceField.getText().toString();
                String description = binding.foodItemDescriptionTextField.getText().toString();
                String preparationTimeString = binding.preparationTimeTextField.getText().toString();
                String caloryCountString = binding.caloryCount.getText().toString();
                String category = binding.categorySpinner.getSelectedItem().toString();

//                validation process

                boolean isValid = true;
                StringBuilder validationMessage = new StringBuilder("Please fill in the required fields:\n");

                // Validate title
                if (title.isEmpty()) {
                    isValid = false;
                    validationMessage.append("- Title\n");
                }

// Validate price
                if (price.isEmpty()) {
                    isValid = false;
                    validationMessage.append("- Price\n");
                }

// Validate description
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
                if (category.isEmpty()) {
                    isValid = false;
                    validationMessage.append("- Category\n");
                }
                if(uniqueImageName.equals("") ){
                    isValid = false;
                    validationMessage.append("- error image uploading\n");
                }
                if(binding.chosenImage.getDrawable() == null ){
                    isValid = false;
                    validationMessage.append("- please select an image\n");
                }
                if (isValid) {
                    // Proceed to the next steps
                } else {
                    // Display validation error message
                    Toast.makeText(UploadFoodItemActivity.this, validationMessage.toString(), Toast.LENGTH_LONG).show();
                }

//                isValid = true;
                if ( isValid) {
                    UploadImageFirebase.observeImageUploading(binding.chosenImage,uniqueImageName)
                            .subscribe(
                                    uploadedData -> {
                                        if(uploadedData.get("uploadStatus").equals("success")){
                                            System.out.println("url is "+uploadedData.get("url"));


                                            FoodDomain foodDomain = new FoodDomain();
                                            foodDomain.setTitle(title);
                                            foodDomain.setPrice(Double.parseDouble(price));
                                            foodDomain.setImageUrl(uploadedData.get("url").toString());
                                            foodDomain.setAvailable(false);
                                            foodDomain.setCalories(Integer.parseInt(caloryCountString));
                                            foodDomain.setPreparationTime(Integer.parseInt(preparationTimeString));
                                            foodDomain.setFastFoodCategory(category);
                                            foodDomain.setDescription(description);

                                            UploadFoodItem.observeFoodItemUploading(foodDomain)
                                                    .subscribe(
                                                            uploadedFoodItemStatus->{
                                                                binding.foodTitleField.setText("");
                                                                binding.foodPriceField.setText("");
                                                                binding.foodItemDescriptionTextField.setText("");
                                                                binding.preparationTimeTextField.setText("");
                                                                binding.caloryCount.setText("");
                                                                binding.categorySpinner.setSelection(0);
                                                                uniqueImageName = "";
                                                                binding.chosenImage.setImageDrawable(null);
                                                                Toast.makeText(UploadFoodItemActivity.this, "Fast Food Items uploaded Successfully", Toast.LENGTH_SHORT).show();

                                                            },
                                                            throwable -> {

                                                            }

                                                    );



                                        }
                                    },
                                    throwable -> {

                                    }
                            );
                }else{
                    Toast.makeText(UploadFoodItemActivity.this, "Invalie details", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void loadCategorySpinner() {
        List<String> categoryNames = Stream.of(FastFoodCategory.values()).map(FastFoodCategory::name).collect(Collectors.toList());

        Spinner spinner = binding.categorySpinner;

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNames);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        adapterView.getSelectedItem().toString();
        System.out.println("hey hey");
        Log.d(TAG, "Selected Item View is " + adapterView.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}