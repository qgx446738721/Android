package org.voiddog.mblog.activity;

import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.voiddog.mblog.R;
import org.voiddog.mblog.fragment.MainListFragment_;

@EActivity(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {
    @ViewById
    Toolbar tool_bar;
    @ViewById
    DrawerLayout dl_main;

    private MainActivity mainActivity;

    @AfterViews
    void init(){
        //一体化色彩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        mainActivity = this;

        tool_bar.setTitle("主页");
        tool_bar.setTitleTextColor(getResources().getColor(R.color.title_text));
        setSupportActionBar(tool_bar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //创建返回键，并实现打开关/闭监听
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, dl_main, tool_bar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        dl_main.setDrawerListener(mDrawerToggle);

        setUpLeftListMenu();
        setUpContent();
    }

    void setUpLeftListMenu(){

    }

    void setUpContent(){
        MainListFragment_ fragment = new MainListFragment_();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.ll_main_fragment, fragment)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}
