package com.rag.foodMeMiaAdmin.testing;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.rag.foodMeMiaAdmin.databinding.ActivityTestBinding;

public class TestActivity extends AppCompatActivity {
    ActivityTestBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}
