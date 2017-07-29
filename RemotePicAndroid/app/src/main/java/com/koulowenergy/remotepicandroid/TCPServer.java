package com.koulowenergy.remotepicandroid;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by bdm on 17/07/29.
 */

public class TCPServer extends Thread {
    public Delegate delegate;
    public void run() {
        try {
            Log.d("TCP", "Listening...");
            ServerSocket server = new ServerSocket(15000);
            while (true) {
                Socket client = server.accept();
                Log.d("TCP", "receiving");

                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String str = in.readLine();
                    Log.d("TCP", "---received---: " + str);
                    if (this.delegate != null) {
                        this.delegate.callbackString(str);
                    }
                } catch (Exception e) {
                    Log.d("TCP", "error in receiving.");
                } finally {
                    client.close();
                }
            }
        } catch (Exception e) {
            Log.d("TCP", "error:" + e);
            e.printStackTrace();
        }
    }
}
