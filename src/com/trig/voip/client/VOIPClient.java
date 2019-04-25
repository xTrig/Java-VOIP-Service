package com.trig.voip.client;

import javax.swing.*;
import java.net.Socket;

public class VOIPClient {

    private MainGUI gui;
    private Socket socket;
    private LocalClientWriter writer;
    private LocalClientReader reader;
    private volatile boolean ready = false;

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
            socket = new Socket("192.168.1.121", 8000);
            reader = new LocalClientReader(socket, this);
            writer = new LocalClientWriter(socket, this);
            Thread readerThread = new Thread(reader);
            readerThread.start();
            writeToConsole("Connected to server!");

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

        writer.sendMessage(data);
    }

    public void sendRaw(byte[] data, int count) {
        writer.sendRaw(data, count);
    }

    public void sendVoice(byte[] data, int count) {
        String voicePkt = "05 " + new String(data) + '\n';
        sendRaw(voicePkt.getBytes(), count + 4);
        System.out.println("Voice packet: " + voicePkt + "\nLength: " + (count + 4));

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