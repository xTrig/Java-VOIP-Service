package com.trig.voip.client;

import javax.swing.*;
import java.net.Socket;

public class VOIPClient {

    private MainGUI gui;
    private Socket socket;
    private Socket micSocket;
    private LocalClient client;
    private volatile boolean ready = false;
    private static final String HOST = "localhost"; //192.168.1.121
    private static final int PORT = 8000;
    private static final int MIC_PORT = 8001;

    private LocalMicWriter micWriter;

    public void start() {
        //Setup the look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            exc.printStackTrace();
        }


        //Launch the GUI
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(gui == null) {
                    gui = new MainGUI(VOIPClient.this);
                    gui.setVisible(true);
                }
            }
        });


        //Now we wait for the GUI to start up, and then call the ready() interrupt before we continue
        //so that we can output some data to the GUI
        while(!ready) {
            try {
                Thread.sleep(100);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
        writeToConsole("Connecting to server...");
        System.out.println("Connecting to server...");
        try {
            socket = new Socket(HOST, PORT);
            client = new LocalClient(this, socket, false);
            client.init();
            writeToConsole("Connected to server!");
            sendMessage("01 Client"); //ID this socket as the client
            writeToConsole("ID packet sent!");
            writeToConsole("Starting voice communications link...");
            micSocket = new Socket(HOST, MIC_PORT);
            LocalClient micClient = new LocalClient(this, micSocket, true);
            micClient.init();
            Thread.sleep(500);
            if(client.attachMic(micClient)) {
                writeToConsole("Mic communications link established!");
            } else {
                writeToConsole("Mic communications link failed");
            }
        } catch (Exception exc) {
            exc.printStackTrace();
            writeToConsole("Failed to connect to server!");
        }

    }

    public void writeToConsole(String msg) {

        gui.writeToConsole(msg);
    }

    public void ready() {
        ready = true;
    }

    public void sendMessage(String data) {

        client.sendMessage(data);
    }

    public void sendRaw(byte[] data, int count) {

        client.sendRaw(data, count);
    }

    public void sendVoice(byte[] data, int count) {
        client.getMicClient().sendVoice(data, count);
//        String voicePkt = "05 " + new String(data) + '\n';
//        sendRaw(voicePkt.getBytes(), count + 4);
//        System.out.println("Voice packet: " + voicePkt + "\nLength: " + (count + 4));

    }

    public synchronized void micTest() {
        if(micWriter == null) {
            micWriter = new LocalMicWriter(this);
            micWriter.start();
        } else {
            micWriter.dispose();
            micWriter = null;
            micTest();
        }

    }
}
