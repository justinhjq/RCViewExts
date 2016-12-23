package ttyy.com.recyclerexts.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Author: Administrator
 * Date  : 2016/12/21 13:27
 * Name  : EXTRecyclerAdapter
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/21    Administrator   1.0              1.0
 */
public abstract class EXTRecyclerAdapter<D> extends RecyclerView.Adapter<EXTViewHolder> {

    protected List<D> datas;
    protected MultiType<D> mMultiType;
    /**
     * 内置
     * 保证无论何时设置OnItemClickListener时，都会有效
     */
    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClicked(View itemView, int position) {
            if(mOnItemClickListener != null){
                mOnItemClickListener.onItemClicked(itemView, position);
            }
        }
    };
    protected OnItemClickListener mOnItemClickListener;

    public EXTRecyclerAdapter(int resId){
        mMultiType = new MultiType<D>() {
            @Override
            public int getItemType(int position, D d) {
                return 0;
            }
        }.add(0, resId);
    }

    public EXTRecyclerAdapter(MultiType<D> type){
        mMultiType = type;
    }

    public void setDatas(List<D> datas){
        this.datas = datas;
    }

    public D getDataForPosition(int position){
        if(datas != null){
            if(position > 0 && position < datas.size()){
                return datas.get(position);
            }
        }
        return null;
    }

    
    public boolean removeDataForPosition(int position){
        if(datas != null){
            if(position > 0 && position < datas.size()){
                datas.remove(position);
                notifyItemRemoved(position);
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if(mMultiType.getViewTypes() > 0){
            return mMultiType.getItemType(position, getDataForPosition(position));
        }
        return super.getItemViewType(position);
    }

    @Override
    public EXTViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId = mMultiType.getLayoutIdForType(viewType);
        EXTViewHolder holder = EXTViewHolder.from(parent, layoutId);
        holder.setOnItemClickListener(listener);
        return EXTViewHolder.from(parent, layoutId);
    }

    @Override
    public void onBindViewHolder(EXTViewHolder holder, int position) {
        onBindViewHolder(holder, position, datas.get(position));
    }

    public abstract void onBindViewHolder(EXTViewHolder holder, int position, D data);

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClicked(View itemView, int position);
    }
}
