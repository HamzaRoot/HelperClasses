package com.qboxus.tictic.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.qboxus.tictic.Constants;
import com.qboxus.tictic.activitesfragments.VideosListF;
import com.qboxus.tictic.interfaces.FragmentCallBack;
import com.qboxus.tictic.models.HomeModel;
import com.qboxus.tictic.R;
import com.qboxus.tictic.simpleclasses.Variables;
import com.qboxus.tictic.simpleclasses.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class ViewPagerStatAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private static int PAGE_REFRESH_STATE=PagerAdapter.POSITION_UNCHANGED;

    public ViewPagerStatAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    public void refreshStateSet(boolean isRefresh) {
       if (isRefresh)
       {
           PAGE_REFRESH_STATE=PagerAdapter.POSITION_NONE;
       }
       else
       {
           PAGE_REFRESH_STATE=PagerAdapter.POSITION_UNCHANGED;
       }
    }


    public void addFragment(Fragment fragment) {
        mFragmentList.add(fragment);
    }

    public void removeFragment(int position) {
        mFragmentList.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PAGE_REFRESH_STATE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}