package ttyy.com.recyclerexts.tags;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * author: admin
 * date: 2017/02/13
 * version: 0
 * mail: secret
 * desc: SquareTagsLayoutManager
 */

public class SquareTagsLayoutManager extends RecyclerView.LayoutManager {

    int mSquareWidth;
    int mSquareHeight;

    int mVerticalScrollOffset;
    SparseArray<Rect> mItemRects;

    private SquareTagsLayoutManager() {
        mItemRects = new SparseArray<>();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        if (getItemCount() == 0) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        if (getChildCount() == 0 && state.isPreLayout()) {
            detachAndScrapAttachedViews(recycler);
            return;
        }

        detachAndScrapAttachedViews(recycler);
        mVerticalScrollOffset = 0;
        fill(recycler, state);
    }

    protected void fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        fill(recycler, state, 0);
    }

    protected void fill(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {

        if (getChildCount() > 0) {
            // 垃圾回收
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View item = getChildAt(i);
                if (getDecoratedBottom(item) < 0) {
                    // item出了上边界 回收
                    removeAndRecycleView(item, recycler);
                } else if (getDecoratedTop(item) > getHeight() - getPaddingBottom()) {
                    // item出了下边界 回收
                    removeAndRecycleView(item, recycler);
                }
            }
        }

        if (dy >= 0) {
            // 向上滑动
            int mTopOffset = getPaddingTop();
            int mLeftOffset = getPaddingLeft();
            int mMaxRight = getWidth() - getPaddingRight();
            int mMaxHeight = getHeight() - getPaddingBottom();
            int mFirstPosition = 0;
            if (getChildCount() > 0) {
                View mBotView = getChildAt(getChildCount() - 1);
                mFirstPosition = getPosition(mBotView) + 1;
                mTopOffset = getDecoratedTop(mBotView);
                mLeftOffset = getDecoratedRight(mBotView);
            }

            for (int i = mFirstPosition; i < getItemCount(); i++) {

                View item = recycler.getViewForPosition(i);
                // 重设LayoutParams.with LayoutParams.height
                item.getLayoutParams().width = mSquareWidth;
                item.getLayoutParams().height = mSquareHeight;
                addView(item);
                measureChildWithMargins(item, 0, 0);

                if (mLeftOffset + mSquareWidth > mMaxRight) {
                    // 换行
                    if (mTopOffset + mSquareHeight > mMaxHeight) {
                        // 越界
                        removeAndRecycleView(item, recycler);
                        break;
                    } else {

                        mLeftOffset = getPaddingLeft();
                        mTopOffset = mTopOffset + mSquareHeight;
                    }
                }

                layoutDecorated(item, mLeftOffset, mTopOffset, mLeftOffset + mSquareWidth, mTopOffset + mSquareHeight);

                Rect rect = new Rect(mLeftOffset, mTopOffset + mVerticalScrollOffset, mLeftOffset + mSquareWidth, mTopOffset + mSquareHeight + mVerticalScrollOffset);
                mItemRects.put(i, rect);

                mLeftOffset += mSquareWidth;
            }

        } else {
            // 向下滑动
            if (getChildCount() > 0) {
                View mFirstView = getChildAt(0);
                int mLastPosition = getPosition(mFirstView) - 1;
                for (int i = mLastPosition; i >= 0; i--) {
                    Rect rect = mItemRects.get(i);
                    if (rect.bottom - mVerticalScrollOffset < getPaddingTop()) {
                        break;
                    }

                    View item = recycler.getViewForPosition(i);
                    item.getLayoutParams().width = mSquareWidth;
                    item.getLayoutParams().height = mSquareHeight;
                    addView(item, 0);
                    measureChildWithMargins(item, 0, 0);

                    layoutDecorated(item, rect.left, rect.top - mVerticalScrollOffset, rect.right, rect.bottom - mVerticalScrollOffset);
                }
            }
        }

    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }

        if (dy > 0) {
            // 向上滑动
            View botItemView = getChildAt(getChildCount() - 1);
            int position = getPosition(botItemView);
            if (position < getItemCount() - 1) {

            } else {

                int gap = getHeight() - getPaddingBottom() - getDecoratedBottom(botItemView);
                if (gap < 0) {
                    dy = Math.min(-gap, dy);
                } else if (gap == 0) {
                    dy = 0;
                } else {
                    dy = -gap;
                }

            }

        } else {
            // 向下滑动
            if (mVerticalScrollOffset + dy < 0) {
                dy = -mVerticalScrollOffset;
            }

        }

        mVerticalScrollOffset += dy;
        offsetChildrenVertical(-dy);
        fill(recycler, state, dy);

        return dy;
    }

    public static class Builder {

        int mSquareWidth;
        int mSquareHeight;
        int mColumnNum;
        Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext.getApplicationContext();
        }

        public Builder setColumnNum(int num) {
            this.mColumnNum = num;
            return this;
        }

        public Builder setSquareWidth(int width) {
            this.mSquareWidth = width;
            return this;
        }

        public Builder setSquareHeight(int height) {
            this.mSquareHeight = height;
            return this;
        }

        public SquareTagsLayoutManager build() {
            SquareTagsLayoutManager manager = new SquareTagsLayoutManager();
            if (mSquareWidth != 0 && mSquareHeight != 0) {
                manager.mSquareHeight = mSquareHeight;
                manager.mSquareWidth = mSquareWidth;
            } else {
                mColumnNum = mColumnNum > 0 ? mColumnNum : 3;
                manager.mSquareHeight = manager.mSquareWidth = mContext.getResources().getDisplayMetrics().widthPixels / mColumnNum;
            }

            return manager;
        }
    }

}
