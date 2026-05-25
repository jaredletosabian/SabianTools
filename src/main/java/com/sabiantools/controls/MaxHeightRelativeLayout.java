package com.sabiantools.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.sabiantools.R;


public class MaxHeightRelativeLayout extends RelativeLayout {

    private int maxHeight = -1;

    public MaxHeightRelativeLayout(Context context) {
        super(context);
    }

    public MaxHeightRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaxHeightRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(
                attrs,
                R.styleable.MaxHeightRelativeLayout
        );

        maxHeight = a.getDimensionPixelSize(
                R.styleable.MaxHeightRelativeLayout_maxHeight,
                -1
        );

        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (maxHeight >= 0) {

            int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);

            if (measuredHeight > maxHeight) {

                heightMeasureSpec = MeasureSpec.makeMeasureSpec(
                        maxHeight,
                        MeasureSpec.AT_MOST
                );
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}