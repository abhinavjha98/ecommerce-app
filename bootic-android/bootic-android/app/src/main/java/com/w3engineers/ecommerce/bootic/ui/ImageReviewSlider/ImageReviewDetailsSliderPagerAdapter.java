package com.w3engineers.ecommerce.bootic.ui.ImageReviewSlider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.models.ShowReviewImage;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

public class ImageReviewDetailsSliderPagerAdapter extends PagerAdapter {

    private List<ShowReviewImage> imgList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public ImageReviewDetailsSliderPagerAdapter(List<ShowReviewImage> arrayList, Context context) {
        this.imgList = arrayList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (LinearLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.item_single_view_review_slider, container, false);
        ImageView imageView = item_view.findViewById(R.id.single_view);
        UIHelper.setFullImageUriInView(imageView, imgList.get(position).getProfileImageOfReviewer());
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}
