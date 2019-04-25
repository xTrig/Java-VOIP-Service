package com.trig.voip.client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class LocalClientReader implements Runnable {

    private Socket socket;
    private BufferedReader reader;
    private VOIPClient client;

    public LocalClientReader(Socket socket, VOIPClient client) {
        this.socket = socket;
        this.client = client;
    }


    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line;
//            AudioInputStream is = AudioSystem.getAudioInputStream(new BufferedInputStream(socket.getInputStream()));
//            Clip clip = AudioSystem.getClip();
//            clip.open(is);
//            clip.start();

            //TODO strip packet header from packet, turn the rest of the line into a byte[] and play it via Clip.open()

            while((line = reader.readLine()) != null) {
                client.writeToConsole("PACKET: " + line);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
