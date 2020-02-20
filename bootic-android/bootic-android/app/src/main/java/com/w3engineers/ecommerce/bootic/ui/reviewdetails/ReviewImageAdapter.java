package com.w3engineers.ecommerce.bootic.ui.reviewdetails;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.makeramen.roundedimageview.RoundedImageView;
import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.models.ShowReviewImage;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.ui.ImageReviewSlider.ImageReviewSliderActivity;

import java.util.List;

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ImageViewHolder> {
    private List<ShowReviewImage> imageList;
    private Context mContext;

    public ReviewImageAdapter(List<ShowReviewImage> imageList,Context context) {
        this.imageList = imageList;
        this.mContext=context;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_review_up_images, viewGroup, false);
        return new ImageViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
        if (imageList!=null){
            ShowReviewImage showReviewImage=imageList.get(i);
            UIHelper.setThumbImageUriInView(imageViewHolder.mRoundedImageView , showReviewImage.getProfileImageOfReviewer());
            imageViewHolder.mRoundedImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageReviewSliderActivity.runActivity(imageList,mContext);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (imageList !=null){
            return imageList.size();
        }else {
            return 0;
        }

    }


    public class ImageViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView mRoundedImageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            mRoundedImageView=itemView.findViewById(R.id.image_view_images);
        }
    }
}
