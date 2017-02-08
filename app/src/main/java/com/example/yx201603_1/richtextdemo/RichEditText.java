package com.example.yx201603_1.richtextdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.widget.EditText;
import android.widget.FrameLayout;

/**
 * 富文本组件
 */
public class RichEditText extends FrameLayout implements IEditView {



	private LayoutInflater mInflater;
	private Context mContext;


	private EditText mEditText;

	private IClickCallBack clickCallBack;

	public Holder holder;

	@Override
	public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
		return new DeleteInputConnection(super.onCreateInputConnection(outAttrs),
				true);
	}
	//处理软键盘回删按钮backSpace时回调OnKeyListener
	private class DeleteInputConnection extends InputConnectionWrapper {

		public DeleteInputConnection(InputConnection target, boolean mutable) {
			super(target, mutable);
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) {
			return super.sendKeyEvent(event);
		}

		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) {
			if (beforeLength == 1 && afterLength == 0) {
				return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_DEL))
						&& sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_DEL));
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}

	}

	public RichEditText(Context context) {
		this(context, null);
	}

	public RichEditText(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RichEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;
		mInflater = LayoutInflater.from(context);
		mInflater.inflate(R.layout.item_rich_edit,this);
		holder = new Holder();
		holder.viewType = Type.CONTENT;
		init();
	}

	private void init() {
		mEditText = (EditText) findViewById(R.id.et_rich);
		findViewById(R.id.blank_view).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(clickCallBack != null)
					clickCallBack.onBlankViewClick(v, RichEditText.this);
			}
		});
		mEditText.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(clickCallBack != null)
					clickCallBack.onContentClick(v, RichEditText.this);
				return false;
			}
		});

	}

	 public void setContent(String content){
		 mEditText.setText(content);
	 }

	public EditText getEditText(){
		return mEditText;
	}

	public int getSelectionStart(){
		return mEditText.getSelectionStart();
	}

	public void setText(String text){
		mEditText.setText(text);
	}

	public void setSelection(int start,int stop){
		mEditText.setSelection(start,stop);
	}

	public void reqFocus(){
		mEditText.requestFocus();
	}

	@Override
	public String getUploadId() {
		return null;
	}

	@Override
	public Enum getViewType() {
		return Type.CONTENT;
	}

	@Override
	public String getFilePath() {
		return null;
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
		String s = mEditText.getText().toString();
		holder.content = s;
		return s;
	}

	@Override
	public Holder getHolder() {
		return holder;
	}


}