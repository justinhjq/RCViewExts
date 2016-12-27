package ttyy.com.recyclerexts.tags;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: hjq
 * Date  : 2016/12/21 13:23
 * Name  : TagsVerticalLayoutManager
 * Intro : 带复用的标签流 垂直方向
 * StaggeredGridLayoutManager 也可以满足需求
 * 此类作研究LayoutManager  学习性扩展
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/21    hjq              1.0              1.0
 */
public class TagsVerticalLayoutManager extends RecyclerView.LayoutManager {

    // 0 <= mVerticalOffset <= maxheight
    // maxHeight = getHeight() - getPaddingHeight()
    private int mVerticalOffset;
    //key 是View的position，保存View的rect用于逆序排序时使用
    private SparseArray<Rect> mItemRects;

    public TagsVerticalLayoutManager() {
        mItemRects = new SparseArray<>();
    }

    /**
     * ItemView的LayoutParams
     *
     * @return
     */
    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Item layout
     * <p>
     * RecyclerView onMeasure时会触发两次
     *
     * RecyclerView setAdapter会触发该方法
     * Adapter notifyDataSetChanged会触发该方法
     *
     * setAdapter/notifyDataSetChanged时，LayotuManager生命周期方法调用流程:
     * 1. removeAndRecycleAllViews(recycler) 回收到回收池
     * 2. onLayoutChildren，从回收池(非垃圾堆，垃圾堆对应scrap)取出itemview，进行onBindHolder绑定
     *
     * @param recycler 回收管理器
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getItemCount() == 0) {
            //没有Item，界面空着吧
            detachAndScrapAttachedViews(recycler);
            return;
        }
        if (getChildCount() == 0 && state.isPreLayout()) {
            //state.isPreLayout()是支持动画的
            return;
        }
        //onLayoutChildren方法在RecyclerView 初始化时 会执行两遍
        detachAndScrapAttachedViews(recycler);

        mVerticalOffset = 0;
        fill(recycler, state);
    }

    /**
     * Item布局填充
     *
     * @param recycler
     * @param state
     */
    void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    /**
     * Item布局填充
     * dy > 0 向上滑动
     * dy < 0 向下滑动
     *
     * @param recycler
     * @param state
     * @param dy
     * @return
     */
    int fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        int topOffset = getPaddingTop();
        int leftOffset = getPaddingLeft();
        int bottomLine = getHeight() - getPaddingBottom();
        int mFirstVisiPos = 0;
        int mLastVisiPos = getItemCount();
        if (getChildCount() > 0) {
            // 滑动布局更新
            // 垃圾回收要从上往下回收
            // addView() 中越后添加的越在上层
            // 回收检查从上层到下层 层层回收
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View item = getChildAt(i);
                if (getDecoratedBottom(item) < topOffset) {
                    // 上滑动 出界回收
                    removeAndRecycleView(item, recycler);
                } else if (getDecoratedTop(item) > getHeight() - getPaddingBottom()) {
                    // 下滑动 出界回收
                    removeAndRecycleView(item, recycler);
                }
            }

        }

        if (dy >= 0) {
            // 行高
            int mMaxLineHeight = 0;
            if (getChildCount() > 0) {
                // 向上滑动 从底部更新
                View anchorView = getChildAt(getChildCount() - 1);
                mFirstVisiPos = getPosition(anchorView) + 1;
                mLastVisiPos = getItemCount();
                topOffset = getDecoratedBottom(anchorView);
                leftOffset = getDecoratedRight(anchorView);
            }

            for (int i = mFirstVisiPos; i < mLastVisiPos; i++) {
                View item = recycler.getViewForPosition(i);
                addView(item);
                measureChildWithMargins(item, 0, 0);

                if (leftOffset + getDecoratedMeasurementHorizontal(item) > getHorizontalSpace() + getPaddingLeft()) {
                    // 换行
                    topOffset += mMaxLineHeight;
                    leftOffset = getPaddingLeft();
                    mMaxLineHeight = 0;
                    if (topOffset > bottomLine) {
                        // 多生成一排用作缓存
                        removeAndRecycleView(item, recycler);
                        break;
                    }
                }

                layoutDecorated(item, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(item), topOffset + getDecoratedMeasurementVertical(item));
                //保存Rect供逆序layout用
                Rect rect = new Rect(leftOffset, topOffset + mVerticalOffset, leftOffset + getDecoratedMeasurementHorizontal(item), topOffset + getDecoratedMeasurementVertical(item) + mVerticalOffset);
                mItemRects.put(i, rect);

                leftOffset += getDecoratedMeasurementHorizontal(item);
                mMaxLineHeight = Math.max(mMaxLineHeight, getDecoratedMeasurementVertical(item));
            }
        } else {
            /**
             * 下滑动 丛顶部向底部排版ItemView
             * 利用Rect保存子View边界
             * 正序排列时，保存每个子View的Rect，逆序时，直接拿出来layout。
             * 避免重复layout增加代码复杂度
             */
            int maxPos = getItemCount() - 1;
            mFirstVisiPos = 0;
            if (getChildCount() > 0) {
                View firstView = getChildAt(0);
                maxPos = getPosition(firstView) - 1;
            }
            for (int i = maxPos; i >= mFirstVisiPos; i--) {
                Rect rect = mItemRects.get(i);

                if (rect.bottom - mVerticalOffset < getPaddingTop()) {
                    break;
                } else {
                    View child = recycler.getViewForPosition(i);
                    addView(child, 0);//将View添加至RecyclerView中，childIndex为1，但是View的位置还是由layout的位置决定
                    measureChildWithMargins(child, 0, 0);

                    layoutDecoratedWithMargins(child, rect.left, rect.top - mVerticalOffset, rect.right, rect.bottom - mVerticalOffset);
                }
            }
        }

        return dy;
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

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (getChildCount() == 0 || dy == 0)
            return 0;

        if (dy > 0) {
            // scrolling up
            View bottomView = getChildAt(getChildCount() - 1);
            if (getPosition(bottomView) == getItemCount() - 1) {
                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(bottomView);
                if (gap > 0) {
                    dy = -gap;
                } else if (gap == 0) {
                    dy = 0;
                } else {
                    dy = Math.min(dy, -gap);
                }
            }
        } else {
            // scrolling down
            if (mVerticalOffset + dy < 0) {
                dy = -mVerticalOffset;
            }
        }
        mVerticalOffset += dy;

        /*
         *  偏移取负值原因
         *  Android 源码对dx的处理为 -dx/-dy  以减偏移量处理View的滚动（刷新重绘时以-dx/-dy为绘制的偏移）
         *  系统源码 根据如下位置刷新
         *  tmpr.set(l - dx, t - dy, r - dx, b - dy)
         */
        offsetChildrenVertical(-dy);
        fill(recycler, state, dy);
        return dy;
    }

    /**
     * 只可以垂直滑动
     *
     * @return
     */
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    public int getHorizontalSpace() {
        return getWidth() - getPaddingRight() - getPaddingLeft();
    }

    public int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }
}
