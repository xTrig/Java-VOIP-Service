package com.trig.voip.client;

import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;

import javax.sound.sampled.*;
import java.io.*;
import java.net.Socket;

public class LocalClientReader extends Thread {

    private Socket socket;
    private DataInputStream reader;
    private VOIPClient client;
    private boolean isVoiceReader = false;
    private boolean running = false;

    public LocalClientReader(Socket socket, VOIPClient client, boolean isVoiceReader) {
        this.socket = socket;
        this.client = client;
        this.isVoiceReader = isVoiceReader;
    }


    @Override
    public void run() {
        try {
            running = true;
            reader = new DataInputStream(socket.getInputStream());
            byte[] buffer = new byte[8000];
            String line;

            if(isVoiceReader) { //We need to handle voice data separately
                AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);
                DataLine.Info speakerInfo = new DataLine.Info(SourceDataLine.class, format);
                SourceDataLine speaker = (SourceDataLine) AudioSystem.getLine(speakerInfo);
                speaker.open(format);
                buffer = new byte[speaker.getBufferSize() / 5];

                speaker.start();
                while(running) {
                   int count = reader.read(buffer);
                   if(count > 0) {
                       System.out.println("Playing " + new String(buffer, 0, count));
                       speaker.write(buffer, 0, count);
                   }
                }
                speaker.drain();
                speaker.close();
//                AudioInputStream is = AudioSystem.getAudioInputStream(new BufferedInputStream(socket.getInputStream()));
//                Clip clip = AudioSystem.getClip();
//                clip.open(is);
//                clip.start();
            } else { //This is regular data
                while((reader.read(buffer)) != -1) {
                    line = new String(buffer);
                    System.out.println("Packet: " + line);
                }
            }

        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
