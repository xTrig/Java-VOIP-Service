package com.trig.voip.client;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class LocalClientWriter extends Thread {

    private Socket socket;
    private BufferedWriter writer;
    private String data;
    private VOIPClient client;

    public LocalClientWriter(Socket socket, VOIPClient client) {
        this.socket = socket;
        this.client = client;

        init(); //Setup the BufferedWriter
    }

    public void sendMessage(String data) {
        this.data = data;
        run();
    }

    private void init() {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            writer.write(data);
            writer.newLine();
            writer.flush();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
