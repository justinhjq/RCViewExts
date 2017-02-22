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
            boolean isChecked = isItemChecked(position);
            if (isChecked) {
                ((Checkable) holder.getItemView()).setChecked(true);
            } else {
                ((Checkable) holder.getItemView()).setChecked(false);
            }
        }
    }

    protected void updateDefaultListener() {

        mChoiceMode.mAdapter = this;

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
        /*
         * 枚举 是单例模式
         * 每次使用需要把上一次缓存的数据清空
         */
        this.mChoiceMode.clearChoiceCache();

        this.mChoiceMode = mode == null ? Mode.None : mode;
        this.mChoiceMode.mAdapter = this;
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
                super.clearChoiceCache();
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
            public void setItemChecked(int position, View itemView, boolean value) {

            }
        };

        /**
         * 选中的Positions
         */
        protected HashMap<Integer, ModeItem> mSelectedItemsDict = new HashMap<>();

        protected TagsAdapter mAdapter;

        protected void onTagItemClicked(View itemView, int position) {

        }

        public void clearChoiceCache() {
            for (Map.Entry<Integer, ModeItem> entry : mSelectedItemsDict.entrySet()) {

                entry.getValue().isChecked = false;

            }
            mSelectedItemsDict.clear();
            mAdapter.notifyDataSetChanged();
        }

        public void setItemChecked(int position, View itemView, boolean value) {
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
        boolean isChecked;

    }

}
