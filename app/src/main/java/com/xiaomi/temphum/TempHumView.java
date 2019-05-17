package com.xiaomi.temphum;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;


/**
 * @describe：<br>
 * @author：Xiaomi<br>
 * @createTime：2019/5/16<br>
 * @remarks：<br>
 * @changeTime:<br>
 */
public class TempHumView extends View {
    /**
     * 控件宽
     */
    private int mWidth;
    /**
     * 控件高
     */
    private int mHeight;
    private String mTempTitle = "温度(℃)";
    private String mHumTitle = "湿度(%)";
    /**
     * 刻度高 短针
     */
    private int mScaleHeight = dp2px(10);
    /**
     * 刻度高 长针
     */
    private int mScaleHeight1 = dp2px(15);
    /**
     * 刻度盘/
     */
    private Paint mDialPaint;
    /**
     * 文本画笔
     */
    private Paint mTitlePaint;
    /**
     * 画湿度圆环的画笔
     */
    private Paint mHumCirclePaint;
    /**
     * 当前湿度值的小圆点
     */
    private Paint mHumValueCirclePaint;
    /**
     * 湿度圆环左边和右边的小圆点画笔
     */
    private Paint mHumLeftCirclePaint;
    private Paint mHumRightCirclePaint;

    /**
     * 当前温度
     */
    private int mTemperature = 15;
    //
    /**
     * 最低温度
     */
    private int mMinTemp = -30;
    /**
     * 最高温度
     */
    private int mMaxTemp = 50;
    /**
     * 当前湿度
     */
    private int mHumidity = 100;
    /**
     * 最低湿度
     */
    private int mMinHum = 0;
    /**
     * 最高湿度
     */
    private int mMaxHum = 100;
    /**
     * 温度每份的角度
     */
    private float mAngleOneTem = (float) 270 / (mMaxTemp - mMinTemp);
    /**
     * 湿度每份的角度
     */
    private float mAngleOneHum = (float) 270 / (mMaxHum - mMinHum);

    /**
     * 温度，刻度的半径
     */
    private int mTemDialRadius;
    /**
     * 湿度，内圈半径
     */
    private int mHumInSideRadius;
    /**
     * 湿度，圆环宽度
     */
    private int mHumCriWidth = dp2px(15);

    /**
     * 刻度和文字颜色
     */
    private String mTextColor = "#666666";
    /**
     * 未达到的温度
     */
    private String mDialBackGroundColor = "#666666";
    /**
     * 已经达到的温度
     */
    private String mDialForegroundColor = "#1FC8A2";
    /**
     * 湿度圆环背景色
     */
    private String mHumCriBackground = "#666666";
    /**
     * 湿度圆环前景色
     */
    private String mHumCriForeground = "#1FC8A2";
    /**
     * 当前湿度的小圆点颜色
     */
    private String mHumCriValue = "#eeeeee";
    /**
     * 当前湿度的小圆点的背景色
     */
    private String mHumCriValueBackGround = "#30333333";

    public TempHumView(Context context) {
        super(context);
        init();
    }

    public TempHumView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TempHumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        //温度刻度盘
        mDialPaint = new Paint();
        mDialPaint.setAntiAlias(true);
        mDialPaint.setColor(Color.parseColor(mDialBackGroundColor));
        mDialPaint.setStrokeWidth(dp2px(2));
        mDialPaint.setStyle(Paint.Style.STROKE);

        //文字描述
        mTitlePaint = new Paint();
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTextSize(sp2px(15));
        mTitlePaint.setColor(Color.parseColor(mTextColor));
        mTitlePaint.setStyle(Paint.Style.STROKE);

        //湿度圆环
        mHumCirclePaint = new Paint();
        mHumCirclePaint.setAntiAlias(true);
        mHumCirclePaint.setColor(Color.parseColor(mHumCriBackground));
        mHumCirclePaint.setStyle(Paint.Style.STROKE);
        mHumCirclePaint.setStrokeWidth(mHumCriWidth);

