package ttyy.com.recyclerexts.drag_swipe_support;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;

/**
 * Author: Administrator
 * Date  : 2016/12/27 15:37
 * Name  : SimpleSwipeCallback
 * Intro : 滑动删除
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class SimpleSwipeCallback extends ItemTouchHelper.SimpleCallback {

    EXTRecyclerAdapter mAdapter;

    public SimpleSwipeCallback(EXTRecyclerAdapter mAdapter){
        this(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT);
        this.mAdapter = mAdapter;
    }

    private SimpleSwipeCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        List<Object> mDatas = mAdapter.getDatas();
        mDatas.remove(viewHolder.getLayoutPosition());
        mAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
    }
}
