package com.zhuo.tong.view.pager_indicator;

/**
 * 图标接口，非必须的，一般tag不用设置图标
 * @author dong
 *
 */
public interface IIconPagerAdapter {
    /**
     * Get icon representing the page at {@code index} in the adapter.
     */
    int getIconResId(int index);

    // From PagerAdapter
    int getCount();
}
