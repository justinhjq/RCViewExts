package ttyy.com.recyclerexts.cycle_album;

/**
 * Author: Administrator
 * Date  : 2016/12/28 14:43
 * Name  : CycleAlbumConfig
 * Intro : Edit By Administrator
 * Modification  History:
 * Date          Author        	 Version          Description
 * ----------------------------------------------------------
 * 2016/12/28    Administrator   1.0              1.0
 */
final class CycleAlbumConfig {

    /**
     * 循环展示数量
     */
    private int mCycleCount = 4;
    /**
     * 显示图片叠加效果
     * 每层往下一个单位
     */
    private int Y_GAP_UNIT = 30;
    /**
     * 显示图片叠加效果
     * 每层缩放一个单位
     */
    private float SCALE_UNIT = 0.05f;

    /**
     * 最大旋转角度
     */
    private int MAX_ROTATION = 15;

    static class Holder{
        static CycleAlbumConfig instance = new CycleAlbumConfig();
    }

    private CycleAlbumConfig(){

    }

    static CycleAlbumConfig getInstance(){
        return Holder.instance;
    }

    protected int getCycleCount(){
        return mCycleCount;
    }

    protected int getYGapUnit(){
        return Y_GAP_UNIT;
    }

    protected float getScaleUnit(){
        return SCALE_UNIT;
    }

    protected int getMaxRotation(){
        return MAX_ROTATION;
    }

}
