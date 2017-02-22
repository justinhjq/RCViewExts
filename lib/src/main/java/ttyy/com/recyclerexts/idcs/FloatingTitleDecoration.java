package ttyy.com.recyclerexts.idcs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Author: Administrator
 * Date  : 2017/01/14 10:55
 * Name  : FloatingTitleDecoration
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2017/01/14    Administrator   1.0              1.0
 */
public class FloatingTitleDecoration extends RecyclerView.ItemDecoration {

    TitleCallback mCallback;

    Rect mTitleRect;
    Paint mTitleRectBackgroundPaint;
    TextPaint mTitleRectTextPaint;
    int mTitleTextLeftOffset;

    Rect mNormalDividerRect;
    Paint mNormalDividerRectBackgroundPaint;

    public FloatingTitleDecoration() {

        mTitleRect = new Rect();
        mTitleRect.top = 80;

        mTitleRectBackgroundPaint = new Paint();
        mTitleRectBackgroundPaint.setStyle(Paint.Style.FILL);
        mTitleRectBackgroundPaint.setAntiAlias(true);
        mTitleRectBackgroundPaint.setColor(Color.parseColor("#00ff00"));

        mTitleRectTextPaint = new TextPaint();
        mTitleRectTextPaint.setTextSize(20);
        mTitleRectTextPaint.setAntiAlias(true);
        mTitleRectTextPaint.setColor(Color.parseColor("#666666"));

        mNormalDividerRect = new Rect();
        mNormalDividerRect.top = 5;

        mNormalDividerRectBackgroundPaint = new Paint();
        mNormalDividerRectBackgroundPaint.setStyle(Paint.Style.FILL);
        mNormalDividerRectBackgroundPaint.setAntiAlias(true);
        mNormalDividerRectBackgroundPaint.setColor(Color.parseColor("#ff00ff"));

    }

    public final FloatingTitleDecoration setCallback(FloatingTitleDecoration.TitleCallback callback) {
        this.mCallback = callback;
        return this;
    }

    public FloatingTitleDecoration setTitleBackgroundColor(int color){
        mTitleRectBackgroundPaint.setColor(color);
        return this;
    }

    public FloatingTitleDecoration setTitleHeight(int pixels){
        mTitleRect.top = pixels;
        return this;
    }

    public FloatingTitleDecoration setTitleTextColor(int color){
        mTitleRectTextPaint.setColor(color);
        return this;
    }

    public FloatingTitleDecoration setTitleTextSize(int pixels){
        mTitleRectTextPaint.setTextSize(pixels);
        return this;
    }

    public FloatingTitleDecoration setTitleTextLeftOffset(int pixels){
        mTitleTextLeftOffset = pixels;
        return this;
    }

    public FloatingTitleDecoration setNormalDividerHeight(int pixels){
        mNormalDividerRect.top = pixels;
        return this;
    }

    public FloatingTitleDecoration setNormalDividerBackgroundColor(int color){
        mNormalDividerRectBackgroundPaint.setColor(color);
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        // getLayout 没有 getAdapter精准 相对于Item在数据上的Index
        int position = parent.getChildAdapterPosition(view);
        if (isPositionTitle(position)) {
            // 是Title
            outRect.top = mTitleRect.top;
            outRect.left = mTitleRect.left;
            outRect.right = mTitleRect.right;
        } else {
            outRect.top = mNormalDividerRect.top;
            outRect.left = mNormalDividerRect.left;
            outRect.right = mNormalDividerRect.right;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View mChildView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(mChildView);
            // getTop 此时getTop是View所在绘制空间预留出Decoration大小之后的top 即outerRect.top
            // 同理 getLeft getRight getBottom
            int leftOffset = 0, rightOffset = 0, topOffset = mChildView.getTop(), bottomOffset = 0;
            if (isPositionTitle(position)) {
                leftOffset = mChildView.getLeft();
                rightOffset = mChildView.getRight();
                bottomOffset = topOffset;
                topOffset -= mTitleRect.top;

                c.drawRect(leftOffset, topOffset, rightOffset, bottomOffset, mTitleRectBackgroundPaint);

                Paint.FontMetrics fm = mTitleRectTextPaint.getFontMetrics();
                float textTop = topOffset + mTitleRect.top / 2 - (fm.top + fm.bottom) / 2;

                String mFloatingTitleStr = titleForPosition(position);
                mFloatingTitleStr = mFloatingTitleStr == null ? "" : mFloatingTitleStr;

                leftOffset += mTitleTextLeftOffset;
                c.drawText(mFloatingTitleStr, leftOffset, textTop, mTitleRectTextPaint);
            } else {
                leftOffset = mChildView.getLeft();
                rightOffset = mChildView.getRight();
                bottomOffset = topOffset;
                topOffset -= mNormalDividerRect.top;

                c.drawRect(leftOffset, topOffset, rightOffset, bottomOffset, mNormalDividerRectBackgroundPaint);
            }

        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (parent.getChildCount() == 0)
            return;

        int leftOffset = 0, rightOffset = 0, topOffset = 0, bottomOffset = mTitleRect.top;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View mChildView = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(mChildView);
            leftOffset = mChildView.getLeft();
            rightOffset = mChildView.getRight();
            topOffset = 0;
            bottomOffset = mTitleRect.top;

            if (isPositionTitle(position)) {
                leftOffset = mChildView.getLeft();
                rightOffset = mChildView.getRight();
                int childLayerTopOffset = mChildView.getTop() - mTitleRect.top;
                if (childLayerTopOffset > 0) {
                    bottomOffset = Math.min(bottomOffset, childLayerTopOffset);
                    topOffset = bottomOffset - mTitleRect.top;
                }

                break;
            }
        }

        c.drawRect(leftOffset, topOffset, rightOffset, bottomOffset, mTitleRectBackgroundPaint);


        Paint.FontMetrics fm = mTitleRectTextPaint.getFontMetrics();
        float textTop = topOffset + mTitleRect.top / 2 - (fm.top + fm.bottom) / 2;

        int mFirstTitlePosition = parent.getChildAdapterPosition(parent.getChildAt(0));
        String mFloatingTitleStr = getLatestTitleForPosition(mFirstTitlePosition);
        mFloatingTitleStr = mFloatingTitleStr == null ? "" : mFloatingTitleStr;

        leftOffset += mTitleTextLeftOffset;
        c.drawText(mFloatingTitleStr, leftOffset, textTop, mTitleRectTextPaint);

    }

