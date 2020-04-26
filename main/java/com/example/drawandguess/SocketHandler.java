package com.example.drawandguess;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SocketHandler extends Thread{

    private String serviceAddress;
    private Integer port;
    private String mstr;

    public SocketHandler(String serviceAddress, String port, String string)
    {
        this.serviceAddress=serviceAddress;
        this.port = Integer.parseInt(port);
        this.mstr=string;
    }

    @Override
    public void run() {
        Socket socket=new Socket();
        try {
            //创建一个client socket,设置需连接的服务端的IP地址，端口号和超时时间
            socket.connect(new InetSocketAddress(serviceAddress, port), 500);
            OutputStream outputStream=socket.getOutputStream();//获取输出流
            ByteArrayOutputStream out=new ByteArrayOutputStream();
            out.write(mstr.getBytes());
            outputStream.write(out.toByteArray()); //发送数据

            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {          //在完成传输或发生异常时清理任何打开的socket。
            if (socket!=null)
            {
                if (socket.isConnected())
                {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
