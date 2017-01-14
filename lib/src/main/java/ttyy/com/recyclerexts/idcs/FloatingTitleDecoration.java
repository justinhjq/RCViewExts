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

    Rect mTitleGapRect;
    Paint mTitleGapPaint;
    TextPaint mTitleTextPaint;

    Rect mDecorationRect;
    Paint mDecorationPaint;

    public FloatingTitleDecoration() {

        mTitleGapRect = new Rect();
        mTitleGapRect.top = 80;

        mTitleGapPaint = new Paint();
        mTitleGapPaint.setStyle(Paint.Style.FILL);
        mTitleGapPaint.setAntiAlias(true);
        mTitleGapPaint.setColor(Color.parseColor("#00ff00"));

        mTitleTextPaint = new TextPaint();
        mTitleTextPaint.setTextSize(20);
        mTitleTextPaint.setAntiAlias(true);
        mTitleTextPaint.setColor(Color.parseColor("#666666"));

        mDecorationRect = new Rect();
        mDecorationRect.top = 5;

        mDecorationPaint = new Paint();
        mDecorationPaint.setStyle(Paint.Style.FILL);
        mDecorationPaint.setAntiAlias(true);
        mDecorationPaint.setColor(Color.parseColor("#ff00ff"));

    }

    public final FloatingTitleDecoration setCallback(FloatingTitleDecoration.TitleCallback callback) {
        this.mCallback = callback;
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildLayoutPosition(view);

        if (isPositionTitle(position)) {
            // 是Title
            outRect.top = mTitleGapRect.top;
            outRect.left = mTitleGapRect.left;
            outRect.right = mTitleGapRect.right;
        } else {
            outRect.top = mDecorationRect.top;
            outRect.left = mTitleGapRect.left;
            outRect.right = mTitleGapRect.right;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        for (int i = 0; i < parent.getChildCount(); i++) {
            View mChildView = parent.getChildAt(i);
            int position = parent.getChildLayoutPosition(mChildView);
            int leftOffset = 0, rightOffset = 0, topOffset = mChildView.getTop(), bottomOffset = 0;
            if (isPositionTitle(position)) {
                leftOffset = mChildView.getLeft() + mTitleGapRect.left;
                rightOffset = mChildView.getRight() - mTitleGapRect.right;
                bottomOffset = topOffset;
                topOffset -= mTitleGapRect.top;

                c.drawRect(leftOffset, topOffset, rightOffset, bottomOffset, mTitleGapPaint);

                Paint.FontMetrics fm = mTitleTextPaint.getFontMetrics();
                float textTop = topOffset + mTitleGapRect.top / 2 - (fm.top + fm.bottom) / 2;

                String mFloatingTitleStr = titleForPosition(position);
                mFloatingTitleStr = mFloatingTitleStr == null ? "" : mFloatingTitleStr;
                c.drawText(mFloatingTitleStr, leftOffset, textTop, mTitleTextPaint);
            } else {
                leftOffset = mChildView.getLeft() + mDecorationRect.left;
                rightOffset = mChildView.getRight() - mDecorationRect.right;
                bottomOffset = topOffset;
                topOffset -= mDecorationRect.top;

                c.drawRect(leftOffset, topOffset, rightOffset, bottomOffset, mDecorationPaint);
            }

        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        if (parent.getChildCount() == 0)
            return;

        int leftOffset = 0, rightOffset = 0, topOffset = 0, bottomOffset = mTitleGapRect.top;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View mChildView = parent.getChildAt(i);

            int position = parent.getChildLayoutPosition(mChildView);
            leftOffset = mChildView.getLeft() + mTitleGapRect.left;
            rightOffset = mChildView.getRight() - mTitleGapRect.right;
            topOffset = 0;
            bottomOffset = mTitleGapRect.top;

            if (isPositionTitle(position)) {
                leftOffset = mChildView.getLeft() + mTitleGapRect.left;
                rightOffset = mChildView.getRight() - mTitleGapRect.right;
                int childLayerTopOffset = mChildView.getTop() - mTitleGapRect.top;
                if (childLayerTopOffset > 0) {
                    bottomOffset = Math.min(bottomOffset, childLayerTopOffset);
                    topOffset = bottomOffset - mTitleGapRect.top;
                }

                break;
            }
        }

        c.drawRect(leftOffset, topOffset, rightOffset, bottomOffset, mTitleGapPaint);

        int mFirstTitlePosition = parent.getChildLayoutPosition(parent.getChildAt(0));
        String mFloatingTitleStr = getLatestTitleForPosition(mFirstTitlePosition);
        Paint.FontMetrics fm = mTitleTextPaint.getFontMetrics();
        float textTop = topOffset + mTitleGapRect.top / 2 - (fm.top + fm.bottom) / 2;

        mFloatingTitleStr = mFloatingTitleStr == null ? "" : mFloatingTitleStr;
        c.drawText(mFloatingTitleStr, leftOffset, textTop, mTitleTextPaint);

    }

    protected boolean isPositionTitle(int position) {
        if (mCallback != null) {
            // 缓存是否有没有
            if (mTitlePositions.contains(position)) {
                return true;
            }

            if (mCallback.isPositionTitle(position)) {
                mTitlePositions.add(position);
                return true;
            }
        }
        return false;
    }

    private HashMap<Integer, String> mPosTitleDict = new HashMap<>();
    private ArrayList<Integer> mTitlePositions = new ArrayList<>();

    protected String titleForPosition(int position) {
        if (mCallback != null) {

            String title = mPosTitleDict.get(position);
            if (TextUtils.isEmpty(title)) {
                title = mCallback.titleForPosition(position);
                mPosTitleDict.put(position, title);
            }
            return title;
        }
        return null;
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
        mPosTitleDict.clear();
        mTitlePositions.clear();
    }

    public interface TitleCallback {
        boolean isPositionTitle(int position);

        String titleForPosition(int position);
    }
}
