package sunrise.hugh.com.sunrise;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Hugh on 2017/9/18.
 *
 */
public class SunRiseView extends View {
    private final int SIZE_PADDING = 20;
    private String mStartTime;
    private String mEndTime;
    private String mCurrentTime;
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private Paint mPaint;
    private int mSemicircleColor;
    private int mTextColor;
    private Context mContext;
    private float mPercentage;
    private Bitmap mSun;
    private float positionX;
    private float positionY;
    private float mCurrentAngle;

    public SunRiseView(Context context) {
        super(context);
    }

    public SunRiseView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SunRiseView);
        mSemicircleColor = typedArray.getColor(R.styleable.SunRiseView_sun_circle_color, Color.BLACK);
        mTextColor = typedArray.getColor(R.styleable.SunRiseView_sun_text_color, Color.GRAY);
        typedArray.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSun = BitmapFactory.decodeResource(getResources(), R.drawable.sun);
    }


    public void setTimes(String startTime,String endTime,String currentTime){
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mCurrentTime = currentTime;

        float mTotalMinutes = calculateTotalTime(startTime, endTime);
        float mCurrentMinutes = calculateTotalTime(startTime, currentTime);
        mPercentage = mCurrentMinutes/mTotalMinutes;
        mCurrentAngle = mPercentage*180;

        setAnimation(0,mCurrentAngle,5000);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureHeight(heightMeasureSpec);
        measureWidth(widthMeasureSpec);
        mRadius = mWidth/2 - 20;
        setMeasuredDimension(mWidth,mHeight);
        positionX = mWidth / 2 - mRadius - 20; // 太阳图片的初始x坐标
        positionY = mRadius; // 太阳图片的初始y坐标
    }

    private void measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY){
            mWidth = size;
        }else {
            mWidth = 2*mHeight + 40;
            if (mode == MeasureSpec.AT_MOST){
                mWidth = Math.min(mWidth,size);
            }
        }
    }

    private void measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY){
            mHeight = size;
        }else {
            mHeight = 900;
            if (mode == MeasureSpec.AT_MOST){
                mWidth = Math.min(mHeight,size);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, 0, SIZE_PADDING, mWidth, (int) (mRadius*1.5));
    }

    private void drawSemiCircle(Canvas canvas){
        RectF rectF = new RectF(mWidth/2 - mRadius, SIZE_PADDING, mWidth/2 + mRadius, mRadius*2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setDither(true);
        mPaint.setColor(mSemicircleColor);
        canvas.drawArc(rectF,180,180,true,mPaint);
    }

    private void drawSunPosition(Canvas canvas)
    {
        canvas.drawBitmap(mSun, positionX, positionY, mPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawSemiCircle(canvas);
        canvas.save();

        drawSunPosition(canvas);
    }

    public float calculateTotalTime(String startTime,String endTime){
        String[] splitStart = startTime.split(":");
        String[] splitEnd = endTime.split(":");
        Float endHour = Float.valueOf(splitEnd[0]);
        Float startHour = Float.valueOf(splitStart[0]);
        Float endMinute = Float.valueOf(splitEnd[1]);
        Float startMinute = Float.valueOf(splitStart[1]);
        return (endHour - startHour)*60 + (endMinute - startMinute + 60);
    }

    private void setAnimation(float startAngle, float currentAngle, int duration)
    {
        ValueAnimator sunAnimator = ValueAnimator.ofFloat(startAngle, currentAngle);
        sunAnimator.setDuration(duration);
        sunAnimator.setTarget(currentAngle);
        sunAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                //每次要绘制的圆弧角度
                mCurrentAngle = (float) animation.getAnimatedValue();
                invalidateView();
            }

        });
        sunAnimator.start();
    }

    private void invalidateView()
    {
        //绘制太阳的x坐标和y坐标
        positionX = mWidth / 2 - (float) (mRadius * Math.cos((mCurrentAngle) * Math.PI / 180)) - 20;
        positionY = mRadius - (float) (mRadius * Math.sin((mCurrentAngle) * Math.PI / 180)) - 10;

        invalidate();
    }

    public int dp2px(Context context, float dpValue)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
