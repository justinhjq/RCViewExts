package ttyy.com.recyclerexts.tags;

import android.view.View;
import android.widget.Checkable;

import java.util.LinkedList;

import ttyy.com.recyclerexts.base.EXTRecyclerAdapter;
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

    public TagsAdapter(int resId) {
        super(resId);
        updateDefaultListener();
    }

    public TagsAdapter(MultiType<D> type) {
        super(type);
        updateDefaultListener();
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

    /**
     * 清空缓存
     */
    public void clearChoiceCache() {
        this.mChoiceMode.clearChoiceCache();
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
                mSelectedPositions.clear();
                mSelectedPositions.add(position);

                if (mLastSelectedTag != null) {
                    mLastSelectedTag.setChecked(false);
                }
                if (itemView instanceof Checkable) {
                    mLastSelectedTag = (Checkable) itemView;
                }
            }
        },

        /**
         * 多选模式
         */
        MultiChoice() {
            @Override
            protected void onTagItemClicked(View itemView, int position) {
                mSelectedPositions.add(position);
                if (itemView instanceof Checkable) {
                    Checkable tagItem = (Checkable) itemView;
                    tagItem.setChecked(!tagItem.isChecked());
                }
            }
        },
        /**
         * 没有选中模式
         */
        None;

        /**
         * 选中的Positions
         */
        protected LinkedList<Integer> mSelectedPositions = new LinkedList<>();

        protected void onTagItemClicked(View itemView, int position) {

        }

        public void clearChoiceCache() {
            mSelectedPositions.clear();
        }

        public LinkedList<Integer> getSelectedPositions() {
            return mSelectedPositions;
        }
    }

}
