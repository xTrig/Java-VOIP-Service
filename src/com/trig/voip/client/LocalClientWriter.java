package com.trig.voip.client;

import java.io.DataOutputStream;
import java.net.Socket;

public class LocalClientWriter extends Thread {

    private Socket socket;
    private DataOutputStream writer;
    private byte[] data;
    private int length;
    private VOIPClient client;

    public LocalClientWriter(Socket socket, VOIPClient client) {
        this.socket = socket;
        this.client = client;

        init(); //Setup the DataOutputStream
    }

    public void sendMessage(String data) {
        data = data + "\n";
        sendRaw(data.getBytes(), data.length());
    }

    public void sendRaw(byte[] data, int length) {
        this.data = data;
        this.length = length;
        if(length < 3) { //Don't send data if the length is less than 3 bytes
            return;
        }
        run();

    }

    private void init() {
        try {
            writer = new DataOutputStream(socket.getOutputStream());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            writer.write(data, 0, length);
            writer.flush();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
