package com.rag.foodMeMiaAdmin.domain;

public class FoodDomainRetrieval extends FoodDomain{

    private String uniqueId;
    private String title;
    private String price;
    private String description;
    private Integer preparationTime;
    private Integer calories;
    private String fastFoodCategory;

    private Boolean isAvailable;

    private String imageUrl;

    public FoodDomainRetrieval() {
    }

    public FoodDomainRetrieval(String uniqueId, String title, String price, String description, Integer preparationTime, Integer calories, String fastFoodCategory, Boolean isAvailable, String imageUrl) {
        this.uniqueId = uniqueId;
        this.title = title;
        this.price = price;
        this.description = description;
        this.preparationTime = preparationTime;
        this.calories = calories;
        this.fastFoodCategory = fastFoodCategory;
        this.isAvailable = isAvailable;
        this.imageUrl = imageUrl;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }


}
