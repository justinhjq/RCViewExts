package ttyy.com.recyclerexts.idcs;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Author: Administrator
 * Date  : 2016/12/28 18:03
 * Name  : SimpleVerticalItemDecoration
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/28    Administrator   1.0              1.0
 */
public class SimpleVerticalItemDecoration extends RecyclerView.ItemDecoration {

    int mDividerHeight = 4;
    int mDividerColor = Color.parseColor("#efefef");
    Paint mDividerPaint;

    public SimpleVerticalItemDecoration(){
        mDividerPaint = new Paint();
        mDividerPaint.setColor(mDividerColor);
    }

    public SimpleVerticalItemDecoration setDividerHeight(int mDividerHeight){
        this.mDividerHeight = mDividerHeight;
        return this;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int viewPosition = parent.getChildLayoutPosition(view);
        int lastPosition = state.getItemCount() - 1;
        if(viewPosition == lastPosition){
            outRect.bottom = 0;
        }else {
            outRect.bottom = mDividerHeight;
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int pos = parent.getChildLayoutPosition(view);
            if(pos != state.getItemCount() - 1){
                float top = view.getBottom();
                float bottom = view.getBottom() + mDividerHeight;
                c.drawRect(left, top, right, bottom, mDividerPaint);
            }
        }
    }
}
