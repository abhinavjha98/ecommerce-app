package com.w3engineers.ecommerce.bootic.ui.userProfile;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.models.AddressModel;

import java.util.List;

public class UserAddressAdapter extends RecyclerView.Adapter<UserAddressAdapter.AddressViewHolder> {
    private List<AddressModel> addressList;
    private Context mContext;
    private ItemClickListener<AddressModel> mClickListener;

    public UserAddressAdapter(List<AddressModel> addressList, Context mContext) {
        this.addressList = addressList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_address, viewGroup, false);
        return new AddressViewHolder(rootView);
    }

    public void addListItem(List<AddressModel> newList) {
        for (AddressModel model : newList) {
            this.addressList.add(model);
            notifyItemInserted(addressList.size() - 1);
        }
    }

    public void addItem(AddressModel model) {
        if (model != null) {
            this.addressList.add(model);
            notifyDataSetChanged();
        }
    }

    public void addItem(AddressModel model, int position) {
        if (model != null) {
            this.addressList.add(position, model);
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        addressList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder addressViewHolder, int i) {
        AddressModel addressModel = addressList.get(i);
        if (addressModel != null) {

            String address = addressModel.addressLine1 + addressModel.addressLine2
                    + mContext.getResources().getString(R.string.city_text) + addressModel.city +
                    mContext.getResources().getString(R.string.zip_code) + addressModel.zipCode
                    + mContext.getResources().getString(R.string.state_text) + addressModel.state +
                    mContext.getResources().getString(R.string.country_text) + addressModel.country;
            addressViewHolder.textViewAddress.setText(address);

            addressViewHolder.imageViewMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(addressViewHolder.imageViewMenu, addressModel, addressViewHolder.getAdapterPosition());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void setItemClickListener(ItemClickListener<AddressModel> mClickListener) {
        this.mClickListener = mClickListener;
    }

    public class AddressViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAddress;
        ImageView imageViewMenu;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewAddress = itemView.findViewById(R.id.text_view_address);
            imageViewMenu = itemView.findViewById(R.id.image_view_menu);
        }
    }
}
