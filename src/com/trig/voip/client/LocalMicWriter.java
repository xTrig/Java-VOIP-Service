package com.trig.voip.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

public class LocalMicWriter extends Thread {

    private VOIPClient client;
    private volatile boolean recording = false;
    private TargetDataLine mic;

    public LocalMicWriter(VOIPClient client) {
        this.client = client;
    }

    public void dispose() {
        recording = false;
        mic.drain();
        mic.close();
        stop();
        client = null;
    }

    public void stopRecording() {
        recording = false;
    }

    public void run() {
        client.writeToConsole("Starting mic test");
        recording = true;
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        try {
            mic = AudioSystem.getTargetDataLine(format);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            mic = (TargetDataLine) AudioSystem.getLine(info);
            mic.open(format);

            int numBytesRead;
            int CHUNK_SIZE = 1024;
            byte[] data = new byte[mic.getBufferSize() / 5]; //No clue why we divide this by 5
            mic.start();

            int bytesRead = 0;


            while(recording) {
                numBytesRead = mic.read(data, 0, CHUNK_SIZE);
                bytesRead += numBytesRead;
                client.sendVoice(data, numBytesRead);
                client.writeToConsole(new String(data));
            }
            mic.close();
            client.writeToConsole("Mic recording stopped");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
