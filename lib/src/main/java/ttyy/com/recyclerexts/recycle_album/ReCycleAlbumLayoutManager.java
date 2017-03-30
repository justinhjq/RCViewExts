package ttyy.com.recyclerexts.recycle_album;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Author: Administrator
 * Date  : 2016/12/27 13:52
 * Name  : ReCycleAlbumLayoutManager
 * Intro : 循环相册 布局
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class ReCycleAlbumLayoutManager extends RecyclerView.LayoutManager {

    ReCycleAlbumConfig mConfig = ReCycleAlbumConfig.getInstance();

    public ReCycleAlbumLayoutManager(){

    }

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

        int mFirstVisiPos = getItemCount() > mConfig.getCycleCount() ? getItemCount() - mConfig.getCycleCount() : 0;
        int mLastVisiPos = getItemCount() - 1;
        int topOffset = getPaddingTop();
        int leftOffset = getPaddingLeft();
        // 最后的排列在最上面
        // 重叠排列
        // 居中展示
        for(int i = mFirstVisiPos; i <= mLastVisiPos; i++){

            View scrap = recycler.getViewForPosition(i);
            addView(scrap);
            measureChildWithMargins(scrap, 0, 0);

            leftOffset = (getWidth() - getDecoratedMeasurementHorizontal(scrap)) / 2;
            topOffset = (getHeight() - getDecoratedMeasurementVertical(scrap)) / 2;

            layoutDecoratedWithMargins(scrap, leftOffset, topOffset, leftOffset + getDecoratedMeasurementHorizontal(scrap), topOffset + getDecoratedMeasurementVertical(scrap));

            int level = mLastVisiPos - i;
            if(level >= 0){
                scrap.setTranslationY(mConfig.getYGapUnit() * level);
                scrap.setScaleX(1 - mConfig.getScaleUnit() * level);
                scrap.setScaleY(1 - mConfig.getScaleUnit() * level);
            }
        }

    }

    private int getDecoratedMeasurementHorizontal(View view){
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredWidth(view) + params.leftMargin + params.rightMargin;
    }

    private int getDecoratedMeasurementVertical(View view){
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        return getDecoratedMeasuredHeight(view) + params.topMargin + params.bottomMargin;
    }
}
