package com.w3engineers.ecommerce.bootic.ui.hearderview;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.w3engineers.ecommerce.bootic.data.helper.models.SliderMain;

import java.util.ArrayList;
import java.util.List;

public class SliderMainAdapter extends FragmentStatePagerAdapter {

    List<SliderMain> sliderMainList = new ArrayList<>();

    public SliderMainAdapter(FragmentManager fm, List<SliderMain> mList) {
        super(fm);
        this.sliderMainList = mList;
    }

    @Override
    public Fragment getItem(int position) {
        return SliderMainItemFragment.newInstance(sliderMainList.get(position), position);
    }

    @Override
    public int getCount() {
        return sliderMainList.size();
    }

}
