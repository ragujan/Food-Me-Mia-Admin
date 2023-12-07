package com.rag.foodMeMiaAdmin.domain;

public class FoodDomain {

    private String title;
    private Double price;
    private String description;
    private Integer preparationTime;
    private Integer calories;
    private String fastFoodCategory;

    private Boolean isAvailable;

    private String imageUrl;
    private String added_at;
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public FoodDomain() {
    }

    public FoodDomain(String title, Double price, String description, Integer preparationTime, Integer calories, String fastFoodCategory, Boolean isAvailable, String imageUrl) {
        this.title = title;
        this.price = price;
        this.description = description;
        this.preparationTime = preparationTime;
        this.calories = calories;
        this.fastFoodCategory = fastFoodCategory;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
    }

    public String getAdded_at() {
        return added_at;
    }

    public void setAdded_at(String added_at) {
        this.added_at = added_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public Integer getCalories() {
        return calories;
    }

    public void setCalories(Integer calories) {
        this.calories = calories;
    }

    public String getFastFoodCategory() {
        return fastFoodCategory;
    }

    public void setFastFoodCategory(String fastFoodCategory) {
        this.fastFoodCategory = fastFoodCategory;
    }
}
