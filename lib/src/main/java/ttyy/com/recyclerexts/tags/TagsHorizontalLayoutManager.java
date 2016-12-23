package ttyy.com.recyclerexts.tags;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: hjq
 * Date  : 2016/12/23 09:31
 * Name  : TagsHorizontalLayoutManager
 * Intro : 带复用的标签流 水平方向
 * StaggeredGridLayoutManager 也可以满足需求
 * 此类作研究LayoutManager 学习性扩展
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/23    hjq             1.0              1.0
 */
public class TagsHorizontalLayoutManager extends RecyclerView.LayoutManager {

    // 0 <= mHorizontalOffset <= maxWidth
    private int mHorizontalOffset = 0;
    private SparseArray<Rect> mItemRects;

    public TagsHorizontalLayoutManager() {
        mItemRects = new SparseArray<>();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        // 没有Item
        if (getItemCount() == 0)
            return;

        if (getChildCount() == 0 && state.isPreLayout()) {
            return;
        }

        // onMeasure会调用两次次onLayoutChildren
        detachAndScrapAttachedViews(recycler);

        fill(recycler, state);
    }

    protected void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    int mMaxColumnWidth = 0;

    protected void fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dx) {

        if (getChildCount() > 0) {
            // 滑动时触发 回收越界的item 越上层越优先检查回收
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View child = getChildAt(i);
                if (dx > 0 && getDecoratedRight(child) < getPaddingLeft()) {
                    // 左侧出界 回收
                    removeAndRecycleView(child, recycler);
                } else if (dx < 0 && getDecoratedLeft(child) > getWidth() - getPaddingRight()) {
                    // 右侧出界 回收
                    removeAndRecycleView(child, recycler);
                }
            }
        }

        if (dx >= 0) {
            // 向左滑动 从最右边开始向右布局
            int mFirstVisiPos = 0;
            int mLastVisiPos = getItemCount() - 1;
            int leftOffset = getPaddingLeft();
            int topOffset = getPaddingTop();
            if (getChildCount() > 0) {
                View anchorView = getChildAt(getChildCount() - 1);
                mFirstVisiPos = getPosition(anchorView) + 1;
                leftOffset = getDecoratedLeft(anchorView);
                topOffset = getDecoratedBottom(anchorView);
                mMaxColumnWidth = Math.max(mMaxColumnWidth, getDecoratedMeasurementHorizontal(anchorView));
            }

            // 排版
            for (int i = mFirstVisiPos; i <= mLastVisiPos; i++) {
                View scrap = recycler.getViewForPosition(i);
                addView(scrap);// 加载最右侧
                measureChildWithMargins(scrap, 0, 0);

                if (topOffset + getDecoratedMeasurementVertical(scrap) > getHeight() - getPaddingBottom()) {
                    // 换一列
                    leftOffset += mMaxColumnWidth;
                    topOffset = getPaddingTop();

                    // 下一个起始位置是否超出屏幕外边
                    if (leftOffset > getWidth() - getPaddingRight()) {
                        removeAndRecycleView(scrap, recycler);
                        break;
                    }
                    mMaxColumnWidth = 0;
                }


                layoutDecoratedWithMargins(scrap, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(scrap), topOffset + getDecoratedMeasurementVertical(scrap));

                /*
                 * 缓存position上的绘制坐标信息 用于右滑动时直接layout上去 不需要重新排版
                 *
                 * 加上 mHorizontalOffset 原因:
                 *
                 * 逆序排序时直接拿出rect进行layout的
                 * 这时需要知道逆序排序，rect相对于原位置的偏差
                 * mHorizontalOffset在不同时间的不同值可以得出他们之间的误差
                 * 所以逆序排序时，需要 -mHorizontalOffset
                 *
                 */
                Rect rect = new Rect(leftOffset + mHorizontalOffset, topOffset, leftOffset + mHorizontalOffset + getDecoratedMeasurementHorizontal(scrap), topOffset + getDecoratedMeasurementVertical(scrap));
                mItemRects.put(i, rect);

                topOffset += getDecoratedMeasurementVertical(scrap);
                mMaxColumnWidth = Math.max(mMaxColumnWidth, getDecoratedMeasurementHorizontal(scrap));
            }

        } else {
            // 向右滑动 从最左边开始布局 取出缓存好的rect 直接layout上去
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                int mFirstVisiPos = getPosition(firstView) - 1;
                for (int i = mFirstVisiPos; i >= 0; i--) {
                    Rect rect = mItemRects.get(i);
                    View scrap = recycler.getViewForPosition(i);
                    addView(scrap, 0);// 加载最左侧
                    measureChildWithMargins(scrap, 0, 0);

                    layoutDecoratedWithMargins(scrap, rect.left - mHorizontalOffset, rect.top, rect.right - mHorizontalOffset, rect.bottom);
                }
            }
        }
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    /**
     * dx > 0 向右滑动
     * dx < 0 向左滑动
     *
     * @param dx
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getChildCount() == 0 || dx == 0)
            return 0;

        if (dx > 0) {
            // 向左滑动
            View anchorView = getChildAt(getChildCount() - 1);
            if (getPosition(anchorView) == getItemCount() - 1) {
                // 最后一个
                int gap = getWidth() - getPaddingRight() - getDecoratedRight(anchorView);
                if (gap > 0) {
                    // 向左滑过头了 需要矫正回去
                    dx = -gap;
                } else if (gap == 0) {
                    dx = 0;
                } else {
                    // 还没有向左滑到尽头
                    dx = Math.min(dx, -gap);
                }
            }
        } else {
            // 向右滑动
            if (mHorizontalOffset + dx < 0) {
                dx = -mHorizontalOffset;
            }
        }
        mHorizontalOffset += dx;

        /*
         *  偏移取负值原因
         *  Android 源码对dx的处理为 -dx/-dy  以减偏移量处理View的滚动（刷新重绘时以-dx/-dy为绘制的偏移）
         *  系统源码 根据如下位置刷新
         *  tmpr.set(l - dx, t - dy, r - dx, b - dy)
         */
        offsetChildrenHorizontal(-dx);
        fill(recycler, state, dx);
        return dx;
    }

    /**
     * 获取itemView在垂直方向上的所占用空间
     *
     * @param view
     * @return
     */
    int getDecoratedMeasurementVertical(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin
                + params.bottomMargin;
    }

    /**
     * 获取itemView在水平方向上的所占用空间
     *
     * @param view
     * @return
     */
    int getDecoratedMeasurementHorizontal(View view) {
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)
                view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin
                + params.rightMargin;
    }

}