        //当前湿度值
        mHumValueCirclePaint = new Paint();
        mHumValueCirclePaint.setAntiAlias(true);
        mHumValueCirclePaint.setColor(Color.parseColor(mHumCriValue));
        mHumValueCirclePaint.setStyle(Paint.Style.FILL);

        //湿度圆环左边的小圆点
        mHumLeftCirclePaint = new Paint();
        mHumLeftCirclePaint.setAntiAlias(true);
        mHumLeftCirclePaint.setColor(Color.parseColor(mHumCriForeground));
        mHumLeftCirclePaint.setStyle(Paint.Style.FILL);

        //湿度圆环右边的小圆点
        mHumRightCirclePaint = new Paint();
        mHumRightCirclePaint.setAntiAlias(true);
        mHumRightCirclePaint.setColor(Color.parseColor(mHumCriBackground));
        mHumRightCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        // 参考宽，处理成正方形
        setMeasuredDimension(specSize, specSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 控件宽、高
        mWidth = mHeight = Math.min(h, w);
        // 温度，刻度的半径
        mTemDialRadius = mWidth / 2 - dp2px(70);
        // 湿度，内圈半径=刻度的内圈+最长刻度+15像素
        mHumInSideRadius = mTemDialRadius + mScaleHeight1 + dp2px(15);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画温度刻度盘和内的刻度
        drawTempDial(canvas);
        //画湿度圆弧
        drawHumArc(canvas);
        //画左边和左边的小圆点
        drwaHumArcLeftAndRight(canvas);
        //当前湿度值
        drawHumValueArc(canvas);
        //湿度外边的刻度
        drawHumText(canvas);
        //画温湿度标题文字
        drawTemText(canvas);
    }

    /**
     * 画温度刻度 和温度内圈文字
     */
    private void drawTempDial(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        // 顺时针旋转135度
        canvas.rotate(-135);
        for (int i = mMinTemp; i <= mMaxTemp; i++) {
            if (i <= mTemperature) {
                mDialPaint.setColor(Color.parseColor(mDialForegroundColor));
            } else {
                mDialPaint.setColor(Color.parseColor(mDialBackGroundColor));
            }
            if (i % 5 == 0) {
                //从刻度的内圈开始，往外画
                canvas.drawLine(0, -mTemDialRadius, 0, -mTemDialRadius - mScaleHeight1, mDialPaint);
                if (i % 10 == 0) {
                    float tempWidth = mDialPaint.measureText(i + "");
                    canvas.drawText(i + "", 0 - tempWidth / 2, -mTemDialRadius + dp2px(20), mTitlePaint);
                }
            } else {
                canvas.drawLine(0, -mTemDialRadius, 0, -mTemDialRadius - mScaleHeight, mDialPaint);
            }
            canvas.rotate(mAngleOneTem);
        }
        canvas.restore();
    }

    /**
     * 画温湿度标题
     *
     * @param canvas
     */
    private void drawTemText(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        float tempWidth = mTitlePaint.measureText(mTempTitle);
        float humWidth = mTitlePaint.measureText(mHumTitle);
        canvas.drawText(mTempTitle, -tempWidth / 2, mTemDialRadius + dp2px(5), mTitlePaint);
        canvas.drawText(mHumTitle, -humWidth / 2, mHumInSideRadius + dp2px(7), mTitlePaint);
        canvas.restore();
    }

    /**
     * 画湿度圆环
     */
    private void drawHumArc(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(135);
        mHumCirclePaint.setColor(Color.parseColor(mHumCriBackground));
        RectF rectF = new RectF(-mHumInSideRadius, -mHumInSideRadius, mHumInSideRadius, mHumInSideRadius);
        //画背景
        canvas.drawArc(rectF, 0, 270, false, mHumCirclePaint);
        mHumCirclePaint.setColor(Color.parseColor(mHumCriForeground));
        //画前景
        canvas.drawArc(rectF, 0, mAngleOneHum * mHumidity, false, mHumCirclePaint);
        canvas.restore();
    }

