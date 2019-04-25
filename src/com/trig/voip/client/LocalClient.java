package com.trig.voip.client;

import java.net.Socket;

public class LocalClient {

    private Socket socket;
    private LocalClient micClient;
    private LocalClientReader reader;
    private LocalClientWriter writer;
    private VOIPClient voip;
    private boolean isVoiceClient = false;
    private boolean initialized = false;

    public LocalClient(VOIPClient voip, Socket socket, boolean isVoiceClient) {
        this.voip = voip;
        this.socket = socket;
        this.isVoiceClient = isVoiceClient;
    }

    public void init() {
        if(!initialized) {
            reader = new LocalClientReader(socket, voip, isVoiceClient);
            writer = new LocalClientWriter(socket, voip);
            reader.start();
            initialized = true;
        } else {
            throw new RuntimeException("Client already initialized!");
        }
    }

    public LocalClient getMicClient() {
        return micClient;
    }

    /***
     * Attaches a LocalClient to this Client for voice communication
     * @param micClient
     */
    public boolean attachMic(LocalClient micClient) {
        if(micClient != null) {
            this.micClient = micClient;
            if(!this.micClient.isInitialized()) {
                this.micClient.init();
            }
            System.out.println("Attempting to attach mic to port " + socket.getLocalPort());
            this.micClient.sendMessage("03 " + socket.getLocalPort());
            return true;
        }
        return false;
    }

    public void sendVoice(byte[] data, int length) {
        if(isVoiceClient) {
            sendRaw(data, length);
        } else {
            System.out.println("Not voice client");
        }

    }

    public void sendMessage(String data) {
        writer.sendMessage(data);
    }

    public void sendRaw(byte[] data, int count) {
        writer.sendRaw(data, count);
    }

    public boolean isInitialized() {
        return initialized;
    }
}
