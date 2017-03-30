package ttyy.com.recyclerexts.recycle_album;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import java.util.List;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;

/**
 * Author: Administrator
 * Date  : 2016/12/27 13:55
 * Name  : ReCycleAlbumCallback
 * Intro : 循环相册 touch事件处理
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class ReCycleAlbumCallback extends ItemTouchHelper.SimpleCallback {

    EXTRecyclerAdapter mAdapter;
    ReCycleAlbumConfig mConfig = ReCycleAlbumConfig.getInstance();

    public ReCycleAlbumCallback(EXTRecyclerAdapter mAdapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        this.mAdapter = mAdapter;
    }

    private ReCycleAlbumCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    /**
     * 判定是否删除该Item因子
     * 滑动距离/总距离
     *
     * @param viewHolder
     * @return
     */
    @Override
    public float getMoveThreshold(RecyclerView.ViewHolder viewHolder) {
        return super.getMoveThreshold(viewHolder);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * 滑动Item之后，如果Item达到了删除的界限，那么会触发onSwiped方法，从而进行数据变更
     *
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        List<Object> mDatas = mAdapter.getDatas();
        Object object = mDatas.remove(viewHolder.getLayoutPosition());
        mDatas.add(0, object);
        mAdapter.notifyDataSetChanged();

        // childDraw之后触发onSwiped将移除的itemview状态重置
        viewHolder.itemView.setRotation(0);
    }

    /**
     * 如果需要支持3.0以下版本，那么请重写onChildDrawOver，这两个方法实际含义相同
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        float swipedDistance = (float) Math.sqrt(dX * dX + dY * dY);
        float fraction = swipedDistance / (recyclerView.getWidth() * getSwipeThreshold(viewHolder));

        fraction = fraction > 1 ? 1 : fraction;

        // childIndex 0 是最下层
        // childIndex childCount - 1 是最上层
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);

            int level = recyclerView.getChildCount() - i - 1;
            if (level > 0) {
                // 最顶层正在被用户持有滑动，不需要做出改动
                child.setScaleY(1 - mConfig.getScaleUnit() * level + fraction * mConfig.getScaleUnit());
                child.setScaleX(1 - mConfig.getScaleUnit() * level + fraction * mConfig.getScaleUnit());
                child.setTranslationY(mConfig.getYGapUnit() * level - fraction * mConfig.getYGapUnit());
            } else {
                // 最顶层旋转角度
                fraction  = dX / (recyclerView.getWidth() * getSwipeThreshold(viewHolder));
                if(fraction > 1)
                    fraction = 1;
                if(fraction < -1)
                    fraction = -1;

                child.setRotation(fraction * mConfig.getMaxRotation());
            }

        }

    }

}
