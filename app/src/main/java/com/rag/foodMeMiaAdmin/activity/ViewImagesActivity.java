package com.rag.foodMeMiaAdmin.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.rag.foodMeMiaAdmin.R;
import com.rag.foodMeMiaAdmin.adapters.ImageListAdapter;
import com.rag.foodMeMiaAdmin.databinding.ActivityViewImagesBinding;
import com.rag.foodMeMiaAdmin.domain.ImageListDomain;
import com.rag.foodMeMiaAdmin.util.firebaseUtil.ListImageUrls;

import java.util.LinkedList;
import java.util.List;

public class ViewImagesActivity extends AppCompatActivity {

    List<String> imageNames = new LinkedList<>();
    ActivityViewImagesBinding binding;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter recyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewImagesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String url =
                "https://firebasestorage.googleapis.com/v0/b/fir-authenticationtest-af44e.appspot.com/o/foodImages%2Fai_zoom_pic.jpg?alt=media&token=281a1603-600c-49d4-8be6-877b4c7d8146";
        getImages();
    }
   @SuppressLint("CheckResult")
   public void getImages(){
       ListImageUrls.getAllImageUrls().subscribe(

             imageNames->{
                 imageNames.stream().forEach(e-> System.out.println("urls are "+e));
                 GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewImagesActivity.this, 2, RecyclerView.VERTICAL,false);
                 recyclerView  = findViewById(R.id.recyclerViewListAllImages);
                 recyclerView.setLayoutManager(gridLayoutManager);

                 List<ImageListDomain> imageListDomainList = new LinkedList<>();

                 imageNames.forEach(e->imageListDomainList.add(new ImageListDomain(e)));


                recyclerViewAdapter = new ImageListAdapter(imageListDomainList);
                recyclerView.setAdapter(recyclerViewAdapter);



             }

       );
   }
}