package ttyy.com.recyclerexts.base;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Administrator
 * Date  : 2016/12/21 13:27
 * Name  : EXTRecyclerAdapter
 * Intro : Edit By Administrator
 * setAdapter/notifyDataSetChanged时，LayotuManager生命周期方法调用流程:
 * 1. removeAndRecycleAllViews(recycler) 回收到回收池
 * 2. onLayoutChildren，从回收池(非垃圾堆，垃圾堆对应scrap)取出itemview，进行onBindHolder绑定
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/21    Administrator   1.0              1.0
 */
public abstract class EXTRecyclerAdapter<D> extends RecyclerView.Adapter<EXTViewHolder> {

    protected static final int HEADER = -1000;
    protected static final int FOOTER = -1001;

    protected List<D> datas;
    protected MultiType<D> mMultiType;

    private ExtViewPool headerViewPool = new ExtViewPool();
    private ExtViewPool footerViewPool = new ExtViewPool();

    /**
     * 内置
     * 保证无论何时设置OnItemClickListener时，都会有效
     */
    protected OnItemClickListener _dftClickListener = new OnItemClickListener() {
        @Override
        public void onItemClicked(View itemView, int position) {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClicked(itemView, position);
            }
        }
    };
    protected OnItemClickListener mOnItemClickListener;

    /**
     * 内置
     * 保证无论何时设置O你ItemLongClickListen时，都会有效
     */
    protected OnItemLongClickListener _dftLongClickListener = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClicked(View itemView, int position) {
            if (mOnItemLongClickListener != null) {
                return mOnItemLongClickListener.onItemLongClicked(itemView, position);
            }
            return false;
        }
    };
    protected OnItemLongClickListener mOnItemLongClickListener;

    public EXTRecyclerAdapter(int resId) {
        mMultiType = new MultiType<D>() {
            @Override
            public int getItemType(int position, D d) {
                return 0;
            }
        }.add(0, resId);
    }

    public EXTRecyclerAdapter(MultiType<D> type) {
        mMultiType = type;
    }

    public void setDatas(List<D> datas) {
        this.datas = datas;
    }

    public List<D> getDatas() {
        return datas;
    }

    public List<D> getDatasCopy(){
        if(datas == null)
            return null;
        return new ArrayList<>(datas);
    }

    public EXTRecyclerAdapter addHeaderView(View itemView) {
        headerViewPool.addView(itemView, headerViewPool.getCount());
        return this;
    }

    public EXTRecyclerAdapter removeHeaderView(View itemView) {
        headerViewPool.removeView(itemView);
        return this;
    }

    public EXTRecyclerAdapter addFooterView(View footerView) {
        footerViewPool.addView(footerView, 0);
        return this;
    }

    public EXTRecyclerAdapter removeFooterView(View itemView) {
        footerViewPool.removeView(itemView);
        return this;
    }

    public D getDataForItemPosition(int position) {
        position -= getHeaderViewsCount();
        return getDataForPosition(position);
    }

    public D getDataForPosition(int position) {
        if (datas != null) {
            if (position >= 0 && position < datas.size()) {
                return datas.get(position);
            }
        }
        return null;
    }

    public EXTRecyclerAdapter addDataForPosition(D d, int pos, boolean notify) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        datas.add(pos, d);
        if(notify)
            notifyItemInserted(pos + getHeaderViewsCount());
        return this;
    }

    public EXTRecyclerAdapter addDataForItemPosition(D d, int pos) {
        addDataForPosition(d, pos - getHeaderViewsCount(), true);
        return this;
    }

    public EXTRecyclerAdapter addDataForItemPositionWithoutNotify(D d, int pos) {
        addDataForPosition(d, pos - getHeaderViewsCount(), false);
        return this;
    }

    public boolean removeDataForItemPosition(int position) {
        position -= getHeaderViewsCount();
        return removeDataForPosition(position, true);
    }

    public boolean removeDataForItemPositionWithoutNotify(int position){
        position -= getHeaderViewsCount();
        return removeDataForPosition(position, false);
    }

    public boolean removeDataForPosition(int position, boolean notify) {
        if (datas != null) {
            if (position >= 0 && position < datas.size()) {
                datas.remove(position);
                if(notify)
                    notifyItemRemoved(position + getHeaderViewsCount());
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        position -= getHeaderViewsCount();
        if (position < 0) {
            headerViewPool.setIndex(position + getHeaderViewsCount());
            return HEADER;
        } else if (position >= getDatasCount()) {
            footerViewPool.setIndex(position - getDatasCount());
            return FOOTER;
        } else {
            if (mMultiType.getViewTypes() > 0) {
                return mMultiType.getItemType(position + getHeaderViewsCount(), getDataForPosition(position));
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public EXTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EXTViewHolder holder = null;
        if (viewType == HEADER) {
            holder = EXTViewHolder.from(headerViewPool.getCurrentExtView());
        } else if (viewType == FOOTER) {
            holder = EXTViewHolder.from(footerViewPool.getCurrentExtView());
        } else {
            int layoutId = mMultiType.getLayoutIdForType(viewType);
            holder = EXTViewHolder.from(parent, layoutId);
        }

        if (_dftClickListener != null
                && holder.getOnItemClickListener() != _dftClickListener) {
            holder.setOnItemClickListener(_dftClickListener);
        }

        if(_dftLongClickListener != null
                && holder.getOnItemLongClickListener() != _dftLongClickListener){
            holder.setOnItemLongClickListener(_dftLongClickListener);
        }

        int itemListenerProxyViewId = getItemListenerProxyViewId(viewType);
        if(itemListenerProxyViewId != -1){
            holder.setItemListenerProxyView(itemListenerProxyViewId);
        }

        return holder;
    }

    /**
     * 转移Item ContentView点击事件 到指定的代理View上
     * @param viewType
     * @return
     */
    public int getItemListenerProxyViewId(int viewType){
        return -1;
    }

    @Override
    public final void onBindViewHolder(EXTViewHolder holder, int position) {

        onBindViewHolder(holder, position, getDataForItemPosition(position));
    }

    public abstract void onBindViewHolder(EXTViewHolder holder, int position, D data);

    /**
     * 获取Recyclerview item数量
     * @return
     */
    @Override
    public int getItemCount() {
        return getDatasCount() + getHeaderViewsCount() + getFooterViewsCount();
    }

    /**
     * 获取设置的数据集合的大小
     * @return
     */
    public int getDatasCount() {
        return datas == null ? 0 : datas.size();
    }

    /**
     * 获取Header数量
     *
     * @return
     */
    public int getHeaderViewsCount() {
        return headerViewPool.getCount();
    }

    /**
     * 获取Footer数量
     *
     * @return
     */
    public int getFooterViewsCount() {
        return footerViewPool.getCount();
    }

    public boolean isHeaderView(int itemPosition) {
        return itemPosition < headerViewPool.getCount();
    }

    public boolean isFooterView(int itemPosition) {
        return itemPosition - getHeaderViewsCount() - getDatasCount() >= 0;
    }

    /**
     * StaggeredGridLayoutMangaer Footer Header 整行行设置
     *
     * @param holder
     */
    @Override
    public final void onViewAttachedToWindow(EXTViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            int pos = holder.getLayoutPosition();
            p.setFullSpan(isHeaderView(pos) || isFooterView(pos));
        }
    }

    /**
     * GridLayoutManager Header Footer 整行设置
     * @param recyclerView
     */
    @Override
    public final void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isFooterView(position) || isHeaderView(position))
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClicked(View itemView, int position);
    }

    public interface OnItemLongClickListener{
        boolean onItemLongClicked(View itemView, int position);
    }

    private static class ExtViewPool {
        private LinkedList<View> views;

        public int index;

        public int getCount() {
            return views == null ? 0 : views.size();
        }

        public void addView(View view, int position) {
            if (views == null) {
                views = new LinkedList<>();
            }
            views.add(position, view);
        }

        public void removeView(View view) {
            if (views != null) {
                views.remove(view);
            }
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public View getCurrentExtView() {
            return views.get(index);
        }

    }

}
