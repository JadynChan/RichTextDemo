package com.example.yx201603_1.richtextdemo;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;


/**
 * 富文本内容编辑组件
 * 文本编辑内容组件每次都会自动添加，你只需要添加各种其他组件就行了
 */
public class RichSrcollView extends ScrollView {

    public static final String KEY_TITLE = "title";
    public static final String KEY_CONTENT = "content";

    private LinearLayout allLayout; // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    private OnKeyListener keyListener; // 所有EditText的软键盘监听器
    private OnFocusChangeListener focusListener; // 所有EditText的焦点监听listener
    public RichEditText lastFocusView; // 最近被聚焦的view
    private LayoutTransition mTransitioner; // 只在图片View添加或remove时，触发transition动画
    private Context mContext;

    private boolean hasTitle = false;

    public RichSrcollView(Context context) {
        this(context, null);
    }

    public RichSrcollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RichSrcollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;

        // 初始化allLayout，用来存放所有富文本组件
        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        allLayout.setBackgroundColor(Color.WHITE);
        setupLayoutTransitions();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        addView(allLayout, layoutParams);

        // 键盘退格监听
        // 主要用来处理点击回删按钮时，view的一些列合并操作
        keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    RichEditText richEditText = (RichEditText) v.getParent().getParent();
                    onBackspacePress(richEditText);
                }
                return false;
            }
        };

        //定一个焦点改变监听器，用来知道最后的焦点在哪个组件，这样插入新组件的话就会插入到那个组件的后面
        focusListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lastFocusView = (RichEditText) v.getParent().getParent();
                }
            }
        };

        //初始化生成一个编辑文本框
        LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        RichEditText view = createEditText();
        allLayout.addView(view, firstEditParam);
        lastFocusView = view;
    }

    public void removeAllIEditView() {
        if (allLayout != null) {
            allLayout.removeAllViews();
        }
    }

    /**
     * 处理软键盘backSpace回退事件
     * 回退时是否在文本上回退，在文本上时是否还有数据，有就删除数据，没有就上次上一个组件，当前焦点还是在这个文本框，这样才有一种富文本编辑器的感觉
     *
     * @param
     */
    private void onBackspacePress(RichEditText curView) {
        int startSelection = curView.getEditText().getSelectionStart();
        // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的组件，或两个View合并
        if (startSelection == 0) {
            //表示一个文本框，这种情况回退不能删除组件
            if (allLayout.getChildCount() <= 1) {
                return;
            }
            int editIndex = allLayout.indexOfChild(curView);
            View preView = allLayout.getChildAt(editIndex - 1);
            // 则返回的是null
            if (null != preView) {
                if (preView instanceof RichEditText) {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    String str1 = curView.getEditText().getText().toString();
                    EditText preEdit = ((RichEditText) preView).getEditText();
                    String str2 = preEdit.getText().toString();

                    // 合并文本view时，不需要transition动画
                    allLayout.setLayoutTransition(null);
                    allLayout.removeView(curView);
                    allLayout.setLayoutTransition(mTransitioner); // 恢复transition动画

                    // 文本合并
                    preEdit.setText(str2 + str1);
                    preEdit.requestFocus();
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusView = (RichEditText) preView;
                } else if (preView instanceof IEditView) {
                    // 光标EditText的上一个view对应的是组件
                    onEditViewCloseClick(preView);
                }

            }
        }
    }

    /**
     * 处理组件关闭图标的点击事件
     *
     * @param view 整个image对应的relativeLayout view
     */
    private void onEditViewCloseClick(View view) {
        if (!mTransitioner.isRunning()) {
            allLayout.removeView(view);
        }
    }

    /**
     * 生成文本输入框
     */
    private RichEditText createEditText() {
        RichEditText richEditText = new RichEditText(mContext);
        richEditText.getEditText().setOnKeyListener(keyListener);
        if (haveEditText())
            richEditText.getEditText().setHint("");
        richEditText.getEditText().setOnFocusChangeListener(focusListener);
        return richEditText;
    }


    private boolean haveEditText() {
        int childCount = allLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            IEditView iEditView = (IEditView) allLayout.getChildAt(i);
            if (iEditView.getViewType().ordinal() == IEditView.Type.CONTENT.ordinal()) {
                return true;
            }
        }
        return false;
    }

    private void setEditViewListener(IEditView editView) {
        //删除按钮设置监听器
        editView.setOnClickViewListener(new IClickCallBack() {
            @Override
            public void onBlankViewClick(View v, View widget) {
                //点击组件下面的空白，如果当前组件和上下组件都不是文本框，则创建一个文本框
                int childCount = allLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (allLayout.getChildAt(i) == widget) {
                        View curView = allLayout.getChildAt(i);
                        View nextView = allLayout.getChildAt(i + 1);
                        if (!(curView instanceof RichEditText) && (nextView == null || !(nextView instanceof RichEditText))) {
                            addEditTextAtIndex(i + 1, "");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onDeleteIconClick(View v, View widget) {
//				Toast.makeText(mContext,"点击删除",Toast.LENGTH_SHORT).show();
                onEditViewCloseClick(widget);
                if (lastFocusView != null)
                    lastFocusView.reqFocus();
            }

            @Override
            public void onContentClick(View v, View widget) {

            }
        });
    }


    /**
     * 在特定位置插入EditText
     *
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    private void addEditTextAtIndex(final int index, String editStr) {
        RichEditText view = createEditText();
        EditText editText2 = (EditText) view.findViewById(R.id.et_rich);
        editText2.setText(editStr);
        lastFocusView = view;
        view.reqFocus();
        // 请注意此处，EditText添加、或删除不触动Transition动画
        allLayout.setLayoutTransition(null);
        allLayout.addView(view, index);
        allLayout.setLayoutTransition(mTransitioner); // remove之后恢复transition动画
    }

    /**
     * 在特定位置添加一个编辑组件
     */
    private void addEditViewAtIndexAnimation(final int index, final IEditView editView) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                allLayout.addView(editView.getView(), index);

            }
        }, 200);


    }

    private void srollToBottom() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lastFocusView != null)
                    lastFocusView.reqFocus();
                fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 1000);
    }

    /**
     * 立即插入一个编辑组件，适用于编辑话题，有延时会导致顺序错乱
     * 代价是没有动画
     *
     * @param index    显示位置
     * @param editView 组件
     */
    private void addEditViewAtIndexImmediate(final int index, final IEditView editView) {

        allLayout.addView(editView.getView(), index);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (lastFocusView != null)
                    lastFocusView.reqFocus();
                fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 1000);

    }

    /**
     * 初始化transition动画
     */
    private void setupLayoutTransitions() {
        mTransitioner = new LayoutTransition();
        allLayout.setLayoutTransition(mTransitioner);
        mTransitioner.setDuration(300);
    }


    /**
     * 获取当前焦点的Edittext
     *
     * @return
     */
    public EditText getCurFousEditText() {
        if (lastFocusView != null)
            return lastFocusView.getEditText();
        return null;
    }

    public void setLastEditTextFocus() {
        int childCount = allLayout.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childAt = allLayout.getChildAt(i);
            if (childAt instanceof RichEditText) {
                ((RichEditText) childAt).reqFocus();
                showKeyBoard(((RichEditText) childAt).getEditText());
                return;
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getY() > allLayout.getBottom()) {
            setLastEditTextFocus();
            return true;
        }


        return super.dispatchTouchEvent(ev);
    }

    /**
     * 隐藏小键盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(lastFocusView.getWindowToken(), 0);
    }

    public void showKeyBoard(EditText view) {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        view.setSelection(0);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        imm.showSoftInput(view, 0);
    }

    /**
     * 插入一个编辑组件,根据焦点的不同而位置不同
     */
    public void insertEditView(IEditView editView) {
        setEditViewListener(editView);

        String lastEditStr = lastFocusView.getContent();
        lastFocusView.reqFocus();
        int cursorIndex = lastFocusView.getSelectionStart();
        int lastEditIndex = allLayout.indexOfChild(lastFocusView);
        if (cursorIndex >= 0) {
            String editStr1 = lastEditStr.substring(0, cursorIndex).trim();

            if (lastEditStr.length() == 0 || editStr1.length() == 0) {
                // 如果EditText为空，或者光标已经顶在了editText的最前面，则直接插入组件，并且EditText下移即可
                addEditViewAtIndexAnimation(lastEditIndex, editView);
            } else {
                // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
                lastFocusView.setText(editStr1);
                String editStr2 = lastEditStr.substring(cursorIndex).trim();
                if (allLayout.getChildCount() - 1 == lastEditIndex
                        || editStr2.length() > 0) {
                    addEditTextAtIndex(lastEditIndex + 1, editStr2);
                }

                addEditViewAtIndexAnimation(lastEditIndex + 1, editView);
                lastFocusView.reqFocus();
                lastFocusView.setSelection(lastFocusView.getContent().length(), lastFocusView.getContent().length());
            }
            if (allLayout.indexOfChild(lastFocusView) >= allLayout.getChildCount() - 1) {
                srollToBottom();
            }
        } else {
            //出现失去焦点的情况，默认添加到最后面
            addEditViewAtIndexAnimation(allLayout.getChildCount() - 1, editView);
            srollToBottom();
        }

        hideKeyBoard();
    }

    /**
     * 获取全部数据集合
     */
    public List<IEditView> buildData() {
        List<IEditView> dataList = new ArrayList<IEditView>();
        int num = allLayout.getChildCount();
        for (int index = 0; index < num; index++) {
            IEditView itemView = (IEditView) allLayout.getChildAt(index);
            dataList.add(itemView);
        }
        return dataList;
    }




}
