package com.rag.foodMeMiaAdmin.domain;

import android.net.Uri;

public class ImageListDomain {
    private Uri imageUrl;

    public ImageListDomain(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }
}
