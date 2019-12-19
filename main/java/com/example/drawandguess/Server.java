package com.example.drawandguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Server extends AppCompatActivity {

    private TextView title;
    private Button jump_to_serverboard;
    private EditText client1;
    private EditText client2;
    private EditText client3;
    private EditText port1;
    private EditText port2;
    private EditText port3;

    public static String client_address1;
    public static String client_address2;
    public static String client_address3;
    public static Integer client_port1;
    public static Integer client_port2;
    public static Integer client_port3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        title = findViewById(R.id.title);
        jump_to_serverboard = findViewById(R.id.button);
        client1 = findViewById(R.id.client1);
        client2 = findViewById(R.id.client2);
        client3 = findViewById(R.id.client3);
        port1 = findViewById(R.id.port1);
        port2 = findViewById(R.id.port2);
        port3 = findViewById(R.id.port3);

        WifiManager wifiManager = (WifiManager) Server.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
        title.setText(ipAddress);

        edit_init();

        jump_to_serverboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client_address1 = client1.getText().toString();
                client_address2 = client2.getText().toString();
                client_address3 = client3.getText().toString();
                client_port1 = Integer.parseInt(port1.getText().toString());
                client_port2 = Integer.parseInt(port2.getText().toString());
                client_port3 = Integer.parseInt(port3.getText().toString());
                client1.setText("");
                client2.setText("");
                client3.setText("");
                port1.setText("");
                port2.setText("");
                port3.setText("");
                Intent intent = new Intent(Server.this, ServerBoard.class);
                startActivity(intent);
            }
        });

    }

    public void edit_init(){
        client1.setFocusable(true);
        client1.setFocusableInTouchMode(true);
        client1.requestFocus();
    }

    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
