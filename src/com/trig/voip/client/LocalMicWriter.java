package com.trig.voip.client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class LocalMicWriter extends Thread {

    private VOIPClient client;

    public LocalMicWriter(VOIPClient client) {
        this.client = client;
    }

    public void dispose() {
        stop();
        client = null;
    }

    public void run() {
        client.writeToConsole("Starting mic test");
        AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
        TargetDataLine mic;
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

            while(bytesRead < 20000) {
                numBytesRead = mic.read(data, 0, CHUNK_SIZE);
                bytesRead += numBytesRead;
                client.sendVoice(data, numBytesRead);
                client.writeToConsole(new String(data));
            }
            mic.drain();
            mic.close();
            client.writeToConsole("Mic test complete");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
