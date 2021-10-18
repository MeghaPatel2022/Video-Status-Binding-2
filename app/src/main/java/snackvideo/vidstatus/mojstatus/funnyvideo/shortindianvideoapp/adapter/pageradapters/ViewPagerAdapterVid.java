package snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.adapter.pageradapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.fragment.CategoryVidFragment;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.fragment.NewVidFragment;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.fragment.TrendingVidFragment;
import snackvideo.vidstatus.mojstatus.funnyvideo.shortindianvideoapp.utils.Constant;

public class ViewPagerAdapterVid extends FragmentPagerAdapter {
    private static final int NUM_ITEMS = 3;
    private final String[] tabTitles = new String[]{Constant.NAV_SELECTED_ITEM, "Trending", "Category"};

    public ViewPagerAdapterVid(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new NewVidFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new TrendingVidFragment();
            case 2: // Fragment # 1 - This will show SecondFragment
                return new CategoryVidFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

}
