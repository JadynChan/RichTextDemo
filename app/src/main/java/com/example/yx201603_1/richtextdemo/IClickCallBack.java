package com.example.yx201603_1.richtextdemo;

import android.view.View;

/**
 * Created by Jadyn on 2016/5/9.
 */
public interface IClickCallBack {

    /**
     * 点击view下面的空白处回调事件，可在此实现插入edittext
     * @param v 点击的view
     * @param widget 当前的组件
     */
    void onBlankViewClick(View v, View widget);
    /**
     * 点击view里面的删除图标回调事件，部分类型的view里面没有删除图标
     * @param v 点击的view
     * @param widget 当前的组件
     */
    void onDeleteIconClick(View v, View widget);

    /**
     *
     * @param v
     * @param widget
     */
    void onContentClick(View v, View widget);
}
