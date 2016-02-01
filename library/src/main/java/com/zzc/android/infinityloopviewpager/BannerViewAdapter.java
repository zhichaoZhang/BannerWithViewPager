package com.zzc.android.infinityloopviewpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.zzc.android.infinityloopviewpager.R;

import java.util.List;

/**
 * 数据适配器
 *
 * Created by zczhang on 16/1/22.
 */
public class BannerViewAdapter<T> extends PagerAdapter {
    private static final String TAG = "BannerViewAdapter";
    private ViewHolderCreator mViewHolderCreator;
    private List<T> mDatas;
    private int mBannerCount;

    public BannerViewAdapter(ViewHolderCreator viewHolderCreator, List<T> datas) {
        this.mViewHolderCreator = viewHolderCreator;
        this.mDatas = datas;
        mBannerCount = mDatas.size();
    }

    public void setDatas(List<T> mDatas) {
        this.mDatas = mDatas;
        mBannerCount = mDatas.size();
    }

    @Override
    public int getCount() {
        return mBannerCount == 1 ? 1 : Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        final int realPosition = translateReal2LogicPosition(position);
        View view = getView(realPosition, null, container);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private View getView(int position, View view, ViewGroup container) {
        ViewHolder holder = null;
        if (view == null) {
            holder = (ViewHolder) mViewHolderCreator.createHolder();
            view = holder.createView(container.getContext());
            view.setTag(R.id.cb_item_tag, holder);
        } else {
            holder = (ViewHolder<T>) view.getTag(R.id.cb_item_tag);
        }
        if (mDatas != null && !mDatas.isEmpty())
            holder.UpdateUI(container.getContext(), position, mDatas.get(position));
        return view;
    }

    public int translateReal2LogicPosition(int realPosition) {
        return realPosition % mBannerCount;
    }

    public interface ViewHolder<T> {
        View createView(Context context);

        void UpdateUI(Context context, int position, T data);
    }

    public interface ViewHolderCreator<Holder> {
        Holder createHolder();
    }
}
