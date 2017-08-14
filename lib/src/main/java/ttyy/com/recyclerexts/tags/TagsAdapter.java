package ttyy.com.recyclerexts.tags;

import android.view.View;
import android.widget.Checkable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
import ttyy.com.recyclerexts.base.EXTViewHolder;
import ttyy.com.recyclerexts.base.MultiType;

/**
 * Author: Administrator
 * Date  : 2016/12/26 14:43
 * Name  : TagsAdapter
 * Intro : 支持选中模式的标签流RecyclerView的适配器
 * setChoiceMode() 设置选中模式
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/26    Administrator   1.0              1.0
 */
public abstract class TagsAdapter<D> extends EXTRecyclerAdapter<D> {

    Mode mChoiceMode = Mode.None;

    int mCheckMode = Mode.BEFORE_BIND_DATA;

    public TagsAdapter(int resId) {
        super(resId);
        updateDefaultListener();
    }

    public TagsAdapter(MultiType<D> type) {
        super(type);
        updateDefaultListener();
    }

    @Override
    public final void onBindViewHolder(EXTViewHolder holder, int position, D data) {

        switch (mChoiceMode.getCheckMode()){
            case Mode.AFTER_BIND_DATA:
                // 绑定数据之后 进行checked item设置
                onBindTagViewHolder(holder, position, data);
                setChecked(holder, position);
                break;
            case Mode.BEFORE_BIND_DATA:
                // 绑定数据之前 进行checked item设置
                setChecked(holder, position);
                onBindTagViewHolder(holder, position, data);
                break;
            case Mode.NONE_CHECK:
                // 没有检查模式 直接绑定数据
                onBindTagViewHolder(holder, position, data);
                break;
            default:
                onBindTagViewHolder(holder, position, data);
                break;
        }

    }

    private void setChecked(EXTViewHolder holder, int position){
        if (holder.getItemView() instanceof Checkable) {
            boolean isChecked = isItemChecked(position);
            if (isChecked) {
                ((Checkable) holder.getItemView()).setChecked(true);
            } else {
                ((Checkable) holder.getItemView()).setChecked(false);
            }
        }
    }

    public abstract void onBindTagViewHolder(EXTViewHolder holder, int position, D data);

