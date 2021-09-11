package com.jx.livestream;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jx.livestream.databinding.ProductShowPageBinding;

public class ProductShowPage extends AppCompatActivity {
    private RecyclerView productShowRecyclerView;
    private ProductShowAdapter productShowAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ProductShowPageBinding productShowPageBinding = ProductShowPageBinding.inflate(getLayoutInflater());
        setContentView(productShowPageBinding.getRoot());

        Log.e("product page","create");

        productShowRecyclerView = productShowPageBinding.productShowRecyclerView;

        productShowAdapter = new ProductShowAdapter(this);
        productShowRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        productShowRecyclerView.setAdapter(productShowAdapter);
    }
}