    protected boolean isPositionTitle(int position) {
        if (mCallback != null) {
            // 缓存是否有没有
            if (mTitleDataUseCache
                    && mCachedTitlePositions.contains(position)) {
                return true;
            }

            if (mCallback.isPositionTitle(position)) {
                if(mTitleDataUseCache)
                    mCachedTitlePositions.add(position);
                return true;
            }
        }
        return false;
    }

    private HashMap<Integer, String> mCachedPosTitleDict = new HashMap<>();
    private ArrayList<Integer> mCachedTitlePositions = new ArrayList<>();
    private boolean mTitleDataUseCache = true;

    protected String titleForPosition(int position) {
        if (mCallback != null) {

            String title = mCachedPosTitleDict.get(position);
            if (mTitleDataUseCache
                    && TextUtils.isEmpty(title)) {
                title = mCallback.titleForPosition(position);
                mCachedPosTitleDict.put(position, title);
            }
            return title;
        }
        return null;
    }

    public FloatingTitleDecoration setTitleDataUseCache(boolean value){
        this.mTitleDataUseCache = value;
        return this;
    }

    public FloatingTitleDecoration updateCacheWhenInsertItem(int position){
        if(!mTitleDataUseCache
                || mCachedPosTitleDict.size() == 0
                || mCachedTitlePositions.size() == 0){
            return this;
        }

        // 初始化ArrayList的容纳能力
        ArrayList<Integer> copy = new ArrayList<>(Arrays.asList(new Integer[mCachedTitlePositions.size()]));
        Collections.copy(copy, mCachedTitlePositions);

        HashMap<Integer, String> copyDict = new HashMap<>(mCachedPosTitleDict);

        mCachedTitlePositions.clear();
        mCachedPosTitleDict.clear();

        for(int tmp : copy){
            if(tmp < position){

            }else {
                String title = copyDict.get(tmp);
                tmp += 1;
                mCachedPosTitleDict.put(tmp, title);

            }
            mCachedTitlePositions.add(tmp);
        }

        return this;
    }

    public FloatingTitleDecoration updateCacheWhenRemoveItem(int position){
        if(!mTitleDataUseCache
                || mCachedPosTitleDict.size() == 0
                || mCachedTitlePositions.size() == 0){
            return this;
        }

        ArrayList<Integer> copy = new ArrayList<>(Arrays.asList(new Integer[mCachedTitlePositions.size()]));
        Collections.copy(copy, mCachedTitlePositions);

        HashMap<Integer, String> copyDict = new HashMap<>(mCachedPosTitleDict);

        mCachedTitlePositions.clear();
        mCachedPosTitleDict.clear();

        for(int tmp : copy){
            if(tmp < position){

            }else {
                String title = copyDict.get(tmp);
                tmp -= 1;
                mCachedPosTitleDict.put(tmp, title);

            }
            mCachedTitlePositions.add(tmp);
        }

        return this;
    }

    private String getLatestTitleForPosition(int position){

        for(int i = position; i>=0; i--){
            if(isPositionTitle(i)){
                return titleForPosition(i);
            }
        }

        return null;
    }

    /**
     * 数据变动时，需要清空缓存
     */
    public final void clearCaches() {
        mCachedPosTitleDict.clear();
        mCachedTitlePositions.clear();
    }

    public interface TitleCallback {
        boolean isPositionTitle(int position);

        String titleForPosition(int position);
    }
}
