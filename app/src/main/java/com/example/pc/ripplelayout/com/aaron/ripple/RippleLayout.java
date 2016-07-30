package com.example.pc.ripplelayout.com.aaron.ripple;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.RelativeLayout;

import com.example.pc.ripplelayout.R;

import java.util.ArrayList;

/**
 * Created by pc on 2016/7/30.
 * 波纹控件
 * 1.实现思路
 * <p/>
 * 使用属性动画 将 view的属性 scaleX,scaleY,Alpha进行改变，完成 从小到大 放大消失的效果。
 * <p/>
 * 使用一个布局，实现多个view的效果叠加，完成波纹的效果。
 */
public class RippleLayout extends RelativeLayout {


    private static final int DEFAULT_RIPPLE_COUNT = 1;
    private static final int DEFAULT_DURATION_TIME = 6000;
    private static final float DEFAULT_RIPPLE_SCALE = 6.0f;
    private static final int DEFAULT_FILL_STYLE = 0;


    private int rippleCount;//波纹个数
    private int rippleDelayTime;//波纹延时播放时间
    private float rippleScale;//波纹放大倍数
    private int rippleColor;//波纹的颜色
    private int rippleDurationTime;//波纹的周期时长
    private float rippleStrokeWidth;//波纹的宽度
    private int rippletype;//波纹的类型   环形，实心型
    private float rippleRaduis;//波纹的半径

    private boolean rippleRunning;
    private LayoutParams rippleLayoutParams;
    private AnimatorSet animationSet;
    private ArrayList<Animator> animations;
    private ArrayList<RippleView> rippleViews = new ArrayList<>();

    private Paint paint;


    public RippleLayout(Context context) {
        super(context);

    }

    public RippleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, context);

    }

    private void init(AttributeSet attrs, Context context) {
        if (isInEditMode()) {
            return;
        }

        if (null == attrs) {
            throw new IllegalArgumentException("Attributes should be provided to this view,");
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RippleLayout);


        rippleColor = ta.getColor(R.styleable.RippleLayout_rippleColor, getResources().getColor(R.color.light_perpure));
        rippleCount = ta.getInt(R.styleable.RippleLayout_rippleCount, DEFAULT_RIPPLE_COUNT);
        rippleDurationTime = ta.getInt(R.styleable.RippleLayout_rippleDuration, DEFAULT_DURATION_TIME);
        rippleRaduis = ta.getDimension(R.styleable.RippleLayout_rippleRadius, getResources().getDimension(R.dimen.rippleRadius_default));
        rippletype = ta.getInt(R.styleable.RippleLayout_rippleType, DEFAULT_FILL_STYLE);
        if (rippletype != DEFAULT_FILL_STYLE) {
            rippleStrokeWidth = ta.getDimension(R.styleable.RippleLayout_rippleStrokeWidth, getResources().getDimension(R.dimen.rippleStrokeWidth_default));
        }
        rippleScale = ta.getFloat(R.styleable.RippleLayout_rippleScale, DEFAULT_RIPPLE_SCALE);
        ta.recycle();
        rippleDelayTime = rippleDurationTime / rippleCount;

        paint = new Paint();
        paint.setColor(rippleColor);
        paint.setAntiAlias(true);
        if (rippletype == DEFAULT_FILL_STYLE) {
            rippleStrokeWidth = 0;
            paint.setStyle(Paint.Style.FILL);

        } else {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(rippleStrokeWidth);
            // rippleRaduis = rippleRaduis - rippleStrokeWidth;
        }

        rippleLayoutParams = new LayoutParams((int) ((rippleRaduis + rippleStrokeWidth) * 2), (int) ((rippleRaduis + rippleStrokeWidth) * 2));
        rippleLayoutParams.addRule(CENTER_IN_PARENT, TRUE);

        //设置动画集合
        animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animations = new ArrayList<>();

        for (int i = 0; i < rippleCount; i++) {
            RippleView rippleView = new RippleView(context);
            addView(rippleView, rippleLayoutParams);
            rippleViews.add(rippleView);
            final ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(rippleView, "ScaleX", 1, rippleScale);
            scaleXAnim.setDuration(rippleDurationTime);
            scaleXAnim.setStartDelay(rippleDelayTime * i);
            scaleXAnim.setRepeatCount(ValueAnimator.INFINITE);
            scaleXAnim.setRepeatMode(ValueAnimator.RESTART);
            animations.add(scaleXAnim);
            final ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(rippleView, "ScaleY", 1, rippleScale);
            scaleYAnim.setDuration(rippleDurationTime);
            scaleYAnim.setStartDelay(rippleDelayTime * i);
            scaleYAnim.setRepeatCount(ValueAnimator.INFINITE);
            scaleYAnim.setRepeatMode(ValueAnimator.RESTART);
            animations.add(scaleYAnim);
            final ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(rippleView, "Alpha", 0.5f, 0f);
            alphaAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ObjectAnimator.RESTART);
            alphaAnimator.setStartDelay(i * rippleDelayTime);
            alphaAnimator.setDuration(rippleDurationTime);
            animations.add(alphaAnimator);


        }

        animationSet.playTogether(animations);


    }

    public RippleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, context);
    }


    class RippleView extends View {


        public RippleView(Context context) {
            super(context);
            setVisibility(INVISIBLE);
        }

        @Override
        protected void onDraw(Canvas canvas) {

            int radius = Math.min(getWidth(), getHeight()) / 2;
            canvas.drawCircle(radius, radius, radius - rippleStrokeWidth, paint);
        }
    }


    public void startRippleAniamtion() {
        if (!isRippleRunning()) {
            for (RippleView rippleView : rippleViews) {
                rippleView.setVisibility(VISIBLE);
            }
            animationSet.start();
            rippleRunning = true;

        }
    }

    public void stopRippleAniamtion() {
        if (isRippleRunning()) {
            for (RippleView rippleView : rippleViews) {
                rippleView.setVisibility(INVISIBLE);
            }
            animationSet.end();
            rippleRunning = false;
        }
    }


    public boolean isRippleRunning() {
        return rippleRunning;
    }


    /**
     * 设置一个不变的中心元显示
     * @param visibility
     */
    public void setCenterVisibility(boolean visibility){
        
    }


}
