package com.example.drawandguess;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientBoard extends AppCompatActivity implements View.OnClickListener{

    private TextView tv;
    private TextView res;
    private EditText edit;
    private String res_str = "0";
    private Button check;

    private Handler drawpathHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mDrawingBoard.renovate();
            super.handleMessage(msg);
        }
    };

    private Handler setquesHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch(Integer.parseInt((String)msg.obj)){
                case 0: tv.setText("三个字，植物");break;
                case 1: tv.setText("两个字，日常用品");break;
                default: tv.setText("两个字");break;
            }
            res.setText("");
            super.handleMessage(msg);
        }
    };

    private Handler chexiaoHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mDrawingBoard.nextStep();
            super.handleMessage(msg);
        }
    };

    private Handler fanchexiaoHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mDrawingBoard.lastStep();
            super.handleMessage(msg);
        }
    };


    private DrawingBoard mDrawingBoard;
    private Slider mSlider;
    //代表颜色选项
    private ImageView Black;
    private ImageView Accent;
    private ImageView Primary;
    private ImageView Red;
    //对画板的操作
    private ImageView mPaint;
    private ImageView mEraser;
    private ImageView mClean;
    private ImageView mLast;
    private ImageView mNext;
    //记录画笔大小
    private float size;

    //获取像素点
    private int dip2x(float depValue){
        final float density = getResources().getDisplayMetrics().density;
        return (int)(depValue*density+0.5f);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_board);
        tv = findViewById(R.id.title);
        res = findViewById(R.id.result);
        check = findViewById(R.id.check);
        edit = findViewById(R.id.edit);
        initView();
        initEvent();
        addSliderListener();

        new getpath().start();
        new get_ques_str().start();
        new receivechexiao().start();
        new receivefanchexiao().start();

        edit.setFocusable(true);
        edit.setFocusableInTouchMode(true);
        edit.requestFocus();

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str;
                Integer result;
                str = edit.getText().toString();
                edit.setText("");
                switch(str){
                    case "玫瑰花": result = 0;break;
                    case "水杯": result = 1;break;
                    case "眼镜": result = 2;break;
                    default: result = 3;break;
                }
                if(Integer.parseInt(res_str) == result){
                    res.setText("Yes");
                }
                else{
                    res.setText("No");
                }

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //默认画笔大小
        size  = dip2x(10);
    }

    private void initView(){
        mDrawingBoard = findViewById(R.id.draw_board);
        mSlider = findViewById(R.id.slider);
        Black = findViewById(R.id.iv_one);
        Accent = findViewById(R.id.iv_two);
        Primary = findViewById(R.id.iv_three);
        Red = findViewById(R.id.iv_four);
        mPaint = findViewById(R.id.iv_paint);
        mEraser = findViewById(R.id.iv_eraser);
        mClean = findViewById(R.id.iv_clean);
        mLast = findViewById(R.id.iv_last);
        mNext = findViewById(R.id.iv_next);
    }
    //实现滑动小圆点改变画笔线条粗细大小
    private void addSliderListener(){
        mSlider.addListener(new Slider.OnSliderChangedListener() {
            @Override
            public void positionChanged(float p) {
                if (size > 0) {
                    mDrawingBoard.setmPaintSize((int) (p * size * 2));
                }
            }
        });
    }

    private void initEvent(){

        Black.setOnClickListener(this);
        Accent.setOnClickListener(this);
        Primary.setOnClickListener(this);
        Red.setOnClickListener(this);
        //设置默认画笔背景为蓝色
        mPaint.getBackground().setLevel(1);
        mPaint.getDrawable().setLevel(1);
        mPaint.setOnClickListener(this);
        mEraser.setOnClickListener(this);
        mClean.setOnClickListener(this);
        mLast.setOnClickListener(this);
        mNext.setOnClickListener(this);

    }

    //设置画板清空对话框
    private void alertDialogClean(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定要清空画板吗？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDrawingBoard.clean();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        final  AlertDialog dialog = builder.show();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_one:
                mDrawingBoard.setmPaintColor(Color.BLACK);
                break;
            case R.id.iv_two:
                mDrawingBoard.setmPaintColor(Color.parseColor("#D81B60"));
                break;
            case R.id.iv_three:
                mDrawingBoard.setmPaintColor(Color.parseColor("#008577"));
                break;
            case R.id.iv_four:
                mDrawingBoard.setmPaintColor(Color.RED);
                break;
            case R.id.iv_paint:
                if (mDrawingBoard.getMode() != DrawMode.PaintMode) {
                    mDrawingBoard.setMode(DrawMode.PaintMode);
                }
                mPaint.getDrawable().setLevel(1);
                mPaint.getBackground().setLevel(1);
                mEraser.getDrawable().setLevel(0);
                mEraser.getBackground().setLevel(0);
                break;
            case R.id.iv_eraser:
                if (mDrawingBoard.getMode() != DrawMode.EraserMode) {
                    mDrawingBoard.setMode(DrawMode.EraserMode);
                }
                mPaint.getDrawable().setLevel(0);
                mPaint.getBackground().setLevel(0);
                mEraser.getDrawable().setLevel(1);
                mEraser.getBackground().setLevel(1);
                break;
            case R.id.iv_clean:
                alertDialogClean();
                break;
            case R.id.iv_next:
                new fanchexiao().start();
                mDrawingBoard.lastStep();
                break;
            case R.id.iv_last:
                new chexiao().start();
                mDrawingBoard.nextStep();
                break;
        }
    }

    public class getpath extends Thread{

        @Override
        public void run(){
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket(4545);

                while(true) {
                    Socket client=serverSocket.accept();
                    String str;
                    Path wpath = new Path();
                    Paint wpaint;
                    InputStream inputstream=client.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((len = inputstream.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    str = out.toString();

                    float x, y, lastx, lasty;
                    int pcolor, psize;
                    String[] strArray = str.split(",");
                    pcolor = Integer.parseInt(strArray[0]);
                    psize = Integer.parseInt(strArray[1]);


                    //设置画笔抗锯齿和抖动
                    wpaint = new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
                    //设置画笔填充方式为只描边
                    wpaint.setStyle(Paint.Style.STROKE);
                    //设置画笔颜色
                    wpaint.setColor(pcolor);
                    //设置画笔宽度
                    wpaint.setStrokeWidth(psize);
                    //设置圆形线帽
                    wpaint.setStrokeJoin(Paint.Join.ROUND);
                    //设置线段连接处为圆角
                    wpaint.setStrokeCap(Paint.Cap.ROUND);
                    if(pcolor == Color.WHITE){wpaint.setXfermode(mDrawingBoard.mEraserMode);}
                    else{wpaint.setXfermode(null);}


                    x = Float.parseFloat(strArray[2]);
                    y = Float.parseFloat((strArray[3]));
                    mDrawingBoard.startList.add(new Startpoint(x,y));

                    wpath.moveTo(x,y);
                    lastx = x;
                    lasty = y;
                    for(int i = 4; i < strArray.length; ) {
                        x = Float.parseFloat(strArray[i]);
                        ++i;
                        y = Float.parseFloat(strArray[i]);
                        ++i;

                        wpath.quadTo(lastx, lasty, (lastx+x)/2, (lasty+y)/2);

                        lastx = x;
                        lasty = y;
                    }
                    mDrawingBoard.wPathList.add(new DrawPathList(wpaint, wpath));
                    mDrawingBoard.savewPathList();

                    new drawpath(wpath).start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class drawpath extends Thread {

        private Path mpath;

        public drawpath(Path path)
        {
            mpath = path;
        }

        @Override
        public void run() {
            Message message = new Message();
            message.obj = mpath;
            drawpathHandler.sendMessage(message);
        }
    }

    public class get_ques_str extends Thread{

        @Override
        public void run(){
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket(4541);

                while(true) {
                    Socket client=serverSocket.accept();

                    String str;
                    InputStream inputstream=client.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((len = inputstream.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    str = (String)out.toString();
                    res_str = str;

                    new setques(str).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public class setques extends Thread{

        private String mstr;
        public  setques(String str){mstr = str;}

        @Override
        public void run() {
            Message message = new Message();
            message.obj = mstr;
            setquesHandler.sendMessage(message);
        }

    }

    public class chexiao extends Thread{
        @Override
        public void run(){
            Integer port = Integer.parseInt(Client.server_port) + 10;
            new SocketHandler(Client.server_address, port.toString(), "100").start();
        }
    }

    public class fanchexiao extends Thread{
        @Override
        public void run(){
            Integer port = Integer.parseInt(Client.server_port) + 20;
            new SocketHandler(Client.server_address, port.toString(), "100").start();
        }
    }

    public class receivechexiao extends Thread{

        @Override
        public void run(){
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket(4549);

                while(true) {
                    Socket client=serverSocket.accept();

                    InputStream inputstream=client.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((len = inputstream.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    if(Integer.parseInt(out.toString()) == 100){
                        Message message = new Message();
                        chexiaoHandler.sendMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class receivefanchexiao extends Thread{

        @Override
        public void run(){
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket(4550);

                while(true) {
                    Socket client=serverSocket.accept();

                    InputStream inputstream=client.getInputStream();
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((len = inputstream.read(buf)) != -1) {
                        out.write(buf, 0, len);
                    }
                    if(Integer.parseInt(out.toString()) == 100){
                        Message message = new Message();
                        fanchexiaoHandler.sendMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
