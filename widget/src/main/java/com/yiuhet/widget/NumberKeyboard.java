package com.yiuhet.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by yiuhet on 2019/7/1.
 * <p>
 * 九宫格数字输入键盘
 */
public class NumberKeyboard extends ViewGroup {
    private int mDefaultKeyPaddding = 20; //默认的按键间的距离 dp
    private int mDefaultKeyW = 20; //默认的按键宽度 dp
    private int mDefaultKeyH = 20; //默认的按键高度 dp

    private Context mContext;
    private float mDensity; //屏幕密度
    private float mKeyPaddding; //按键间的距离
    private float mKeyWidth; //按键的高度
    private float mKeyHeight;//按键的宽度


    public NumberKeyboard(Context context) {
        this(context, null);
    }

    public NumberKeyboard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDensity = mContext.getResources().getDisplayMetrics().density;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberKeyboard, defStyleAttr, 0);
        int indexCount = typedArray.getIndexCount();
        for (int i = 0; i < indexCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.NumberKeyboard_keyPadding:
                    mKeyPaddding = typedArray.getDimensionPixelSize(attr, dp2px(mDefaultKeyPaddding));
                    break;
                case R.styleable.NumberKeyboard_keyWidth:
                    mKeyWidth = typedArray.getDimensionPixelSize(attr, dp2px(mDefaultKeyW));
                    break;
                case R.styleable.NumberKeyboard_keyHeight:
                    mKeyHeight = typedArray.getDimensionPixelSize(attr, dp2px(mDefaultKeyH));
                    break;
                case R.styleable.NumberKeyboard_keyTextSize:
                    break;
            }
        }
        typedArray.recycle();
        initKey();
    }

    private void initKey() {
        // 生成键盘的每个key
        for (int i = 1; i <= 12; i++) {
            TextView key = new TextView(mContext);
            key.setGravity(Gravity.CENTER);
            key.setText(i % 11 + ""); //下标为11时为0 ， 10 12下面覆盖
            if (i == 10) {
//                key = inflate(getContext(), R.layout.num_key_clean, null);
                key.setText("");
            } else if (i == 12) {
//                key = inflate(getContext(), R.layout.num_key_del, null);
                key.setText("");
            }
            LayoutParams params = new LayoutParams((int) mKeyWidth, (int) mKeyHeight);
            key.setLayoutParams(params);
            addView(key);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float width = MeasureSpec.getSize(widthMeasureSpec)
                + getPaddingLeft() + getPaddingRight();
        float height = MeasureSpec.getSize(heightMeasureSpec)
                + getPaddingBottom() + getPaddingTop();
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED: //未指定
            case MeasureSpec.AT_MOST://wrap_content
                width = mKeyWidth * 3 + mKeyPaddding * 2 + getPaddingLeft() + getPaddingRight();
            case MeasureSpec.EXACTLY://match_parent or 具体数值
//                measureChild();
                break;
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private int dp2px(float dp) {
        return (int) (dp * mDensity + 0.5f);
    }
}
