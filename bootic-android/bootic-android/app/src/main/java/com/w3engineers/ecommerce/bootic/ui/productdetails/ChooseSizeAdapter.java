package com.w3engineers.ecommerce.bootic.ui.productdetails;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.ItemClickListener;
import com.w3engineers.ecommerce.bootic.data.helper.models.AttributeValueModel;

import java.util.List;

public class ChooseSizeAdapter extends RecyclerView.Adapter<ChooseSizeAdapter.SizeViewHolder> {

    private List<AttributeValueModel> imageList;
    private Context mContext;
    private ItemClickListener<AttributeValueModel> mListener;
    private int prevPosition;
    private boolean isFirstClick;
    private RadioButton newRadioButton;

    public ChooseSizeAdapter(List<AttributeValueModel> imageList, Context mContext) {
        this.imageList = imageList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SizeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_choose_size, viewGroup, false);
        return new SizeViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SizeViewHolder viewHolder, int i) {
        AttributeValueModel valueModel = imageList.get(i);
        if (valueModel != null) {
            viewHolder.radioButton.setText(valueModel.title);
            viewHolder.radioButton.setClickable(false);
            viewHolder.constraintLayout.setOnClickListener(v -> {
                if (isFirstClick && prevPosition != i) {
                    newRadioButton.setChecked(false);
                }
                newRadioButton = viewHolder.radioButton;
                prevPosition = i;
                isFirstClick = true;
                viewHolder.radioButton.setChecked(true);
                mListener.onItemClick(viewHolder.constraintLayout, valueModel, i);
            });
            if (i == (imageList.size() - 1)) {
                viewHolder.line_view.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void addItem(List<AttributeValueModel> newList) {
        if (newList != null) {
            for (AttributeValueModel list : newList) {
                this.imageList.add(list);
                notifyItemInserted(imageList.size() - 1);
            }
        }
    }

    public void serOnClickListener(ItemClickListener<AttributeValueModel> listener) {
        this.mListener = listener;
    }

    public class SizeViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        RadioButton radioButton;
        View line_view;

        public SizeViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.size_XS);
            constraintLayout = itemView.findViewById(R.id.parent_layout_size);
            line_view = itemView.findViewById(R.id.view_line);
        }
    }
}
