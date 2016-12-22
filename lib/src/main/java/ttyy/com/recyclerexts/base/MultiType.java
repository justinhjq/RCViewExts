package ttyy.com.recyclerexts.base;

import android.util.SparseArray;

/**
 * Author: Administrator
 * Date  : 2016/12/22 17:52
 * Name  : MultiType
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/22    Administrator   1.0              1.0
 */
public abstract class MultiType<T> {

    SparseArray<Integer> typeLayouts;

    public MultiType() {
        typeLayouts = new SparseArray<>();
    }

    public int getViewTypes() {

        return 0;
    }

    /**
     * 根据position data确定item的type
     *
     * @param position
     * @param t
     * @return
     */
    public abstract int getItemType(int position, T t);

    /**
     * 根据type获得相应的layoutId
     *
     * @param type
     * @return
     */
    public int getLayoutIdForType(int type){
        return typeLayouts.get(type);
    }

    public MultiType add(int type, int layoutId) {
        typeLayouts.put(type, layoutId);
        return this;
    }


}
