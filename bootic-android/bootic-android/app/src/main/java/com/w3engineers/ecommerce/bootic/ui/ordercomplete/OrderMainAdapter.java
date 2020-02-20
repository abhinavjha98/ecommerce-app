package com.w3engineers.ecommerce.bootic.ui.ordercomplete;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.models.OrderModel;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.CustomSharedPrefs;
import com.w3engineers.ecommerce.bootic.data.util.SharedPref;
import com.w3engineers.ecommerce.bootic.data.util.UtilityClass;

import java.util.List;

public class OrderMainAdapter extends RecyclerView.Adapter<OrderMainAdapter.MainViewHolder> {
    private List<OrderModel> orderList;
    private Context mContext;

    public OrderMainAdapter(List<OrderModel> orderModels, Context mContext) {
        this.orderList = orderModels;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_order_main,
                viewGroup, false);
        return new MainViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder mainHolder, int i) {
        OrderModel orderModel = orderList.get(i);
        if (orderModel != null) {

            mainHolder.textViewTransactionId.setText(""+orderModel.transactionId);

            mainHolder.textViewTax.setText( ""+ UtilityClass.getCurrencySymbolAndAmount(mContext, Float.parseFloat(orderModel.tax)));
            mainHolder.textViewPrice.setText(""+UtilityClass.getCurrencySymbolAndAmount(mContext, Float.parseFloat(orderModel.amount)));


            mainHolder.textViewMethod.setText(orderModel.method);

            mainHolder.textViewStatus.setText(orderModel.status);

            String convertLocalTime= UtilityClass.getDateStringFromDateValue(orderModel.DateTime,
                    Constants.DateFormat.DATE_FORMAT_TIME);
            mainHolder.textViewTimeDate.setText(convertLocalTime);

            if (orderModel.userOrderLists != null) {
                OrderCompleteAdapter orderCompleteAdapter = new OrderCompleteAdapter(orderModel.userOrderLists, mContext);
                mainHolder.recyclerView.setAdapter(orderCompleteAdapter);
                mainHolder.recyclerView.setLayoutManager(new LinearLayoutManager(mContext,
                        LinearLayoutManager.VERTICAL, false));
            }

        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void addItem(List<OrderModel> newList) {
        if (newList != null) {
            for (OrderModel list : newList) {
                this.orderList.add(list);
                notifyItemInserted(orderList.size() - 1);
            }
        }
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        TextView textViewTransactionId, textViewMethod, textViewStatus, textViewTimeDate, textViewPrice,textViewTax;
        RecyclerView recyclerView;

        public MainViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewTransactionId = itemView.findViewById(R.id.text_view_trans_id);
            textViewMethod = itemView.findViewById(R.id.text_view_bank_name);
            textViewStatus = itemView.findViewById(R.id.text_view_status_condition);
            textViewTimeDate = itemView.findViewById(R.id.text_view_date_time);
            recyclerView = itemView.findViewById(R.id.recycler_view_order_products);
            textViewPrice = itemView.findViewById(R.id.text_view_total_price_value);
            textViewTax = itemView.findViewById(R.id.text_view_tax_value);
        }
    }
}
