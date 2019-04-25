package com.trig.voip.server.commands;

import com.trig.voip.server.Client;

public class VoiceCommand extends AbstractCommand {

    public VoiceCommand(Client client, String data) {
        super(client, data);
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    public void interpret() {
        data = data.replaceAll("\n", ""); //Strip the newline
        System.out.println("Voice Packet from " + client + ": " + data);
    }
}
