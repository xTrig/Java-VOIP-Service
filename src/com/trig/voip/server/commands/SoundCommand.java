package com.trig.voip.server.commands;

import com.trig.voip.Main;
import com.trig.voip.server.Client;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SoundCommand extends AbstractCommand {

    public SoundCommand(Client client, String data) {
        super(client, data);
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    public void interpret() {
        final byte[] audio;
        try {
            audio = Files.readAllBytes(Paths.get(Main.class.getClassLoader().getResource("sound.wav").toURI()));
            client.sendRaw(audio, audio.length);
            System.out.println("Sending audio file: " + new String(audio));
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
