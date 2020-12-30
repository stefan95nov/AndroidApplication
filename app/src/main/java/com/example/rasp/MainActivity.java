package com.example.rasp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    Socket myAppSocket = null;
    Button cwBtn;
    Button ccwBtn;
    Button accBtn;
    Button deaccBtn;
    EditText ipAdr;
    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;
    public String CMD;
    LineGraphSeries<DataPoint> series;
    GraphView graph;
    double pwm = 50;
    double x = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cwBtn = (Button) findViewById(R.id.cwBtn);
        ccwBtn = (Button) findViewById(R.id.ccwBtn);
        accBtn = (Button) findViewById(R.id.accBtn);
        deaccBtn = (Button) findViewById(R.id.deaccBtn);
        graph = (GraphView) findViewById(R.id.graph);
        ipAdr = (EditText) findViewById(R.id.ipAdr);
        series = new LineGraphSeries<DataPoint>();

        cwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIPandPort();
                ++x;
                CMD = "CW";
                series.appendData(new DataPoint(x,pwm), true, 500);
                graph.addSeries(series);
                Socket_AsyncTask cmd_cw = new Socket_AsyncTask();
                cmd_cw.execute();
            }
        });

        ccwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIPandPort();
                ++x;
                CMD = "CCW";
                series.appendData(new DataPoint(x,pwm), true, 500);
                graph.addSeries(series);
                Socket_AsyncTask cmd_ccw = new Socket_AsyncTask();
                cmd_ccw.execute();
            }
        });

        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIPandPort();
                if(pwm < 100) {
                    pwm = pwm + 5;
                }
                ++x;
                CMD = "ACC";
                series.appendData(new DataPoint(x,pwm), true, 500);
                graph.addSeries(series);
                Socket_AsyncTask cmd_acc = new Socket_AsyncTask();
                cmd_acc.execute();
            }
        });

        deaccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIPandPort();
                if(pwm > 0) {
                    pwm = pwm - 5;
                }
                ++x;
                CMD = "DEACC";
                series.appendData(new DataPoint(x,pwm), true, 500);
                graph.addSeries(series);
                Socket_AsyncTask cmd_deacc = new Socket_AsyncTask();
                cmd_deacc.execute();
            }
        });

    }
    public void getIPandPort()
    {
        String IPandPort = ipAdr.getText().toString();
        Log.d("MYTEST","IP String:" + IPandPort);
        String temp[] = IPandPort.split(":");
        wifiModuleIp = temp[0];
        wifiModulePort = Integer.valueOf(temp[1]);
        Log.d("MYTEST", "IP: " + wifiModuleIp);
        Log.d("MYTEST", "PORT: " + wifiModulePort);
    }

    public class Socket_AsyncTask extends AsyncTask<Void, Void, Void>
    {
        Socket socket;

        @Override
        protected Void doInBackground(Void...params){
            try{
                InetAddress inetaddres = InetAddress.getByName(MainActivity.wifiModuleIp);
                socket = new java.net.Socket(inetaddres,MainActivity.wifiModulePort);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeBytes(CMD);
                dataOutputStream.close();
                socket.close();
            }catch(UnknownHostException e){e.printStackTrace();}catch(IOException e){e.printStackTrace();}
            return null;
        }
    }
}
