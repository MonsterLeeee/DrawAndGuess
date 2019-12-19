package com.example.drawandguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static Integer user_type;

    private Button server;
    private Button client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        server = findViewById(R.id.server);
        client = findViewById(R.id.client);

        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_type = 0;
                Intent intent = new Intent(MainActivity.this, Server.class);
                startActivity(intent);
            }
        });
        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_type = 1;
                Intent intent = new Intent(MainActivity.this, Client.class);
                startActivity(intent);
            }
        });

    }
}
