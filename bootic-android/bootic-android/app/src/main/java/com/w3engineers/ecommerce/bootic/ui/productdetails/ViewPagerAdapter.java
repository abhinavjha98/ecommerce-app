package com.w3engineers.ecommerce.bootic.ui.productdetails;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.models.ProductDetailsImageModel;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;

import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {
    private List<ProductDetailsImageModel> imgList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(List<ProductDetailsImageModel> arrayList, Context context) {
        this.imgList = arrayList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return imgList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return (view == (ConstraintLayout) o);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View item_view = layoutInflater.inflate(R.layout.item_swipe_image, container, false);
        ImageView imageView = item_view.findViewById(R.id.image_view_details);
        UIHelper.setFullImageUriInView(imageView, imgList.get(position).imageUri);
        container.addView(item_view);
        return item_view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
