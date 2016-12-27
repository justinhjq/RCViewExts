package ttyy.com.recyclerexts.cycle_album;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: Administrator
 * Date  : 2016/12/27 13:52
 * Name  : CycleAlbumLayoutManager
 * Intro : 循环相册 布局
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class CycleAlbumLayoutManager extends RecyclerView.LayoutManager {

    private int mCycleCount = 4;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * notifyDataSetChanged/setAdapter
     * 会使得LayoutManager中的ItemView被移除并且回收到回收池中，即触发removeAndRecyclerAllViews(recycler)
     * 这是取出的view都需要进行适配器的onBindHolder数据绑定
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        // 没有Item
        if(getItemCount() == 0){
            return;
        }

        // preLayout不需要处理
        if(getChildCount() ==0 && state.isPreLayout()){
            return;
        }

        // onMeasure 会调用两次onLayoutChildren
        // 第二次布局就没必要再进行一次数据绑定了，所以直接回收到垃圾堆
        // 从垃圾堆中取出的view，不需要进行数据绑定
        // 回收进垃圾堆中的view意味着他们只是暂时的脱离Layout，很快就就会重新进入Layout
        detachAndScrapAttachedViews(recycler);

        fill(recycler, state);
    }

    private void fill(RecyclerView.Recycler recycler, RecyclerView.State state){

        int mFirstVisiPos = getItemCount() > mCycleCount ? getItemCount() - mCycleCount : 0;
        int mLastVisiPos = getItemCount();

    }

    private int getDecoratedMeasurementHorizontal(View view){
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
    }

    private int getDecoratedMeasurementVertical(View view){
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.topMargin + params.bottomMargin;
    }
}
