package com.srtianxia.separateseekbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by srtianxia on 2016/8/1.
 */
public class SeparateSeekBar extends View {
    private List<String> mTags = new ArrayList<>();
    private int mTextSize;
    private int mTextCheckedColor;
    private int mTextUnCheckedColor;
    private int mSeekBarColor;
    private int mSeekBarHeight;
    private int mSeekBarCircleRadius;
    private int mSeekBarBigCircleRadius;

    private Paint mTextPaint;
    private Paint mSeekBarPaint;
    private Paint mCirclePaint;

    private float mCurrentX;

    private float mTagWidth;

    private int mClickPosition = 0;

    private int mLastPosition = 0;

    private float mLineStartX;
    private boolean isFirst = true;

    private float mCurrentCircleRadius;
    private boolean isDragRunning = false;

    private float offsetValue = 10;

    private OnItemClickListener mOnItemClickListener;


    public SeparateSeekBar(Context context) {
        this(context, null);
    }


    public SeparateSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public SeparateSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SeparateSeekBar);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.SeparateSeekBar_textSize,
            (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 14, getResources().getDisplayMetrics()));
        mSeekBarHeight = typedArray.getDimensionPixelSize(R.styleable.SeparateSeekBar_seekBarHeight,
            (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        mTextCheckedColor = typedArray.getColor(R.styleable.SeparateSeekBar_textCheckedColor,
            Color.parseColor("#000000"));
        mTextUnCheckedColor = typedArray.getColor(R.styleable.SeparateSeekBar_textUncheckedColor,
            Color.parseColor("#D1D1D1"));
        mSeekBarColor = typedArray.getColor(R.styleable.SeparateSeekBar_seekBarColor,
            Color.parseColor("#1482F0"));
        mSeekBarCircleRadius = typedArray.getDimensionPixelSize(
            R.styleable.SeparateSeekBar_seekBarCircleRadius,
            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics()));
        typedArray.recycle();
        initPaint();

        this.mSeekBarBigCircleRadius = mSeekBarCircleRadius + 5;
    }


    public void setTags(List<String> mTags) {
        this.mTags = mTags;
        requestLayout();
        invalidate();
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }


    private void initPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(mTextSize);

        mSeekBarPaint = new Paint();
        mSeekBarPaint.setAntiAlias(true);
        mSeekBarPaint.setStyle(Paint.Style.STROKE);
        mSeekBarPaint.setStrokeWidth(mSeekBarHeight);
        mSeekBarPaint.setColor(mSeekBarColor);

        mCirclePaint = new Paint();
        mCirclePaint.setColor(mSeekBarColor);
        mSeekBarPaint.setAntiAlias(true);

        this.mCurrentCircleRadius = mSeekBarCircleRadius;
    }


    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        if (widthSpecMode == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getAllTagsLength(), (int) (getTextDatumLine() * 5));
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getAllTagsLength(), heightSpecSize);
        } else if (heightSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSpecSize, (int) (getTextDatumLine() * 5));
        }
    }


    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTagWidth = getWidth() / mTags.size();
        mLineStartX = mTagWidth / 2f;
        if (isFirst) {
            mCurrentX = mLineStartX;
            isFirst = false;
        }
        float offset = 0;
        for (int i = 0; i < mTags.size(); i++) {
            if (mClickPosition == i) {
                mTextPaint.setColor(mTextCheckedColor);
                canvas.drawText(mTags.get(i),
                    mTagWidth / 2f - mTextPaint.measureText(mTags.get(i)) / 2f + offset,
                    getHeight() / 2f - getTextDatumLine(), mTextPaint);
                canvas.drawLine(mLineStartX, mSeekBarCircleRadius + offsetValue, mCurrentX,
                    mSeekBarCircleRadius + offsetValue,
                    mSeekBarPaint);
                canvas.drawCircle(mCurrentX, mSeekBarCircleRadius + offsetValue,
                    mCurrentCircleRadius,
                    mCirclePaint);
            } else {
                mTextPaint.setColor(mTextUnCheckedColor);
                canvas.drawText(mTags.get(i),
                    mTagWidth / 2f - mTextPaint.measureText(mTags.get(i)) / 2f + offset,
                    getHeight() / 2f - getTextDatumLine(), mTextPaint);
            }
            offset += mTagWidth;
        }
    }


    @Override public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDragAnimation();
                break;
            case MotionEvent.ACTION_UP:
                setPosition(event.getX());
                isDragRunning = false;
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(mClickPosition);
                }
                stopDragAnimation();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurrentX = event.getX();
                invalidate();
                break;
        }
        return true;
    }


    private float getTextDatumLine() {
        return (mTextPaint.descent() + mTextPaint.ascent()) / 2f;
    }


    private float getCurrentProgress(float x) {
        return (x / (getRight() - getLeft())) * 100;
    }


    private void setPosition(float x) {
        mLastPosition = mClickPosition;
        mClickPosition = (int) Math.abs(x / mTagWidth);
        startMoveAnimation(x);
    }


    private int getAllTagsLength() {
        int length = 0;
        for (String tag : mTags) {
            length += mTextPaint.measureText(tag);
        }
        return length;
    }


    private void startDragAnimation() {
        if (!isDragRunning) {
            mCurrentCircleRadius = mSeekBarCircleRadius;
            isDragRunning = true;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurrentCircleRadius, mSeekBarBigCircleRadius);
            valueAnimator.setDuration(80);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float progress = (float) valueAnimator.getAnimatedValue();
                    mCurrentCircleRadius = progress;
                    invalidate();
                }
            });
            valueAnimator.start();
        }
    }

    private void stopDragAnimation() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mCurrentCircleRadius, mSeekBarCircleRadius);
        valueAnimator.setDuration(80);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float progress = (float) valueAnimator.getAnimatedValue();
                mCurrentCircleRadius = progress;
                invalidate();
            }
        });
        valueAnimator.start();
    }


    private void startMoveAnimation(float x) {
        if (Math.abs(mClickPosition - mLastPosition) == 1) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(x ,mClickPosition * mTagWidth + mLineStartX);
            valueAnimator.setDuration(100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float progress = (float) valueAnimator.getAnimatedValue();
                    mCurrentX = progress;
                    invalidate();
                }
            });
            valueAnimator.start();
        } else {
            if (mClickPosition == 0) {
                mCurrentX = mLineStartX;
            } else {
                mCurrentX = mClickPosition * mTagWidth + mLineStartX;
            }
            invalidate();
        }
    }

    public interface OnItemClickListener {
        void onClick(int position);
    }
}
