package com.w3engineers.ecommerce.bootic.ui.dashboard;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.data.helper.models.CategoryModel;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.ui.prductGrid.ProductGridActivity;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.util.Constants;

import java.util.ArrayList;

public class CategoryRecylerViewAdapter extends RecyclerView.Adapter<CategoryRecylerViewAdapter.ViewHolder>{

    private ArrayList<CategoryModel> categoryList;
    private Context context;

    public CategoryRecylerViewAdapter(ArrayList<CategoryModel> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recylerview_category, parent, false);

        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CategoryModel productCategory = categoryList.get(position);
        UIHelper.setThumbImageUriInView(holder.ivCategoryImage, productCategory.imageUrl);
        holder.tvCategoryName.setText(productCategory.title);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void addItem(ArrayList<CategoryModel> newList){
        for(CategoryModel products : newList){
            this.categoryList.add(products);
            notifyItemInserted(categoryList.size() - 1);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);

            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            CategoryModel productCategory = categoryList.get(position);
            Intent intent = new Intent(context, ProductGridActivity.class);
            intent.putExtra(Constants.SharedPrefCredential.INTENT_CATEGORY_ID, productCategory.id);
            context.startActivity(intent);

        }
    }

}
