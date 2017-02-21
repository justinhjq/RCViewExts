package ttyy.com.recyclerexts.tags;

import android.view.View;
import android.widget.Checkable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
public class TagsAdapter<D> extends EXTRecyclerAdapter<D> {

    Mode mChoiceMode = Mode.None;

    public TagsAdapter(int resId) {
        super(resId);
        updateDefaultListener();
    }

    public TagsAdapter(MultiType<D> type) {
        super(type);
        updateDefaultListener();
    }

    @Override
    public void onBindViewHolder(EXTViewHolder holder, int position, D data) {
        if (mChoiceMode != Mode.None
                && holder.getItemView() instanceof Checkable) {
            if (mChoiceMode.isItemChecked(position)) {
                ModeItem item = mChoiceMode.getModeItem(position);
                item.mItemTarget = (Checkable) holder.getItemView();
                if (!item.mItemTarget.isChecked()) {
                    item.mItemTarget.setChecked(true);
                }
            } else {
                Checkable item = (Checkable) holder.getItemView();
                if (item.isChecked()) {
                    item.setChecked(false);
                }
            }
        }
    }

    protected void updateDefaultListener() {
        listener = new OnItemClickListener() {
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
        this.mChoiceMode = mode == null ? Mode.None : mode;
        /*
         * 枚举 是单例模式
         * 每次使用需要把上一次缓存的数据清空
         */
        this.mChoiceMode.clearChoiceCache();
    }

    public Mode getMode() {
        return mChoiceMode;
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
        mChoiceMode.setItemChecked(position, value);
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
     * 选中模式
     */
    public enum Mode {
        /**
         * 单选模式
         */
        SingleChoice() {

            Checkable mLastSelectedTag;

            @Override
            protected void onTagItemClicked(View itemView, int position) {

                mSelectedItemsDict.clear();
                if (mLastSelectedTag != null) {

                    mLastSelectedTag.setChecked(false);

                    if (mLastSelectedTag == itemView) {
                        mLastSelectedTag = null;
                        return;
                    }
                }

                if (itemView instanceof Checkable) {
                    mLastSelectedTag = (Checkable) itemView;
                    mLastSelectedTag.setChecked(true);

                    ModeItem modeItem = new ModeItem();
                    modeItem.position = position;
                    modeItem.mItemTarget = mLastSelectedTag;
                    modeItem.isChecked = true;
                    mSelectedItemsDict.put(position, modeItem);

                }
            }
        },

        /**
         * 多选模式
         */
        MultiChoice() {
            @Override
            protected void onTagItemClicked(View itemView, int position) {
                if (itemView instanceof Checkable) {
                    Checkable tagItem = (Checkable) itemView;
                    tagItem.setChecked(!tagItem.isChecked());

                    if (tagItem.isChecked()) {

                        ModeItem modeItem = new ModeItem();
                        modeItem.position = position;
                        modeItem.mItemTarget = tagItem;
                        modeItem.isChecked = true;

                        mSelectedItemsDict.put(position, modeItem);
                    } else {
                        mSelectedItemsDict.remove(position);
                    }

                }
            }
        },
        /**
         * 没有选中模式
         */
        None {
            @Override
            public void clearChoiceCache() {

            }

            @Override
            public boolean isItemChecked(int position) {
                return false;
            }

            @Override
            public void setItemChecked(int position, boolean value) {

            }
        };

        /**
         * 选中的Positions
         */
        protected HashMap<Integer, ModeItem> mSelectedItemsDict = new HashMap<>();

        protected void onTagItemClicked(View itemView, int position) {

        }

        public void clearChoiceCache() {
            for (Map.Entry<Integer, ModeItem> entry : mSelectedItemsDict.entrySet()) {

                if (entry.getValue().mItemTarget != null) {
                    entry.getValue().mItemTarget.setChecked(false);
                }

            }
            mSelectedItemsDict.clear();
        }

        public void setItemChecked(int position, boolean value) {
            if (value) {

                ModeItem item = mSelectedItemsDict.get(position);
                if (item != null) {

                    if (item.mItemTarget != null) {
                        item.mItemTarget.setChecked(true);
                        item.isChecked = true;
                    }
                } else {
                    item = new ModeItem();
                    item.position = position;
                    item.isChecked = true;
                    mSelectedItemsDict.put(position, item);
                }

            } else {

                ModeItem item = mSelectedItemsDict.get(position);
                if (item != null) {
                    if (item.mItemTarget != null) {
                        item.mItemTarget.setChecked(false);
                    }
                }
                mSelectedItemsDict.remove(position);

            }
        }

        public boolean isItemChecked(int position) {
            ModeItem item = mSelectedItemsDict.get(position);
            if (item == null) {
                return false;
            } else {
                return item.isChecked;
            }
        }

        public LinkedList<Integer> getSelectedPositions() {
            return new LinkedList<>(mSelectedItemsDict.keySet());
        }

        private ModeItem getModeItem(int position) {
            return mSelectedItemsDict.get(position);
        }
    }

    private static class ModeItem {

        int position;

        Checkable mItemTarget;

        boolean isChecked;

    }

}
