package com.technohacker.apps.phonemouse.net;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkThread extends AsyncTask<String, Void, Void> {

    private static String host;
    private static int port;
    private static Socket sock;

    public NetworkThread() {
	// FIXME: Put your IP address here for now.
        host = "YOUR_IP_ADDRESS_HERE";
        port = 9559;
        if(sock == null){
            try {
                sock = new Socket(host,port);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            PrintWriter out = new PrintWriter(sock.getOutputStream());
            out.println(strings[0]);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