    /**
     * 画当前湿度值的小圆点
     */
    private void drawHumValueArc(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        //旋转到0，再旋转到当前湿度值
        canvas.rotate(-135 + mAngleOneHum * mHumidity);
        //画背景
        mHumValueCirclePaint.setColor(Color.parseColor(mHumCriValueBackGround));
        canvas.drawCircle(0, -mHumInSideRadius, (mHumCriWidth + dp2px(10)) / 2, mHumValueCirclePaint);
        //画前景
        mHumValueCirclePaint.setColor(Color.parseColor(mHumCriValue));
        canvas.drawCircle(0, -mHumInSideRadius, (mHumCriWidth + dp2px(7)) / 2, mHumValueCirclePaint);
        canvas.restore();
    }

    /**
     * 左边右边分别画一个小圆点
     *
     * @param canvas
     */
    private void drwaHumArcLeftAndRight(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        //旋转到0
        canvas.rotate(-135);
        canvas.drawCircle(0, -mHumInSideRadius, mHumCriWidth / 2, mHumLeftCirclePaint);
        canvas.rotate(270);
        canvas.drawCircle(0, -mHumInSideRadius, mHumCriWidth / 2, mHumRightCirclePaint);
        canvas.restore();
    }

    /**
     * 画湿度的文字
     */
    private void drawHumText(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        // 顺时针旋转135度
        canvas.rotate(-135);

        for (int i = mMinHum; i <= mMaxHum; i++) {
            if (i % 10 == 0) {
                float tempWidth = mTitlePaint.measureText(i + "");
                canvas.drawText(i + "", 0 - tempWidth / 2, -mHumInSideRadius - mHumCriWidth, mTitlePaint);
            }
            canvas.rotate(mAngleOneHum);
        }
        canvas.restore();
    }


    public void setMinTemp(int minTemp) {
        setData(minTemp, mMaxTemp, mTemperature, mMinHum, mMaxHum, mHumidity);
    }

    public void setMaxTemp(int maxTemp) {
        setData(mMinTemp, maxTemp, mTemperature, mMinHum, mMaxHum, mHumidity);
    }

    public void setTemp(int temp) {
        setData(mMinTemp, mMaxTemp, temp, mMinHum, mMaxHum, mHumidity);
    }


    private void setTempTitle(String tempTitle) {
        this.mTempTitle = tempTitle;
        invalidate();

    }

    public void setMinHum(int minHum) {
        setData(mMinTemp, mMaxTemp, mTemperature, minHum, mMaxHum, mHumidity);
    }

    public void setMaxHum(int maxHum) {
        setData(mMinTemp, mMaxTemp, mTemperature, mMinHum, maxHum, mHumidity);
    }

    public void setHum(int hum) {
        setData(mMinTemp, mMaxTemp, mTemperature, mMinHum, mMaxHum, hum);
    }


    private void setHumTitle(String humTitle) {
        this.mHumTitle = humTitle;
        invalidate();
    }

    /**
     * @param minTemp 最小温度
     * @param maxTemp 最大温度
     * @param temp    设置的温度
     * @param minHum  最小湿度
     * @param maxHum  最大湿度
     * @param hum     当前湿度
     */
    public void setData(int minTemp, int maxTemp, int temp, int minHum, int maxHum, int hum) {
        this.mMinTemp = minTemp;
        this.mMaxTemp = maxTemp;
        if (temp < minTemp) {
            this.mTemperature = minTemp;
        } else if (temp > maxTemp) {
            this.mTemperature = maxTemp;
        } else {
            this.mTemperature = temp;
        }

        this.mMinHum = minHum;
        this.mMaxHum = maxHum;
        if (hum < minHum) {
            this.mHumidity = minHum;
        } else if (hum > maxHum) {
            this.mHumidity = maxHum;
        } else {
            this.mHumidity = hum;
        }

        mAngleOneTem = (float) 270 / (mMaxTemp - mMinTemp);
        mAngleOneHum = (float) 270 / (mMaxHum - mMinHum);

        invalidate();
    }


    public int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }
}
