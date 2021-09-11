package com.jx.livestream;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jx.livestream.databinding.ProductShowItemBinding;

import java.util.ArrayList;
import java.util.List;


public class ProductShowAdapter extends RecyclerView.Adapter<ProductShowAdapter.ProductShowItemViewHolder>{
    private Context context;
    private List<ProductModel> productModelList;

    public ProductShowAdapter(Context context) {
        this.context = context;
        productModelList = new ArrayList<>();
        for (int i=0; i<5; i++)
        {
            ProductModel productModel = new ProductModel();
            productModel.setTitle("直播摄像头");
            productModel.setDetails("用于直播使用，灵活方便！");
            productModelList.add(productModel);
        }

        Log.e("product size",productModelList.size()+"");
    }

    @NonNull
    @Override
    public ProductShowItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProductShowItemBinding itemBinding = DataBindingUtil.inflate(LayoutInflater.
                from(context),R.layout.product_show_item,parent,false);
        return new ProductShowItemViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductShowItemViewHolder holder, int position) {
        holder.productShowItemBinding.productTitleText.setText(productModelList.get(holder.getAdapterPosition()).getTitle());
        holder.productShowItemBinding.productDetailText.setText(productModelList.get(holder.getAdapterPosition()).getDetails());

        holder.productShowItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent jumpLivePage = new Intent(context,LivePage.class);
                context.startActivity(jumpLivePage);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productModelList.size();
    }

    public static class ProductShowItemViewHolder extends RecyclerView.ViewHolder {
        private ProductShowItemBinding productShowItemBinding;

        public ProductShowItemViewHolder(@NonNull ProductShowItemBinding productShowItemBinding) {
            super(productShowItemBinding.getRoot());
            this.productShowItemBinding = productShowItemBinding;
        }
    }
}
