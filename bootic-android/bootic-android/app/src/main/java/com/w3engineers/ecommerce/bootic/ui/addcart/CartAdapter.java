package com.w3engineers.ecommerce.bootic.ui.addcart;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.database.DatabaseUtil;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.helper.response.InventoryResponse;
import com.w3engineers.ecommerce.bootic.data.provider.retrofit.RetrofitClient;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.NetworkHelper;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewModel> {

    private List<CustomProductInventory> inventoryList;
    private List<CustomProductInventory> prevCartList;
    private List<String> titleList = new ArrayList<>();
    ItemClickListener<CustomProductInventory> mListener;
    private StringBuilder attTitle;
    private Activity mActivity;
    private int productCounter = 0;
    private boolean canIncrease;

    public CartAdapter(List<CustomProductInventory> inventoryList, Activity mContext) {
        this.inventoryList = inventoryList;
        this.mActivity = mContext;
    }

    @NonNull
    @Override
    public CartViewModel onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_list_cart_product, viewGroup, false);
        return new CartViewModel(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewModel cartViewModel, int i) {

        CustomProductInventory productInventory = inventoryList.get(i);
        attTitle = new StringBuilder();
        if (productInventory != null) {
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            titleList = new Gson().fromJson(productInventory.attributeTitle, listType);

            //swipe layout
            //   cartViewModel.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
         //    cartViewModel.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right, cartViewModel.swipeLayout.findViewById(R.id.bottom_wrapper1));


            cartViewModel.textViewQuantity.setText("" + productInventory.currentQuantity);
            if (productInventory.attributeTitle != null && !productInventory.attributeTitle.isEmpty()) {
                for (String title : titleList) {
                    attTitle.append(title);
                }
            }
            if (titleList != null){
                cartViewModel.textViewSizeLable.setText(attTitle);
                cartViewModel.textViewSizeLable.setVisibility(View.VISIBLE);
            }else {
                cartViewModel.textViewSizeLable.setVisibility(View.GONE);

            }


            cartViewModel.textViewName.setText(productInventory.productName);

            cartViewModel.textViewPrice.setText(  "" + UtilityClass.getCurrencySymbolAndAmount(mActivity,productInventory.price));

            UIHelper.setThumbImageUriInView(cartViewModel.imageViewProductImage, productInventory.imageUri);

            //product increase
            cartViewModel.imageButtonUp.setOnClickListener(v -> {
                AsyncTask.execute(() -> {
                    productCounter = 0;
                    prevCartList = DatabaseUtil.on().getAllCodes();
                    for (CustomProductInventory customInventory : prevCartList) {
                        if (productInventory.inventory_id == customInventory.inventory_id) {
                            productCounter = productCounter + customInventory.currentQuantity;
                        }
                    }
                    if (productCounter < productInventory.available_qty) {
                        //   canIncrease = true;
                        productInventory.currentQuantity = productInventory.currentQuantity + 1;
                        mActivity.runOnUiThread(() -> {
                            cartViewModel.textViewQuantity.setText("" + productInventory.currentQuantity);
                            mListener.onItemClick(cartViewModel.textViewQuantity, productInventory, i);
                        });
                        AsyncTask.execute(() -> {
                            DatabaseUtil.on().updateQuantity(productInventory.currentQuantity, productInventory.id);
                        });

                    } else {
                        mActivity.runOnUiThread(() -> Toast.makeText(mActivity,
                                mActivity.getResources().getString(R.string.inventory_exceed), Toast.LENGTH_SHORT).show());
                        canIncrease = false;
                    }
                });
            });

            //product reduce
            cartViewModel.imageButtonDown.setOnClickListener(v -> {
                if (productInventory.currentQuantity > 1) {
                    productInventory.currentQuantity = productInventory.currentQuantity - 1;
                    cartViewModel.textViewQuantity.setText("" + productInventory.currentQuantity);
                    mListener.onItemClick(cartViewModel.textViewQuantity, productInventory, i);
                    AsyncTask.execute(() -> {
                        DatabaseUtil.on().updateQuantity(productInventory.currentQuantity, productInventory.id);
                    });
                    // productCounter = 0;
                } else {
                    Toast.makeText(mActivity, mActivity.getResources().getString(R.string.mini_quantity_one), Toast.LENGTH_SHORT).show();
                }
            });

           cartViewModel.itemView.setOnClickListener(v -> {
                //confirm activity
            });

            cartViewModel.btnDelete.setOnClickListener(
                    onDeleteListener(cartViewModel.getAdapterPosition(), cartViewModel, productInventory));
        }

    }

   //delete items
   private View.OnClickListener onDeleteListener(final int position, final CartViewModel customViewHolder,
                                                 CustomProductInventory productInventory) {
        return v -> {
            openDialog(v,position,productInventory);

        };
    }


    private void openDialog(View v, int position,CustomProductInventory productInventory) {
        new AlertDialog.Builder(mActivity)
                .setMessage(mActivity.getResources().getString(R.string.remove_item))
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    deleteItemFromServer(v,productInventory,position,dialog);

                })
                .setNegativeButton(android.R.string.no,(dialog, which)->{
                    dialog.dismiss();
                } )
                .show();
    }

    /**
     * Delete item from server
     */
    private void deleteItemFromServer(View v, CustomProductInventory productInventory, int position, DialogInterface dialog){
        getRemovedFromCartResponse(v,mActivity,""+productInventory.product_id,
                ""+productInventory.inventory_id,productInventory,position,dialog);
    }


    /***
     * Remove from cart to server
     * @param context context
     * @param productId product Id
     * @param inventoryId inventory Id
     */
    public void getRemovedFromCartResponse(View v,final Context context, String productId, String inventoryId,
                                           CustomProductInventory productInventory,int position, DialogInterface dialog){
        String userId = CustomSharedPrefs.getLoggedInUserId(context);
        if (NetworkHelper.hasNetworkAccess(context)) {
            RetrofitClient.getApiService().getRemovedFromCartResponse(Constants.ServerUrl.API_TOKEN,inventoryId
                    , userId,productId).enqueue(new Callback<InventoryResponse>() {
                @Override
                public void onResponse(@NonNull Call<InventoryResponse> call, @NonNull Response<InventoryResponse> response) {
                    if (response.body() != null) {
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                DatabaseUtil.on().deleteCartProduct(productInventory.product_id,productInventory.inventory_id);
                                inventoryList.remove(position);
                                mListener.onItemClick(v, productInventory,
                                        CartActivity.CLICK_CART_DELETE);

                            }
                        });
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });

                    }
                }

                @Override
                public void onFailure(@NonNull Call<InventoryResponse> call, @NonNull Throwable t) {
                    if (t.getMessage() !=null){
                        Toast.makeText(mActivity,t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(mActivity,mActivity.getResources().getString(R.string.check_net_connection), Toast.LENGTH_LONG).show();
        }
    }



    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public void addItem(List<CustomProductInventory> newList) {
        if (newList != null) {
            for (CustomProductInventory list : newList) {
                this.inventoryList.add(list);
                notifyItemInserted(inventoryList.size() - 1);
            }
        }
    }

    public void setItemClickListener(ItemClickListener<CustomProductInventory> listener) {
        this.mListener = listener;
    }

    public List<CustomProductInventory> getItems() {
        return inventoryList;
    }

    public class CartViewModel extends RecyclerView.ViewHolder {
        private ImageButton btnDelete;
        private SwipeRevealLayout swipeLayout;
        private ImageView imageViewProductImage;
        private TextView textViewName, textViewPrice, textViewQuantity, textViewSizeLable;
        private ImageButton imageButtonUp, imageButtonDown;
        private LinearLayout linearLayoutColorSize;

        public CartViewModel(@NonNull View itemView) {
            super(itemView);

            //swipe layout
              swipeLayout = itemView.findViewById(R.id.swipe);
              btnDelete = itemView.findViewById(R.id.btndelete);

            imageViewProductImage = itemView.findViewById(R.id.iv_cart_product_image);
            textViewName = itemView.findViewById(R.id.tv_cart_product_heading);
            textViewSizeLable = itemView.findViewById(R.id.tv_cart_product_1);
            textViewPrice = itemView.findViewById(R.id.tv_cart_product_price);
            imageButtonUp = itemView.findViewById(R.id.image_button_up);
            imageButtonDown = itemView.findViewById(R.id.image_button_down);
            linearLayoutColorSize = itemView.findViewById(R.id.linear_layout_color_size);
            textViewQuantity = itemView.findViewById(R.id.btn_cart_product_quantity);
        }
    }

}
