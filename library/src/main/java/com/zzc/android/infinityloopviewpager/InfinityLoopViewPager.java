package com.zzc.android.infinityloopviewpager;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.zzc.android.infinityloopviewpager.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义无限轮播控件
 *
 * Created by zczhang on 16/1/22.
 */
public class InfinityLoopViewPager extends RelativeLayout implements ViewPager.OnPageChangeListener {
    private final String TAG = "InfinityLoopViewPager";
    private static final int DEFAULT_INTERVAL_TIME = 5 * 1000;//默认轮播时间间隔

    private Context mContext;

    /**
     * 用来轮播的图片地址
     */
    private List<String> mUrls = null;

    /**
     * 轮播间隔时间
     */
    private int mIntervalTime = DEFAULT_INTERVAL_TIME;

    /**
     * 是否允许轮播的标志
     */
    private boolean isLoopable = true;

    /**
     * 发送定时消息
     */
    private Handler mLoopHandler = null;

    /**
     * 执行页面切换的任务
     */
    private Runnable mTaskRunnable = null;

    private ViewPager mViewPager = null;

    /**
     * 显示指示器布局
     */
    private LinearLayout mIndicatorLayout = null;

    private List<View> mIndicatorViews = null;

    /**
     * viewpager的数据适配器
     */
    private BannerViewAdapter mBannerAdapter = null;

    /**
     * 当前显示的位置
     */
    private int mViewPagerCurrentItem = 0;

    /**
     * 指示器选中状态的背景资源
     */
    private int mIndicatorSelectedResId = R.drawable.banner_indicator_dot_selected;

    /**
     * 指示器未选中时的背景资源
     */
    private int mIndicatorUnselectedResId = R.drawable.banner_indicator_circle_line_white;

    public InfinityLoopViewPager(Context context) {
        super(context);
        initialize(context, null);
    }

    public InfinityLoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public InfinityLoopViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfinityLoopViewPager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 初始化方法
     *
     * @param context
     * @param attrs
     */
    private void initialize(Context context, AttributeSet attrs) {
        this.mContext = context;
        //初始化Fresco
        Fresco.initialize(context.getApplicationContext());
        //初始化viewpager和指示器布局并添加到父布局中
        mViewPager = new ViewPager(context);
        mViewPager.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mViewPager.addOnPageChangeListener(this);
        addView(mViewPager, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mIndicatorLayout = new LinearLayout(context);
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.bottomMargin = dip2px(context, 10);
        mIndicatorLayout.setLayoutParams(layoutParams);
        addView(mIndicatorLayout);

        mIndicatorViews = new ArrayList<>();
        mLoopHandler = new Handler();
        mTaskRunnable = new Runnable() {
            @Override
            public void run() {
                mViewPager.setCurrentItem(getNextItemPosition(mViewPagerCurrentItem), true);
            }
        };
    }

    /**
     * 设置轮播图片的网络地址
     *
     * @param mUrls
     */
    public synchronized void setUrls(List<String> mUrls) {
        if (mUrls == null || mUrls.size() == 0) {
            Log.e("TAG", "banner data is empty!");
            return;
        } else {
            this.mUrls = mUrls;

            if (mBannerAdapter == null) {
                mBannerAdapter = new BannerViewAdapter(new BannerViewAdapter.ViewHolderCreator() {
                    @Override
                    public Object createHolder() {
                        return new BannerItemHolderView();
                    }
                }, mUrls);
            } else {
                mBannerAdapter.setDatas(mUrls);
            }
            mViewPager.setAdapter(mBannerAdapter);

            //只有当大于一张轮播图的时候才自动轮播
            if (mUrls.size() == 1) {
                isLoopable = false;
                mViewPager.setCurrentItem(0, true);
            } else {
                mViewPager.setCurrentItem(mUrls.size() * 10000);
                generateIndicators(mUrls, mIndicatorLayout);
            }
        }
    }

    //添加指示器
    private void generateIndicators(List<String> mUrls, LinearLayout mIndicatorLayout) {
        mIndicatorLayout.removeAllViews();
        mIndicatorViews.clear();
        int bannerCount = mUrls.size();
        for (int i = 0; i < bannerCount; i++) {
            ImageView dot = new ImageView(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dip2px(mContext, 8), dip2px(mContext, 8));
            layoutParams.setMargins(dip2px(mContext, 3), 0, dip2px(mContext, 3), 0);
            dot.setLayoutParams(layoutParams);
            if (i == 0) {
                dot.setImageResource(mIndicatorSelectedResId);
            } else {
                dot.setImageResource(mIndicatorUnselectedResId);
            }
            mIndicatorLayout.addView(dot);
            mIndicatorViews.add(dot);
        }
    }

    //刷新指示器显示位置
    private void refreshIndicator(int position) {
        int indicatorCount = mIndicatorViews.size();
        for (int i = 0; i < indicatorCount; i++) {
            if(i==position) {
                ((ImageView)mIndicatorViews.get(i)).setImageResource(mIndicatorSelectedResId);
            } else {
                ((ImageView)mIndicatorViews.get(i)).setImageResource(mIndicatorUnselectedResId);
            }
        }
    }

    //获取下一个需要显示的banner的位置
    private int getNextItemPosition(int currentItem) {
        if (currentItem + 1 == mBannerAdapter.getCount()) {
            return 0;
        } else {
            return currentItem + 1;
        }
    }

    /**
     * 设置轮播时间间隔
     *
     * @param mIntervalTime
     */
    public void setIntervalTime(int mIntervalTime) {
        this.mIntervalTime = mIntervalTime;
    }

    /**
     * 开始轮播
     */
    public void startLoop() {
        if (isLoopable) {
            mLoopHandler.removeCallbacks(mTaskRunnable);
            mLoopHandler.postDelayed(mTaskRunnable, mIntervalTime);
        }
    }

    /**
     * 暂定轮播
     */
    public void stopLoop() {
        mLoopHandler.removeCallbacks(mTaskRunnable);
    }

    private int dip2px(Context context, float dpValue) {
        if (context == null) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        refreshIndicator(mBannerAdapter.translateReal2LogicPosition(position));
        mViewPagerCurrentItem = position;
        stopLoop();
        startLoop();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class BannerItemHolderView implements BannerViewAdapter.ViewHolder<String> {
        private SimpleDraweeView simpleDraweeView;

        @Override
        public View createView(Context context) {
            simpleDraweeView = new SimpleDraweeView(context);
            simpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            return simpleDraweeView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            simpleDraweeView.setImageURI(Uri.parse(data));
        }
    }
}
