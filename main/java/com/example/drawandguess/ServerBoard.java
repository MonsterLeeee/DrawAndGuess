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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerBoard extends AppCompatActivity implements View.OnClickListener{

    private TextView tv;
    private Integer ques=0;
    private Button start;

    private Handler drawpathHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mDrawingBoard.renovate();
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
        setContentView(R.layout.activity_server_board);
        tv = findViewById(R.id.title);
        start = findViewById(R.id.start);
        initView();
        initEvent();
        addSliderListener();

        tv.setText("点击开始");

        new getpath(Server.client_port1,Server.client_address2, Server.client_address3).start();
        new getpath(Server.client_port2,Server.client_address1, Server.client_address3).start();
        new getpath(Server.client_port3,Server.client_address1, Server.client_address2).start();
        new receivechexiao(Server.client_port1,Server.client_address2, Server.client_address3).start();
        new receivechexiao(Server.client_port2,Server.client_address1, Server.client_address3).start();
        new receivechexiao(Server.client_port3,Server.client_address1, Server.client_address2).start();
        new receivefanchexiao(Server.client_port1,Server.client_address2, Server.client_address3).start();
        new receivefanchexiao(Server.client_port2,Server.client_address1, Server.client_address3).start();
        new receivefanchexiao(Server.client_port3,Server.client_address1, Server.client_address2).start();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_ques();
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
                new fanchexiao(Server.client_address1).start();
                new fanchexiao(Server.client_address2).start();
                new fanchexiao(Server.client_address3).start();
                mDrawingBoard.lastStep();
                break;
            case R.id.iv_last:
                new chexiao(Server.client_address1).start();
                new chexiao(Server.client_address2).start();
                new chexiao(Server.client_address3).start();
                mDrawingBoard.nextStep();
                break;
        }
    }

    public class getpath extends Thread{

        private String maddress1;
        private String maddress2;
        private Integer mport;

        public getpath(Integer port, String address1, String address2){
            mport = port;
            maddress1 = address1;
            maddress2 = address2;
        }

        @Override
        public void run(){
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket(mport);

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

                    new SocketHandler(maddress1, "4545", str).start();
                    new SocketHandler(maddress2, "4545", str).start();

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

    public void get_ques(){

        /*
        你画我猜中的题目集合为{玫瑰花、水杯、眼镜}对应数字{0，1，2}
         */
        final long l = System.currentTimeMillis();
        ques = (Integer)((int)(l%3));
        String quest;
        switch(ques){
            case 0: quest = "玫瑰花";break;
            case 1: quest = "水杯";break;
            default: quest = "眼镜";break;
        }
        tv.setText(quest);
        new SocketHandler(Server.client_address1,"4541", ques.toString()).start();
        new SocketHandler(Server.client_address2,"4541", ques.toString()).start();
        new SocketHandler(Server.client_address3,"4541", ques.toString()).start();
    }

    public class chexiao extends Thread{

        private String maddress;

        public chexiao(String str){maddress = str;}

        @Override
        public void run(){
            new SocketHandler(maddress,"4549", "100").start();
        }
    }

    public class fanchexiao extends Thread{

        private String maddress;

        public fanchexiao(String str){maddress = str;}

        @Override
        public void run(){
            new SocketHandler(maddress,"4550", "100").start();
        }
    }

    public class receivechexiao extends Thread{

        private String maddress1;
        private String maddress2;
        private Integer mport;

        public receivechexiao(Integer port, String address1, String address2){
            mport = port;
            maddress1 = address1;
            maddress2 = address2;
        }

        @Override
        public void run(){
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket((mport+10));

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
                        new chexiao(maddress1).start();
                        new chexiao(maddress2).start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class receivefanchexiao extends Thread{

        private String maddress1;
        private String maddress2;
        private Integer mport;

        public receivefanchexiao(Integer port, String address1, String address2){
            mport = port;
            maddress1 = address1;
            maddress2 = address2;
        }

        @Override
        public void run(){
            byte[] buf=new byte[1024];
            int len;
            try {
                ServerSocket serverSocket=new ServerSocket((mport+20));

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
                        new fanchexiao(maddress1).start();
                        new fanchexiao(maddress2).start();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}