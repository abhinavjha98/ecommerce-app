package com.w3engineers.ecommerce.bootic.ui.hearderview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.w3engineers.ecommerce.bootic.R;
import com.w3engineers.ecommerce.bootic.data.helper.base.BaseFragment;
import com.w3engineers.ecommerce.bootic.data.helper.models.SliderMain;
import com.w3engineers.ecommerce.bootic.data.util.Constants;
import com.w3engineers.ecommerce.bootic.data.util.UIHelper;
import com.w3engineers.ecommerce.bootic.ui.main.MainActivity;
import com.w3engineers.ecommerce.bootic.ui.offerproduct.OfferProductActivity;


public class SliderMainItemFragment extends BaseFragment<SliderMainMvpView, SliderMainPresenter> implements SliderMainMvpView {

    public static final String SLIDER_MAIN_KEY = "SLIDER_MAIN_KEY";
    public static final String SLIDER_MAIN_KEY_P = "SLIDER_MAIN_KEYs";
    private int positions;
    private String sliderText;

    public static SliderMainItemFragment newInstance(SliderMain sliderMain, int position) {
        Bundle args = new Bundle();
        args.putParcelable(SLIDER_MAIN_KEY, sliderMain);
        args.putInt(SLIDER_MAIN_KEY_P, position);

        SliderMainItemFragment fragment = new SliderMainItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.item_slider_fragment_main;
    }


    @Override
    protected void startUI() {
        Bundle args = getArguments();
        if (args == null) throw new AssertionError();
        SliderMain sliderMain = args.getParcelable(SLIDER_MAIN_KEY);
        positions = args.getInt(SLIDER_MAIN_KEY_P);
        if (sliderMain == null) throw new AssertionError();

        View view = getRootView();
        if (view != null) {
            int imageResource = view.getResources()
                    .getIdentifier(sliderMain.getId(), "drawable", getActivity().getPackageName());
            ImageView ivSliderImage = view.findViewById(R.id.iv_slider_image);
          //  ivSliderImage.setImageResource(imageResource);

            UIHelper.setFullImageUriInView(ivSliderImage, sliderMain.getImageName());

            TextView tvSliderHeading = view.findViewById(R.id.tv_slider_heading);
            tvSliderHeading.setText(sliderMain.getHeading());

            TextView tvSliderPreHeading = view.findViewById(R.id.tv_slider_preheading);
            tvSliderPreHeading.setText(sliderMain.getPreHeading());

            ivSliderImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), OfferProductActivity.class);
                  /*  if (positions == 0) {
                        sliderText = "slider_1";
                    } else if (positions == 1) {
                        sliderText = "slider_2";
                    } else if (positions == 2) {
                        sliderText = "slider_3";
                    }*/
                    intent.putExtra(Constants.IntentKey.INTENT_SLIDER_ID, sliderMain.getId());
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    protected void stopUI() {

    }

    @Override
    protected SliderMainPresenter initPresenter() {
        return new SliderMainPresenter();
    }

}
