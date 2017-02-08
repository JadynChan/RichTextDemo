package com.example.yx201603_1.richtextdemo;

import android.view.View;

import java.io.Serializable;

/**
 * 富文本组件都要实现该类
 * Created by Jadyn on 2016/5/9.
 */
public interface IEditView {

    /**
     * 上传文件返回的id
     */
     String getUploadId();

    /**
     * 获取view类型
     */
    Enum getViewType();

    /**
     * 获取文件本地路径
     * @return
     */
    String getFilePath();

    /**
     * 获取具体实现的view
     * @return
     */
    View getView();

    /**
     * 设置点击组件下面的空白回调事件
     * @param listener
     */
    void setOnClickViewListener(IClickCallBack listener);

    /**
     * 获取显示的文本
     * @return
     */
    String getContent();

    Holder getHolder();

    enum Type{
        IMAGE,FILE,VOICE,LOCATION,CONTENT,TITLE,UNKOWN
    }

    class Holder implements Serializable {
        public String uploadId;
        public String filePath;
        public String fileName;
        public Enum viewType;
        public String content;

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "uploadId='" + uploadId + '\'' +
                    ", filePath='" + filePath + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", viewType=" + viewType +
                    ", content='" + content + '\'' +
                    '}';
        }
    }

}