    protected void updateDefaultListener() {

        mChoiceMode.mAdapter = this;

        _dftClickListener = new OnItemClickListener() {
            @Override
            public void onItemClicked(View itemView, int position) {
                // 不管有没有设置Item点击事件 选择模式都应该响应每个Item的点击事件
                mChoiceMode.onTagItemClicked(itemView, position);
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClicked(itemView, position);
                }
            }
        };
    }

    /**
     * 设置选中模式
     *
     * @param mode
     */
    public void setChoiceMode(Mode mode) {
        /*
         * 枚举 是单例模式
         * 每次使用需要把上一次缓存的数据清空
         */
        this.mChoiceMode.clearChoiceCache();
        this.mChoiceMode = mode == null ? Mode.None : mode;
        this.mChoiceMode.mAdapter = this;
        // 清空上一次的缓存 但是不更新RecyclerView
        this.mChoiceMode.mSelectedItemsDict.clear();
        // 设置检查时机
        this.mChoiceMode.setCheckMode(mCheckMode);
    }

    public TagsAdapter setCheckTimeMode(int modeValue){
        mCheckMode = modeValue;
        mChoiceMode.setCheckMode(modeValue);
        return this;
    }

    public Mode getMode() {
        return mChoiceMode;
    }

    /**
     * 全选
     */
    public void chooseAll(){
        if(mChoiceMode != Mode.MultiChoice){
            return;
        }

        for(int i = 0 ; i < getItemCount(); i++){
            ModeItem modeItem = new ModeItem();
            modeItem.position = i;
            modeItem.isChecked = true;
            mChoiceMode.mSelectedItemsDict.put(i, modeItem);
        }
        notifyDataSetChanged();
    }

    /**
     * 清空选中状态
     */
    public void clearChooseStatus(){
        clearChoiceCache();
    }

    /**
     * 清空缓存
     */
    public void clearChoiceCache() {
        this.mChoiceMode.clearChoiceCache();
    }

    public boolean isItemChecked(int position) {
        return mChoiceMode.isItemChecked(position);
    }

    public void setItemChecked(int position, boolean value) {
        if (mChoiceMode == Mode.None) {
            return;
        } else {
            mChoiceMode.setItemChecked(position, null, value);
        }
    }

    /**
     * 获取选中的位置
     * 多选 默认最后一个选中的位置
     * 单选 当前选中的位置
     * 无模式 -1
     *
     * @return
     */
    public int getSelectedPosition() {
        LinkedList<Integer> mSelectedPositions = getSelectedPositions();
        return mSelectedPositions.size() > 0 ? mSelectedPositions.getLast() : -1;
    }

    /**
     * 获取选中的位置 MultiChoice
     *
     * @return
     */
    public LinkedList<Integer> getSelectedPositions() {
        return mChoiceMode.getSelectedPositions();
    }

    /**
     * choice setChecked 时机
     */
    public enum CheckMode{
        /**
         * bind数据之后
         */
        AfterBind,
        /**
         * bind数据之前
         */
        BeforeBind,
        /**
         * 不check
         */
        None;
    }

    /**
     * 选中模式
     */
    public enum Mode {
        /**
         * 单选模式
         */
        SingleChoice() {

            ModeItem modeItem = new ModeItem() {
                {
                    position = -1;
                    isChecked = false;
                }
            };

            @Override
            protected void onTagItemClicked(View itemView, int position) {

                if (modeItem.position == position) {
                    setItemChecked(position, itemView, false);
                } else {
                    setItemChecked(position, itemView, true);
                }
            }

            @Override
            public void setItemChecked(int position, View itemView, boolean value) {
                if (modeItem.position == -1) {

                    modeItem.position = position;
                    modeItem.isChecked = value;


                } else if (modeItem.position == position) {

                    modeItem.position = -1;
                    modeItem.isChecked = false;

                } else {

                    mAdapter.notifyItemChanged(modeItem.position);

                    modeItem.position = position;
                    modeItem.isChecked = value;
                }

                if (itemView != null) {
                    if (itemView instanceof Checkable) {
                        Checkable tagItem = (Checkable) itemView;
                        tagItem.setChecked(value);
                    }
                } else {
                    mAdapter.notifyItemChanged(position);
                }
            }

            @Override
            public boolean isItemChecked(int position) {
                return modeItem.position == position;
            }

            public ModeItem getModeItem(int position) {
                if (modeItem.position == position) {
                    return modeItem;
                }
                return null;
            }

            @Override
            public LinkedList<Integer> getSelectedPositions() {
                LinkedList<Integer> is = new LinkedList<>();
                if(modeItem.position != -1)
                    is.add(modeItem.position);
                return is;
            }

            @Override
            public void clearChoiceCache() {
            }
        },

        /**
         * 多选模式
         */
        MultiChoice() {
            @Override
            protected void onTagItemClicked(View itemView, int position) {
                if(isItemChecked(position)){
                    setItemChecked(position, itemView, false);
                }else {
                    setItemChecked(position, itemView, true);
                }
            }

            @Override
            protected void setItemChecked(int position, View itemView, boolean value) {
                ModeItem modeItem = mSelectedItemsDict.get(position);
                if (modeItem != null) {

                    modeItem.isChecked = value;

                    if(!modeItem.isChecked){
                        mSelectedItemsDict.remove(position);
                    }

                } else {

                    modeItem = new ModeItem();
                    modeItem.position = position;
                    modeItem.isChecked = value;
                    mSelectedItemsDict.put(position, modeItem);

                }


                if (itemView != null) {
                    if (itemView instanceof Checkable) {
                        Checkable tagItem = (Checkable) itemView;
                        tagItem.setChecked(value);
                    }
                } else {
                    mAdapter.notifyItemChanged(position);
                }
            }

            @Override
            protected void clearChoiceCache() {
                mSelectedItemsDict.clear();
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected boolean isItemChecked(int position) {
                ModeItem item = mSelectedItemsDict.get(position);
                if (item == null) {
                    return false;
                } else {
                    return item.isChecked;
                }
            }

            @Override
            protected LinkedList<Integer> getSelectedPositions() {
                return new LinkedList<>(mSelectedItemsDict.keySet());
            }

            @Override
            protected ModeItem getModeItem(int position) {
                return mSelectedItemsDict.get(position);
            }
        },
        /**
         * 没有选中模式
         */
        None {
            @Override
            protected void onTagItemClicked(View itemView, int position) {

            }

            @Override
            public void clearChoiceCache() {

            }

            @Override
            public boolean isItemChecked(int position) {
                return false;
            }

            @Override
            protected LinkedList<Integer> getSelectedPositions() {
                return new LinkedList<>();
            }

            @Override
            protected ModeItem getModeItem(int position) {
                return null;
            }

            @Override
            public void setItemChecked(int position, View itemView, boolean value) {

            }
        };

        /**
         * 选中的Positions
         */
        protected HashMap<Integer, ModeItem> mSelectedItemsDict = new HashMap<>();

        protected TagsAdapter mAdapter;

        /**
         * 点击Item
         * @param itemView
         * @param position
         */
        protected abstract void onTagItemClicked(View itemView, int position);

        /**
         * 清空选中列表
         */
        protected abstract void clearChoiceCache();

        /**
         * 设置Item 选中
         * @param position
         * @param itemView
         * @param value
         */
        protected abstract void setItemChecked(int position, View itemView, boolean value);

        /**
         * Item是否被选中
         * @param position
         * @return
         */
        protected abstract boolean isItemChecked(int position);

        /**
         * 获取选中的位置
         * @return
         */
        protected abstract LinkedList<Integer> getSelectedPositions();

        /**
         * 获取选中节点具体数据
         * @param position
         * @return
         */
        protected abstract ModeItem getModeItem(int position);

        /**
         * 检查时机
         */
        public static final int AFTER_BIND_DATA = 1;
        public static final int NONE_CHECK = 0;
        public static final int BEFORE_BIND_DATA = -1;
        int mCheckMode = BEFORE_BIND_DATA;

        protected final Mode setCheckMode(int value){
            if(value > 0){
                mCheckMode = AFTER_BIND_DATA;
            }else if(value < 0){
                mCheckMode = BEFORE_BIND_DATA;
            }else {
                mCheckMode = NONE_CHECK;
            }

            return this;
        }

        protected final int getCheckMode(){
            return mCheckMode;
        }
    }

    private static class ModeItem {

        int position;
        boolean isChecked;

    }

}
