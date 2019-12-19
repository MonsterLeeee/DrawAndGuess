package com.example.drawandguess;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Slider extends View {

    //画滑动条
    private Paint paint;
    //画小圆点
    private Paint paint1;
    //画进度条
    private Paint paint2;
    //小圆点的半径
    private int radiux = dip2x(6);

    //滑动条宽度
    private int lineSize = dip2x(3);
    //记录小圆点的横坐标或者纵坐标
    public float position;

    //记录监听者
    private OnSliderChangedListener listener;

    private int dip2x(float depValue){
        float density = getResources().getDisplayMetrics().density;
        return (int)(depValue*density+0.5f);
    }

    public Slider(Context context) {
        this(context,null);
    }

    public Slider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置颜色为灰色
        paint.setColor(Color.parseColor("#666666"));
        paint.setStrokeWidth(lineSize);
        paint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        //设置颜色为紫红色
        paint1.setColor(Color.MAGENTA);
        paint1.setStrokeWidth(lineSize);
        paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(Color.RED);
        paint.setStrokeWidth(lineSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){


            case MotionEvent.ACTION_DOWN:
                if (isHor()){
                    position = event.getY();
                }
                else {
                    position = event.getX();
                }
                callBack();
                break;

            case MotionEvent.ACTION_MOVE:
                if (isHor()){
                    position = event.getY();
                }
                else {
                    position = event.getX();
                }
                callBack();
                break;

            case MotionEvent.ACTION_UP:
                break;

        }
        //重绘，调用onDraw方法
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isHor()) {
            //画滑动条,竖屏
            canvas.drawLine(getWidth()/2,0,getWidth()/2,getHeight(),paint);
            canvas.drawCircle(getWidth()/2,position,radiux,paint1);
            //画进度条
            canvas.drawLine(getWidth()/2,0,getWidth()/2,position,paint2);
        }
        else {
            //横屏
            canvas.drawLine(0,getHeight()/2,getWidth(),getHeight()/2,paint);
            canvas.drawCircle(position,getHeight()/2,radiux,paint1);
            //画进度条
            canvas.drawLine(0,getHeight()/2,position,getHeight()/2,paint2);
        }
    }

    //判断屏幕是横屏还是竖屏
    private boolean isHor(){
        if (getWidth()<getHeight()){
            //竖屏
            return true;
        }
        else {
            //横屏
            return false;
        }
    }
    /**
     * 接口，实现滑动条滑动长度position/滑动条长度的比值的回调
     * */
    public interface OnSliderChangedListener{
        void positionChanged(float p);
    }

    public void addListener(OnSliderChangedListener listener){
        this.listener = listener;
    }

    //数据回调方法
    private void callBack(){
        if (listener != null)
        {
            if (isHor()) {
                listener.positionChanged(position / getHeight());
            }
            else {
                listener.positionChanged(position/getWidth());
            }
        }
    }

}