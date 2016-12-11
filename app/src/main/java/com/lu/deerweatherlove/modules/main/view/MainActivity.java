package com.lu.deerweatherlove.modules.main.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.lu.deerweatherlove.R;
import com.lu.deerweatherlove.base.BaseActivity;
import com.lu.deerweatherlove.base.Constant;
import com.lu.deerweatherlove.common.utils.CircularAnimUtil;
import com.lu.deerweatherlove.common.utils.DoubleClickEXit;
import com.lu.deerweatherlove.common.utils.RxDrawer;
import com.lu.deerweatherlove.common.utils.RxUtils;
import com.lu.deerweatherlove.common.utils.SharedPreferenceUtil;
import com.lu.deerweatherlove.common.utils.SimpleSubscriber;
import com.lu.deerweatherlove.common.utils.ToastUtil;
import com.lu.deerweatherlove.common.utils.ULog;
import com.lu.deerweatherlove.modules.about.view.AboutActivity;
import com.lu.deerweatherlove.modules.city.view.ChoiceCityActivity;
import com.lu.deerweatherlove.modules.main.adapter.HomePagerAdapter;
import com.lu.deerweatherlove.modules.service.AutoUpdateService;
import com.lu.deerweatherlove.modules.setting.view.SettingActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by L on 16/11/10.
 */
public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fabutton)
    FloatingActionButton fabutton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.coordlayout)
    CoordinatorLayout coordlayout;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        ULog.i("onCreate");
       // sHA1(getApplicationContext());
        initView();

        initDrawer();

        initIcon();

        //开启服务
        startService(new Intent(this, AutoUpdateService.class));
    }

    /**
     * 定位获取sha1
     * @param context
     * @return
     */

    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            System.out.println(result+"......................");
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ULog.i("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initIcon();
        ULog.i("onRestart");
    }

    //视图初始化
    private void initView() {
        setSupportActionBar(toolbar);

        HomePagerAdapter mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mHomePagerAdapter.addTab(new MainFragment(), "主页面");
        mHomePagerAdapter.addTab(new MoreCityFragment(), "添加城市");
        viewPager.setAdapter(mHomePagerAdapter);
        tabLayout.setupWithViewPager(viewPager, false);//关联起来，是否自动刷新
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    //设置图片资源
                    fabutton.setImageResource(R.drawable.ic_add);
                    //设置背景颜色列表
                    fabutton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorPrimary)));
                    fabutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //选择城市
                             Intent intent=new Intent(MainActivity.this, ChoiceCityActivity.class);
                             intent.putExtra(Constant.MULTI_CHECK, true);
                            CircularAnimUtil.startActivity(MainActivity.this, intent, fabutton,
                                    R.color.colorPrimary);
                        }
                    });
                    if (!fabutton.isShown()) {
                        fabutton.show();
                    }
                } else {
                    fabutton.setImageResource(R.drawable.ic_heart);
                    fabutton.setBackgroundTintList(
                            ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorAccent))
                    );

                    fabutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //显示一个对话框
                            showFabDialog();
                        }


                    });
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        fabutton.setOnClickListener(v -> showFabDialog());

    }

    //显示主页面fab按钮的 对话框内容
    private void showFabDialog() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("好评")
                .setMessage("GitHub上给个星星吧。拜托亲")
                .setPositiveButton("好滴", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Uri uri = Uri.parse(getString(R.string.app_html));
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        MainActivity.this.startActivity(intent);
                    }
                })
                .show();


    }

    /**
     * 初始化抽屉
     * Info:灵感来源 https://segmentfault.com/a/1190000004151222
     */

    private void initDrawer() {

        if (navView != null) {
            navView.setNavigationItemSelectedListener(this);
            navView.inflateHeaderView(R.layout.nav_view_main);

            ActionBarDrawerToggle mToggle = new ActionBarDrawerToggle(this,
                    drawerLayout, toolbar, R.string.nav_drawer_open,
                    R.string.nae_drawwer_close);

            drawerLayout.addDrawerListener(mToggle);
            /**
             * 源码
             * public void syncState() {
             if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
             setPosition(1);
             } else {
             setPosition(0);
             }
             if (mDrawerIndicatorEnabled) {
             setActionBarUpIndicator((Drawable) mSlider,
             mDrawerLayout.isDrawerOpen(GravityCompat.START) ?
             mCloseDrawerContentDescRes : mOpenDrawerContentDescRes);
             }
             }
             */
            //同步状态
            mToggle.syncState();

        }

    }

    /**
     * 初始化图标
     */

    private void initIcon() {



        if (SharedPreferenceUtil.getInstance().getIconType() == 0) {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_one_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_one_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_one_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_one_light_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_one_heavy_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_one_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_one_thunder_rain);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_one_fog);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_one_fog);
            SharedPreferenceUtil.getInstance().putInt("雪", R.mipmap.type_one_snow);


        } else {
            SharedPreferenceUtil.getInstance().putInt("未知", R.mipmap.none);
            SharedPreferenceUtil.getInstance().putInt("晴", R.mipmap.type_two_sunny);
            SharedPreferenceUtil.getInstance().putInt("阴", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("多云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("少云", R.mipmap.type_two_cloudy);
            SharedPreferenceUtil.getInstance().putInt("晴间多云", R.mipmap.type_two_cloudytosunny);
            SharedPreferenceUtil.getInstance().putInt("小雨", R.mipmap.type_two_light_rain);
            SharedPreferenceUtil.getInstance().putInt("中雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("大雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("阵雨", R.mipmap.type_two_rain);
            SharedPreferenceUtil.getInstance().putInt("雷阵雨", R.mipmap.type_two_thunderstorm);
            SharedPreferenceUtil.getInstance().putInt("霾", R.mipmap.type_two_haze);
            SharedPreferenceUtil.getInstance().putInt("雾", R.mipmap.type_two_fog);
            SharedPreferenceUtil.getInstance().putInt("雨夹雪", R.mipmap.type_two_snowrain);
            SharedPreferenceUtil.getInstance().putInt("雪", R.mipmap.type_two_snow);

        }


    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        RxDrawer.close(drawerLayout).compose(RxUtils.rxSchedulerHelper(
                AndroidSchedulers.mainThread()
        )).subscribe(
                new SimpleSubscriber<Void>() {
                    @Override
                    public void onNext(Void aVoid) {
                        switch (item.getItemId()) {
                            case R.id.nav_set:
                                Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                                startActivity(intentSetting);
                                break;
                            case R.id.nav_about:
                                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                                break;
                            case R.id.nav_city:
                                Intent intentCity = new Intent(MainActivity.this, ChoiceCityActivity.class);
                                startActivity(intentCity);
                                break;
                            case R.id.nav_more_cities:
                                viewPager.setCurrentItem(1);
                                break;
                        }
                    }
                });


        return false;
    }

    /**
     * 返回键的自定义
     */
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);

        }else{
            if (!DoubleClickEXit.check()){
                ToastUtil.showShort(getString(R.string.double_exit));
            }else {
                finish();
            }

        }
    }
}
