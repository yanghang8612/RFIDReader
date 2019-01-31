package com.casc.rfidreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.casc.rfidreader.MyVars;
import com.casc.rfidreader.R;
import com.casc.rfidreader.fragment.FragmentFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private static String TAG = MainActivity.class.getSimpleName();
    private static int DEFAULT_INDEX = 0;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.bnb_main) BottomNavigationBar mMainBnb;

    private int mPreFragmentIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mMainBnb.setMode(BottomNavigationBar.MODE_FIXED)
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)
                .addItem(new BottomNavigationItem(R.drawable.ic_home_white, "任务"))
                .addItem(new BottomNavigationItem(R.drawable.ic_share_white, "历史"))
                .addItem(new BottomNavigationItem(R.drawable.ic_setting_white, "配置"))
                .addItem(new BottomNavigationItem(R.drawable.ic_my_white, "我的"))
                .setActiveColor(R.color.colorPrimary)
                .setInActiveColor(R.color.bottom_bar_active)
                .setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(int position) {
                        switchFragment(position);
                    }

                    @Override
                    public void onTabUnselected(int position) {}

                    @Override
                    public void onTabReselected(int position) {}
                })
                .initialise();

        Fragment defaultFragment = FragmentFactory.getInstanceByIndex(DEFAULT_INDEX);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fl_main, defaultFragment);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyVars.executor.shutdown();
    }

    public void switchFragment(int toIndex) {
        mMainBnb.selectTab(toIndex, false);
        Fragment from = FragmentFactory.getInstanceByIndex(mPreFragmentIndex);
        Fragment to = FragmentFactory.getInstanceByIndex(toIndex);
        if (from != null && to != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (!to.isAdded()) {
                transaction.hide(from).add(R.id.fl_main, to).commit();
            }
            else {
                transaction.hide(from).show(to).commit();
            }
            mPreFragmentIndex = toIndex;
        }
    }
}
