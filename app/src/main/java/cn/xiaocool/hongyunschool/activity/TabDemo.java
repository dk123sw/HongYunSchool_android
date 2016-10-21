package cn.xiaocool.hongyunschool.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.lzy.widget.tab.PagerSlidingTabStrip;

import java.util.ArrayList;

import cn.xiaocool.hongyunschool.R;
/**
 * 未使用
 */
public class TabDemo extends AppCompatActivity {

    private ArrayList<Fragment> fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_demo);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        PagerSlidingTabStrip tab = (PagerSlidingTabStrip) findViewById(R.id.tab);

        fragments = new ArrayList<>();
//        fragments.add(new OkhttpFragment());
//        fragments.add(new DownloadFragment());
//        fragments.add(new UploadFragment());

        viewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        tab.setViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        OkHttpUtils.getInstance().cancelTag(this);
    }

    private class MyAdapter extends FragmentPagerAdapter {

        private String[] titles = {"一般请求", "下载管理", "上传管理"};

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
