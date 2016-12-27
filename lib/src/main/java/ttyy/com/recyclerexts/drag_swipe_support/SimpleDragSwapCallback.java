package ttyy.com.recyclerexts.drag_swipe_support;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.Collections;
import java.util.List;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;

/**
 * Author: Administrator
 * Date  : 2016/12/27 15:27
 * Name  : 拖拽替换
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class SimpleDragSwapCallback extends ItemTouchHelper.SimpleCallback{

    EXTRecyclerAdapter mAdapter;

    public SimpleDragSwapCallback(EXTRecyclerAdapter mAdapter){
        this(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0);
        this.mAdapter = mAdapter;
    }

    private SimpleDragSwapCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    /**
     * 不支持滑动删除动作
     * swipeDirs 0 同返回false效果
     * @return
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    /**
     * Item拖拽到其他Item位置时触发onMove
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return true 触发onMoved，onMoved不会新建一个针对ItemView的Bitmap进行位置变换UI效果处理，
     *              而是直接针对当前被拖拽ItemView修改位置属性，节省内存
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        // 数据源变换
        List<Object> mDatas = mAdapter.getDatas();
        Collections.swap(mDatas, viewHolder.getLayoutPosition(), target.getLayoutPosition());

        // 进行Item位置变换，提供默认的变换动画效果
        mAdapter.notifyItemMoved(viewHolder.getLayoutPosition(), target.getLayoutPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }
}
