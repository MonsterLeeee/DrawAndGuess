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

public class Client extends AppCompatActivity {

    private TextView title;
    private Button jump_to_clientboard;
    private EditText server;
    private EditText port;

    public static String server_address;
    public static String server_port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        title = findViewById(R.id.title);
        jump_to_clientboard = findViewById(R.id.button);
        server = findViewById(R.id.server);
        port = findViewById(R.id.port);

        WifiManager wifiManager = (WifiManager) Client.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());
        title.setText(ipAddress);

        edit_init();

        jump_to_clientboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server_address = server.getText().toString();
                server_port = port.getText().toString();
                server.setText("");
                port.setText("");
                Intent intent = new Intent(Client.this, ClientBoard.class);
                startActivity(intent);
            }
        });

    }

    public void edit_init(){
        server.setFocusable(true);
        server.setFocusableInTouchMode(true);
        server.requestFocus();
    }

    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
