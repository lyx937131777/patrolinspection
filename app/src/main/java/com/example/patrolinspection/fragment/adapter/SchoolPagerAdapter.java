package com.example.patrolinspection.fragment.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.patrolinspection.R;
import com.example.patrolinspection.fragment.SchoolEventFragment;

//护校事件分页用的适配器
public class SchoolPagerAdapter extends FragmentPagerAdapter
{
    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_school_event_1, R.string.tab_text_school_event_2, R.string.tab_text_school_event_3};
    private final Context mContext;

    public SchoolPagerAdapter(Context context, FragmentManager fm)
    {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        // getItem is called to instantiate the fragment for the given page.
        // Return a EventRecordFragment (defined as a static inner class below).
        return SchoolEventFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount()
    {
        // Show 3 total pages.
        return 3;
    }
}
