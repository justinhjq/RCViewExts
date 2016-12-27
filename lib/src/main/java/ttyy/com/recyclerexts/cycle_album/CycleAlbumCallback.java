package ttyy.com.recyclerexts.cycle_album;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;

/**
 * Author: Administrator
 * Date  : 2016/12/27 13:55
 * Name  : CycleAlbumCallback
 * Intro : 循环相册 touch事件处理
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/27    Administrator   1.0              1.0
 */
public class CycleAlbumCallback extends ItemTouchHelper.SimpleCallback {

    EXTRecyclerAdapter mAdapter;

    public CycleAlbumCallback(EXTRecyclerAdapter mAdapter){
        super(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mAdapter = mAdapter;
    }

    private CycleAlbumCallback(int dragDirs, int swipeDirs) {
        super(dragDirs, swipeDirs);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    /**
     * 滑动Item之后，如果Item达到了删除的界限，那么会触发onSwiped方法，从而进行数据变更
     * @param viewHolder
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        List<Object> mDatas = mAdapter.getDatas();
        mDatas.remove(viewHolder.getLayoutPosition());
        mAdapter.notifyItemRemoved(viewHolder.getLayoutPosition());
    }

    /**
     * 如果需要支持3.0以下版本，那么请重写onChildDrawOver，这两个方法实际含义相同
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */
    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
