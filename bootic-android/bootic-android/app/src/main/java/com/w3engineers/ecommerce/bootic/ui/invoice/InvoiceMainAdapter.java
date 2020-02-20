package com.w3engineers.ecommerce.bootic.ui.invoice;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.models.CustomProductInventory;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class InvoiceMainAdapter extends RecyclerView.Adapter<InvoiceMainAdapter.CartViewModel> {

    private List<CustomProductInventory> inventoryList;
    private List<String> titleList = new ArrayList<>();
    private StringBuilder attTitle;
    private Activity mActivity;


    public InvoiceMainAdapter(List<CustomProductInventory> inventoryList, Activity mContext) {
        this.inventoryList = inventoryList;
        this.mActivity = mContext;
    }

    @NonNull
    @Override
    public InvoiceMainAdapter.CartViewModel onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_invoice_main, viewGroup, false);
        return new InvoiceMainAdapter.CartViewModel(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceMainAdapter.CartViewModel cartViewModel, int i) {

        CustomProductInventory productInventory = inventoryList.get(i);
        attTitle = new StringBuilder();
        if (productInventory != null) {
            Type listType = new TypeToken<ArrayList<String>>() {
            }.getType();
            titleList = new Gson().fromJson(productInventory.attributeTitle, listType);


            cartViewModel.textViewQuantity.setText("" + productInventory.currentQuantity);
            if (productInventory.attributeTitle != null && !productInventory.attributeTitle.isEmpty()) {
                for (String title : titleList) {
                    attTitle.append(title.trim());
                    if (!title.contains("Size:")){
                        attTitle.append(mActivity.getResources().getString(R.string.coma));
                    }
                }
            }
            if (titleList != null){
                cartViewModel.textViewAttribute.setText(attTitle);
                cartViewModel.textViewAttribute.setVisibility(View.VISIBLE);
            }else {
                cartViewModel.textViewAttribute.setVisibility(View.GONE);

            }
            cartViewModel.textViewName.setText(productInventory.productName);
            cartViewModel.textViewPrice.setText(UtilityClass.getCurrencySymbolAndAmount(mActivity,productInventory.price));

            cartViewModel.textViewQuantity.setText(""+productInventory.currentQuantity);

            double price = Double.valueOf(productInventory.currentQuantity) * Double.valueOf( productInventory.price);
            String currency = CustomSharedPrefs.getCurrency(mActivity);
            cartViewModel.textViewTotalAmount.setText(UtilityClass.getCurrencySymbolAndAmount(mActivity,(float) price));
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


    public List<CustomProductInventory> getItems() {
        return inventoryList;
    }

    public class CartViewModel extends RecyclerView.ViewHolder {
        TextView textViewName, textViewAttribute, textViewPrice, textViewQuantity, textViewTotalAmount;

        public CartViewModel(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_order_product_name);
            textViewAttribute = itemView.findViewById(R.id.text_view_order_product_color);
            textViewPrice = itemView.findViewById(R.id.text_view_single_order_product_account);
            textViewQuantity = itemView.findViewById(R.id.text_view_order_product_count);
            textViewTotalAmount = itemView.findViewById(R.id.text_view_order_total_price_amount);
        }
    }

}
