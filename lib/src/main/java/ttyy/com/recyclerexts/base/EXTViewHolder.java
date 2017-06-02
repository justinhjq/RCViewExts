package ttyy.com.recyclerexts.base;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author: Administrator
 * Date  : 2016/12/21 13:26
 * Name  : EXTViewHolder
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/21    Administrator   1.0              1.0
 */
public class EXTViewHolder extends RecyclerView.ViewHolder{

    protected View mItemView;
    protected int mLayoutId;
    private SparseArray<View> mHolderViews;
    protected EXTRecyclerAdapter.OnItemClickListener mOnItemClickListener;
    protected EXTRecyclerAdapter.OnItemLongClickListener mOnItemLongClickListener;

    private View.OnClickListener _dftOnClickListener;
    private View.OnLongClickListener _dftOnLongClickListener;

    private EXTViewHolder(View itemView, int layoutId) {
        super(itemView);
        this.mItemView = itemView;
        this.mLayoutId = layoutId;
        this.mHolderViews = new SparseArray<>();

        this._dftOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onItemClicked(v, getLayoutPosition());
                }
            }
        };

        this._dftOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mOnItemLongClickListener != null){
                    return mOnItemLongClickListener.onItemLongClicked(v, getLayoutPosition());
                }
                return false;
            }
        };

        this.mItemView.setOnClickListener(_dftOnClickListener);
        this.mItemView.setOnLongClickListener(_dftOnLongClickListener);
    }

    public static EXTViewHolder from(ViewGroup parent, int layoutId){
        // 这种方式 最上层layout的layout_width layout_height属性也可以映射出来
        View itemView = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        EXTViewHolder holder = new EXTViewHolder(itemView, layoutId);
        return holder;
    }

    public static EXTViewHolder from(View itemView){
        EXTViewHolder holder = new EXTViewHolder(itemView, -1);
        return holder;
    }

    public <T extends View> T findViewById(int id){
        View target = mHolderViews.get(id);
        if(target == null){
            target = mItemView.findViewById(id);
            mHolderViews.put(id, target);
        }

        return (T)target;
    }

    public void setText(int id, CharSequence text){
        TextView tv = findViewById(id);
        tv.setText(text);
    }

    public void setTextColor(int id, int color){
        TextView tv = findViewById(id);
        tv.setTextColor(color);
    }

    public void setImageResouce(int id, int resId){
        ImageView iv = findViewById(id);
        iv.setImageResource(resId);
    }

    public void setBackgroundResource(int id, int resId){
        View view = findViewById(id);
        view.setBackgroundResource(resId);
    }

    public void setBackgroundColor(int id, int color){
        View view = findViewById(id);
        view.setBackgroundColor(color);
    }

    public View getItemView(){
        return this.mItemView;
    }

    public void setVisibility(int id, int value){
        findViewById(id).setVisibility(value);
    }

    public int getVisibility(int id){
        return findViewById(id).getVisibility();
    }

    public void setOnItemClickListener(EXTRecyclerAdapter.OnItemClickListener listener){
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(EXTRecyclerAdapter.OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }

    public void setItemListenerProxyView(int itemId){

        View proxyItemView = this.mItemView.findViewById(itemId);

        if(proxyItemView != null){
            proxyItemView.setOnClickListener(_dftOnClickListener);
            proxyItemView.setOnLongClickListener(_dftOnLongClickListener);

            this.mItemView.setOnClickListener(null);
            this.mItemView.setOnLongClickListener(null);
        }else {

            Log.w("EXTViewHolder", "ProxyItemView Id Not Exists!");
        }

    }

    public EXTRecyclerAdapter.OnItemClickListener getOnItemClickListener(){
        return this.mOnItemClickListener;
    }

    public EXTRecyclerAdapter.OnItemLongClickListener getOnItemLongClickListener(){
        return this.mOnItemLongClickListener;
    }

}
