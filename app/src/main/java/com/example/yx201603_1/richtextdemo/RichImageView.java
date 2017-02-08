package com.example.yx201603_1.richtextdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 图片组件
 *
 * @author jadyn
 */
public class RichImageView extends FrameLayout implements IEditView {

    private LayoutInflater mInflater;
    private Context mContext;

    private ImageView mEditImageView;
    private ImageView mImageClose;
    private View mBlankView;

    private IClickCallBack clickCallBack;

    private Holder holder;
    private int SCREEN_WIDTH;

    public RichImageView(Context context) {
        this(context, null);
    }

    public RichImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mInflater.inflate(R.layout.item_edit_imageview, this);
        holder = new Holder();
        holder.viewType = Type.IMAGE;
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        SCREEN_WIDTH = dm.widthPixels;
        init();
    }


    private void init() {
        mEditImageView = (ImageView) findViewById(R.id.edit_imageView);
        mImageClose = (ImageView) findViewById(R.id.image_close);
        mBlankView = findViewById(R.id.blank_view);
        mBlankView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCallBack != null) {
                    clickCallBack.onBlankViewClick(v, RichImageView.this);
                }
            }
        });
        mImageClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCallBack != null) {
                    clickCallBack.onDeleteIconClick(v, RichImageView.this);
                }
            }
        });
        mEditImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCallBack != null) {
                    clickCallBack.onContentClick(v, RichImageView.this);
                }
            }
        });

    }

    public void setEditImageView(final String imagePath) {
//        if (TextUtils.isEmpty(imagePath))
//            return;
        holder.filePath = imagePath;
        mEditImageView.getLayoutParams().width= SCREEN_WIDTH;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, opts);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(SCREEN_WIDTH, SCREEN_WIDTH);
        mEditImageView.setLayoutParams(layoutParams);

        mEditImageView.setBackgroundResource(R.drawable.ceshi);

    }


    @Override
    public String getUploadId() {
        return holder.uploadId;
    }

    @Override
    public Enum getViewType() {
        return Type.IMAGE;
    }

    @Override
    public String getFilePath() {
        return holder.filePath;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setOnClickViewListener(IClickCallBack listener) {
        this.clickCallBack = listener;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public Holder getHolder() {
        return holder;
    }


}
