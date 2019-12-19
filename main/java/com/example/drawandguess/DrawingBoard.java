package com.example.drawandguess;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class DrawingBoard extends View {

    //默认画笔模式

    public int getmPaintColor() {
        return mPaintColor;
    }

    public void setmPaintColor(int mPaintColor) {
        this.mPaintColor = mPaintColor;
        mPaint.setColor(mPaintColor);
    }

    private DrawMode mDrawMode = DrawMode.PaintMode;

    public DrawMode getMode() {
        return mDrawMode;
    }
    //设置画笔模式
    public void setMode(DrawMode mode){
        if (mode != mDrawMode){
            if (mode == DrawMode.EraserMode){
                //橡皮擦模式
                mPaint.setStrokeWidth(mEraseSize);
                mPaint.setXfermode(mEraserMode);
                //mPaint.setXfermode(null);
                mPaint.setColor(Color.WHITE);
            }
            else {
                //画笔模式
                mPaint.setStrokeWidth(mPaintSize);
                mPaint.setXfermode(null);
                mPaint.setColor(mPaintColor);
            }
        }
        mDrawMode = mode;
    }

    //当前控件的宽高
    private int mWidth;
    private int mHeight;

    //画笔
    private Paint mPaint;
    //画笔颜色，默认黑色
    private int mPaintColor = Color.BLACK;
    //画布颜色，默认白色
    private int mCanvasColor = Color.WHITE;
    //画笔宽度,默认10个像素点
    private int mPaintSize = dip2x(5);
    //橡皮擦宽度
    private int mEraseSize = dip2x(20);
    //缓冲的位图
    public static Bitmap mBufferBitmap;
    //缓冲的画布
    private Canvas mBufferCanvas;
    //上次的位置
    private float mLastX;
    private float mLastY;
    //路径
    public static Path mPath;
    //设置图形混合模式为清除，橡皮擦的功能
    public static PorterDuffXfermode mEraserMode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);

    //str
    private String str = "0.0,0.0";
    public static Path wPath = new Path();
    public static Paint wPaint = new Paint();
    public static List<DrawPathList> wPathList;
    public static List<Startpoint> startList;
    public static List<DrawPathList> wPathList_save;
    public static List<DrawPathList> wPathList_curr;

    //保存的路径
    private List<DrawPathList> savePaths;
    //当前的路径
    private List<DrawPathList> currPaths;
    //最多保存20条路径
    private int MAX = 20;
    public DrawingBoard(Context context) {
        this(context,null);
    }

    public DrawingBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
        initPath();
    }
    //获取像素点
    private int dip2x(float depValue){
        final float density = getResources().getDisplayMetrics().density;
        return (int)(depValue*density+0.5f);
    }

    private void initPath(){
        mPath = new Path();
        savePaths = new ArrayList<>();
        currPaths = new ArrayList<>();

        //初始化wpathlist
        wPathList = new ArrayList<>();
        startList = new ArrayList<>();
        startList.add(new Startpoint(0,1));
        startList.add(new Startpoint(1,0));

        wPathList_save = new ArrayList<>();
        wPathList_curr = new ArrayList<>();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
        initCanvas();
    }

    private void initCanvas(){
        //创建一个BITMAP，BITMAP就是Canvas绘制的图片
        mBufferBitmap = Bitmap.createBitmap(mWidth,mHeight,Bitmap.Config.ARGB_8888);
        mBufferCanvas= new Canvas(mBufferBitmap);
        mBufferCanvas.drawColor(mCanvasColor);
        mBufferBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    private void initPaint(){
        //设置画笔抗锯齿和抖动
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        //设置画笔填充方式为只描边
        mPaint.setStyle(Paint.Style.STROKE);
        //设置画笔颜色
        mPaint.setColor(mPaintColor);
        //设置画笔宽度
        mPaint.setStrokeWidth(mPaintSize);
        //设置圆形线帽
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        //设置线段连接处为圆角
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        //设置画笔抗锯齿和抖动
        wPaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        //设置画笔填充方式为只描边
        wPaint.setStyle(Paint.Style.STROKE);
        //设置画笔颜色
        wPaint.setColor(mPaintColor);
        //设置画笔宽度
        wPaint.setStrokeWidth(mPaintSize);
        //设置圆形线帽
        wPaint.setStrokeJoin(Paint.Join.ROUND);
        //设置线段连接处为圆角
        wPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBufferBitmap,0,0,null);

        //加入list进行绘制

        if(mDrawMode != DrawMode.EraserMode) {
            for (int i = 0; i < wPathList.size(); ++i) {
                mBufferCanvas.drawPath(wPathList.get(i).getPath(), wPathList.get(i).getPaint());
            }
            if(startList.get(startList.size()-1).x ==startList.get(startList.size()-2).x && startList.get(startList.size()-1).y ==startList.get(startList.size()-2).y){
                wPathList.remove(wPathList.size()-2);
                startList.remove(startList.size()-2);
                savewPathList();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x =event.getX();
        float y =event.getY();
        switch (event.getAction())

        {
            case MotionEvent.ACTION_DOWN:

                mLastX = x;
                mLastY = y;
                startList.add(new Startpoint(x,y));
                mPath.moveTo(mLastX,mLastY);
                //把颜色放在第一个字符，size放在第二个字符
                if(mDrawMode != DrawMode.EraserMode)
                    str = Integer.toString(mPaintColor) + ',';
                else
                    str = Integer.toString(Color.WHITE) + ',';
                if(mDrawMode != DrawMode.EraserMode)
                    str = str + Integer.toString(mPaintSize) + ',';
                else
                    str = str + Integer.toString(mEraseSize) +',';
                str = str + Float.toString(x) + ','+ Float.toString(y) + ',';
                break;
            case MotionEvent.ACTION_MOVE:
                //绘制画出的图形
                mPath.quadTo(mLastX,mLastY,(mLastX+x)/2,(mLastY+y)/2);
                str = str + Float.toString(x) + ',' + Float.toString(y) + ',';
                mBufferCanvas.drawPath(mPath,mPaint);

                if(MainActivity.user_type == 0){
                    new SocketHandler(Server.client_address1,"4545",str).start();
                    new SocketHandler(Server.client_address2,"4545",str).start();
                    new SocketHandler(Server.client_address3,"4545",str).start();
                }
                else{
                    new SocketHandler(Client.server_address,Client.server_port,str).start();
                }

                invalidate();
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                Path path_temp1 = new Path(mPath);
                Paint paint_temp1 = new Paint(mPaint);

                wPathList.add(new DrawPathList(paint_temp1, path_temp1));

                savewPathList();

                //保存路径
                savePath();
                mPath.reset();
                break;

        }

        return true;
    }
    private void savePath(){
        if (savePaths.size() == MAX){
            savePaths.remove(0);
        }
        savePaths.clear();
        savePaths.addAll(currPaths);
        Path path = new Path(mPath);
        Paint paint = new Paint(mPaint);
        savePaths.add(new DrawPathList(paint,path));
        currPaths.add(new DrawPathList(paint,path));
    }

    public void clean(){
        wPathList.clear();
        startList.clear();
        startList.add(new Startpoint(0,1));
        startList.add(new Startpoint(1,0));
        wPathList_save.clear();
        wPathList_curr.clear();
        //将位图变为透明的
        mBufferBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    public void savewPathList()
    {
        if (wPathList_save.size() == MAX){
            wPathList_save.remove(0);
        }
        wPathList_save.clear();
        wPathList_save.addAll(wPathList);
        Path path = new Path(mPath);
        Paint paint = new Paint(mPaint);
        wPathList_save.add(new DrawPathList(paint,path));

    }

    /**
     * 下一步 撤销
     * */
    public void nextStep() {
        if (wPathList.size() > 0) {
            wPathList.remove(wPathList.size() - 1);
            if(wPathList.size() > 0)
            {reDrawBitmap();}
            else{
                mBufferBitmap.eraseColor(Color.TRANSPARENT);
                invalidate();
            }
        }
    }

    public int getmPaintSize() {
        return mPaintSize;
    }

    public void setmPaintSize(int mPaintSize) {
        this.mPaintSize = mPaintSize;
        mPaint.setStrokeWidth(mPaintSize);
    }

    /**
     * 上一步 反撤销
     * */
    public void lastStep(){
        if (wPathList != wPathList_save)
        {
            if (wPathList_save.size()>wPathList.size()){
                wPathList.add(wPathList_save.get(wPathList.size()));
                reDrawBitmap();
            }
        }
    }
    //重绘位图
    private void reDrawBitmap(){
        mBufferBitmap.eraseColor(Color.TRANSPARENT);

        mBufferCanvas.drawPath(wPathList.get(wPathList.size()-1).getPath(),wPathList.get(wPathList.size()-1).getPaint());
        invalidate();
    }

    public void renovate(){
        invalidate();
    }

}