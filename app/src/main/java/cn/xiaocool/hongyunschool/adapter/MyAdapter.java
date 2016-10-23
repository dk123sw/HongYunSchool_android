package cn.xiaocool.hongyunschool.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import cn.xiaocool.hongyunschool.activity.MainActivity;
import cn.xiaocool.hongyunschool.fragment.FirstFragment;
import cn.xiaocool.hongyunschool.fragment.FourFragment;
import cn.xiaocool.hongyunschool.fragment.SecondFragment;
import cn.xiaocool.hongyunschool.fragment.ThirdFragment;

/**
 * Created by dk on 2016/10/22.
 */

 public class MyAdapter extends FragmentPagerAdapter {
    private final int PAGER_COUNT = 4;
    private FirstFragment myFragment1 = null;
    private SecondFragment myFragment2 = null;
    private ThirdFragment myFragment3 = null;
    private FourFragment myFragment4 = null;


    public MyAdapter(FragmentManager fm ) {
            super(fm);
        myFragment1 = new FirstFragment();
        myFragment2 = new SecondFragment();
        myFragment3 = new ThirdFragment();
        myFragment4 = new FourFragment();
    }


    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("position Destory" + position);
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = myFragment1;
                break;
            case MainActivity.PAGE_TWO:
                fragment = myFragment2;
                break;
            case MainActivity.PAGE_THREE:
                fragment = myFragment3;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = myFragment4;
                break;
        }
        return fragment;
    }

}
