package com.example.bjy.sockettest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView txtshow;
    private EditText editsend;
    private Button btnsend;
    private static final String HOST = "192.168.1.9";
    private static final int PORT = 12345;
    private StringBuilder sb = null;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtshow = (TextView) findViewById(R.id.txtshow);
        editsend = (EditText) findViewById(R.id.editsend);
        btnsend = (Button) findViewById(R.id.btnsend);
        sb = new StringBuilder();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    sb.append(msg.obj.toString());
                    txtshow.setText(sb.toString());
                }
            }
        };

        new Thread(){
            @Override
            public void run() {
                try {
                    socket = new Socket(HOST,PORT);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
                    while (!MainActivity.this.isFinishing()){
                        if (socket.isConnected()){
                            if (!socket.isInputShutdown()){
                                String msg = in.readLine();
                                Message message = new Message();
                                message.what = 0x123;
                                message.obj = msg;
                                handler.sendMessage(message);
                            }
                        }

                    }
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = editsend.getText().toString();
                if (socket.isConnected()){
                    if (!socket.isOutputShutdown()){
                        out.println(msg);
                    }
                }
            }
        });
    }
}
