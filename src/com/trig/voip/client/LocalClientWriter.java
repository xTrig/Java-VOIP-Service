package com.trig.voip.client;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Arrays;

public class LocalClientWriter extends Thread {

    private Socket socket;
    private BufferedOutputStream writer;
    private byte[] data;
    private VOIPClient client;

    public LocalClientWriter(Socket socket, VOIPClient client) {
        this.socket = socket;
        this.client = client;

        init(); //Setup the BufferedOutputStream
    }

    public void sendMessage(String data) {
        sendRaw((data + "\n").getBytes());
    }

    public void sendRaw(byte[] data) {
        this.data = data;
        if(data.length < 3) { //Don't send data if the length is less than 3 bytes
            return;
        }
        if(this.data.length > 1000) {
            System.out.println("Splitting packet of length " + this.data.length);
            byte[] d2 = Arrays.copyOfRange(this.data, 1000, this.data.length);
            this.data = Arrays.copyOfRange(this.data, 0, 999);
            run();
            this.data = d2;
            run();
            return;
        }
        run();

    }

    private void init() {
        try {
            writer = new BufferedOutputStream(socket.getOutputStream());
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            writer.write(data);
            writer.flush();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
