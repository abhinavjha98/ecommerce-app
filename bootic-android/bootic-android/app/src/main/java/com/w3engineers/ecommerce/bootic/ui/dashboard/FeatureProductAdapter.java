package com.w3engineers.ecommerce.bootic.ui.dashboard;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductModel;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;
import com.w3engineers.ecommerce.bootic.ui.productdetails.ProductDetailsActivity;

import java.util.ArrayList;

public class FeatureProductAdapter extends RecyclerView.Adapter<FeatureProductAdapter.FeatureViewHolder> {


    private ArrayList<ProductModel> productList;
    private Activity context;
    public ItemClickListener<ProductModel> mListener;


    public FeatureProductAdapter(ArrayList<ProductModel> categoryList, Activity context ) {
        this.productList = categoryList;
        this.context = context;
    }


    @NonNull
    @Override
    public FeatureViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_feature_product, viewGroup, false);
        return new FeatureViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeatureViewHolder featureViewHolder, int i) {
        ProductModel productModel = productList.get(i);
        if (productModel != null) {
            featureViewHolder.buttonFavourite.setLiked(productModel.isFavourite == 1);
            Glide.with(context).load(Constants.ServerUrl.THUMB_IMAGE_URL + productModel.imageUri).into(featureViewHolder.imageView);
            featureViewHolder.textViewName.setText(productModel.title == null ? "" : productModel.title);
            String currency = CustomSharedPrefs.getCurrency(context);
            String lenCountOfCurrencyAndPrePrice = currency + productModel.previousPrice;
            if (productModel.previousPrice > 0) {
                featureViewHolder.textViewPrevPrice.setVisibility(View.VISIBLE);
                if (lenCountOfCurrencyAndPrePrice.length() > 6) {
                    //bottom

                    featureViewHolder.textViewPrevPrice.setText("" + UtilityClass.getCurrencySymbolAndAmount(context,productModel.previousPrice));

                    featureViewHolder.textViewPrevPrice.setVisibility(View.VISIBLE);
                    featureViewHolder.textViewPrevPriceRight.setVisibility(View.GONE);
                } else {
                    //right
                    featureViewHolder.textViewPrevPrice.setVisibility(View.GONE);
                    featureViewHolder.textViewPrevPriceRight.setVisibility(View.VISIBLE);
                    featureViewHolder.textViewPrevPriceRight.setText(lenCountOfCurrencyAndPrePrice+"");
                }


            } else {
                featureViewHolder.textViewPrevPrice.setVisibility(View.GONE);
            }

            featureViewHolder.textViewCurrentPrice.setText(""+UtilityClass.getCurrencySymbolAndAmount(context, productModel.currentPrice));

        }

    }

    public void addItem(ArrayList<ProductModel> newList) {
        for (ProductModel products : newList) {
            this.productList.add(products);
            notifyItemInserted(productList.size() - 1);
        }
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }



    public void setItemClickedListener(ItemClickListener<ProductModel> itemClickedListener){
        this.mListener = itemClickedListener;
    }



    public class FeatureViewHolder extends RecyclerView.ViewHolder implements OnLikeListener, View.OnClickListener{
        ImageView imageView;
        TextView textViewName, textViewCurrentPrice, textViewPrevPrice,textViewPrevPriceRight;
        public LikeButton buttonFavourite;

        public FeatureViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_grid_product_image);
            textViewName = itemView.findViewById(R.id.tv_grid_product_heading);
            textViewCurrentPrice = itemView.findViewById(R.id.tv_grid_product_price);
            textViewPrevPrice = itemView.findViewById(R.id.tv_grid_product_Previous_price);
            textViewPrevPriceRight = itemView.findViewById(R.id.tv_grid_product_Previous_price_right);
            buttonFavourite = itemView.findViewById(R.id.btn_favourite);
            buttonFavourite.setOnLikeListener(this);
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            ProductModel product = productList.get(position);
            Intent intent = new Intent(context, ProductDetailsActivity.class);
            intent.putExtra(Constants.SharedPrefCredential.PRODUCT_DETAIL_INTENT, ""+product.id);
            context.startActivity(intent);
        }

        @Override
        public void liked(LikeButton likeButton) {
            if (CustomSharedPrefs.getLoggedInUser(context) != null) {
                int position = getAdapterPosition();
                likeButton.setLiked(false);
                ProductModel currentProduct = productList.get(position);
                mListener.onItemClick(buttonFavourite, currentProduct, position);

            } else {
                UIHelper.openSignInPopUp(context);
                likeButton.setLiked(false);
            }
        }

        @Override
        public void unLiked(LikeButton likeButton) {
            int position = getAdapterPosition();
            ProductModel currentProduct = productList.get(position);
            mListener.onItemClick(buttonFavourite, currentProduct, position);
        }
    }

}
