package ttyy.com.recyclerexts.tags;

import android.view.View;
import android.widget.Checkable;

import java.util.HashMap;
import java.util.LinkedList;

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

    ModeImpl mChoiceModeImpl;

    public TagsAdapter(int resId) {
        super(resId);
        mChoiceModeImpl = ModeImpl.createModeImpl(Mode.None, this);
        updateDefaultListener();
    }

    public TagsAdapter(MultiType<D> type) {
        super(type);
        mChoiceModeImpl = ModeImpl.createModeImpl(Mode.None, this);
        updateDefaultListener();
    }

    @Override
    public final void onBindViewHolder(EXTViewHolder holder, int position, D data) {

        switch (mChoiceModeImpl.getCheckTime()) {
            case AfterBind:
                // 绑定数据之后 进行checked item设置
                onBindTagViewHolder(holder, position, data);
                setChecked(holder, position);
                break;
            case BeforeBind:
                // 绑定数据之前 进行checked item设置
                setChecked(holder, position);
                onBindTagViewHolder(holder, position, data);
                break;
            case None:
                // 没有检查模式 直接绑定数据
                onBindTagViewHolder(holder, position, data);
                break;
            default:
                onBindTagViewHolder(holder, position, data);
                break;
        }

    }

    private void setChecked(EXTViewHolder holder, int position) {
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

        _dftClickListener = new OnItemClickListener() {
            @Override
            public void onItemClicked(View itemView, int position) {
                // 不管有没有设置Item点击事件 选择模式都应该响应每个Item的点击事件
                mChoiceModeImpl.onTagItemClicked(itemView, position);
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
        this.mChoiceModeImpl.clearChoiceCache();

        CheckTime mCachedCheckTime = mChoiceModeImpl.getCheckTime();
        this.mChoiceModeImpl = ModeImpl.createModeImpl(mode, this);
        this.mChoiceModeImpl.setCheckTime(mCachedCheckTime);
    }

    public TagsAdapter setCheckTime(CheckTime modeValue) {
        mChoiceModeImpl.setCheckTime(modeValue);
        return this;
    }

    public Mode getMode() {
        return mChoiceModeImpl.getChoiceMode();
    }

    /**
     * 全选
     */
    public void chooseAll() {
        if (mChoiceModeImpl.getChoiceMode() != Mode.MultiChoice) {
            return;
        }

        for (int i = 0; i < getItemCount(); i++) {
            ModeItem modeItem = new ModeItem();
            modeItem.position = i;
            modeItem.isChecked = true;
            mChoiceModeImpl.mSelectedItemsDict.put(i, modeItem);
        }
        notifyDataSetChanged();
    }

    /**
     * 清空选中状态
     */
    public void clearChooseStatus() {
        clearChoiceCache();
    }

    /**
     * 清空缓存
     */
    public void clearChoiceCache() {
        this.mChoiceModeImpl.clearChoiceCache();
    }

    public boolean isItemChecked(int position) {
        return mChoiceModeImpl.isItemChecked(position);
    }

    public void setItemChecked(int position, boolean value) {
        if (mChoiceModeImpl.getChoiceMode() == Mode.None) {
            return;
        } else {
            mChoiceModeImpl.setItemChecked(position, null, value);
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
        return mChoiceModeImpl.getSelectedPositions();
    }

    /**
     * choice setChecked 时机
     */
    public enum CheckTime {
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
        SingleChoice,
        MultiChoice,
        None;
    }

    private static class ModeItem {

        int position;
        boolean isChecked;

    }

    private static abstract class ModeImpl {

        static ModeImpl createModeImpl(Mode mode, TagsAdapter adapter) {
            if (mode == null) {

                return createNoneChoice(adapter);
            }
            switch (mode) {
                case None:

                    return createNoneChoice(adapter);
                case MultiChoice:

                    return createModeMultiChoice(adapter);
                case SingleChoice:

                    return createModeSingleChoice(adapter);
            }

            return createNoneChoice(adapter);
        }

        static ModeImpl createModeSingleChoice(TagsAdapter adapter) {
            ModeImpl SingleChoice = new ModeImpl() {
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
                protected Mode getChoiceMode() {
                    return Mode.SingleChoice;
                }

                @Override
                public LinkedList<Integer> getSelectedPositions() {
                    LinkedList<Integer> is = new LinkedList<>();
                    if (modeItem.position != -1)
                        is.add(modeItem.position);
                    return is;
                }

                @Override
                public void clearChoiceCache() {
                }
            };

            SingleChoice.mAdapter = adapter;

            return SingleChoice;
        }

        static ModeImpl createModeMultiChoice(TagsAdapter adapter) {
            ModeImpl MultiChoice = new ModeImpl() {
                @Override
                protected void onTagItemClicked(View itemView, int position) {
                    if (isItemChecked(position)) {
                        setItemChecked(position, itemView, false);
                    } else {
                        setItemChecked(position, itemView, true);
                    }
                }

                @Override
                protected void setItemChecked(int position, View itemView, boolean value) {
                    ModeItem modeItem = mSelectedItemsDict.get(position);
                    if (modeItem != null) {

                        modeItem.isChecked = value;

                        if (!modeItem.isChecked) {
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

                @Override
                protected Mode getChoiceMode() {
                    return Mode.MultiChoice;
                }
            };

            MultiChoice.mAdapter = adapter;

            return MultiChoice;
        }

        static ModeImpl createNoneChoice(TagsAdapter adapter) {
            ModeImpl NoneChoice = new ModeImpl() {
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
                protected Mode getChoiceMode() {
                    return Mode.None;
                }

                @Override
                public void setItemChecked(int position, View itemView, boolean value) {

                }
            };

            NoneChoice.mAdapter = adapter;

            return NoneChoice;
        }
        /**
         * 没有选中模式
         */

        /**
         * 选中的Positions
         */
        protected HashMap<Integer, ModeItem> mSelectedItemsDict = new HashMap<>();

        protected TagsAdapter mAdapter;

        /**
         * 点击Item
         *
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
         *
         * @param position
         * @param itemView
         * @param value
         */
        protected abstract void setItemChecked(int position, View itemView, boolean value);

        /**
         * Item是否被选中
         *
         * @param position
         * @return
         */
        protected abstract boolean isItemChecked(int position);

        /**
         * 获取选中的位置
         *
         * @return
         */
        protected abstract LinkedList<Integer> getSelectedPositions();

        /**
         * 获取选中节点具体数据
         *
         * @param position
         * @return
         */
        protected abstract ModeItem getModeItem(int position);

        CheckTime mCheckTime = CheckTime.BeforeBind;

        protected final ModeImpl setCheckTime(CheckTime time) {
            time = time == null ? mCheckTime : time;
            mCheckTime = time;
            return this;
        }

        protected final CheckTime getCheckTime() {
            return mCheckTime;
        }

        protected abstract Mode getChoiceMode();
    }

}
