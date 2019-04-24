package com.trig.voip;

import com.trig.voip.client.VOIPClient;
import com.trig.voip.server.VOIPServer;

public class Main {

    private static int port = 8000;
    private static boolean server = false;

    public static void main(String[] args) {
        //Check args to see if we want to run the program in server mode
        if(args.length > 0) {
            for(int i = 0; i < args.length; i++) {
                if(args[i].equalsIgnoreCase("-server")) { //The user wants to host a server
                    server = true;
                }
                if(args[i].equalsIgnoreCase("-port")) { //The port number for the server or client
                    port = Integer.parseInt(args[i + 1]); //the next parameter should be the port number
                }
            }
        }


        if(server) {
            VOIPServer voip = VOIPServer.getInstance(); //VOIPServer is a Singleton
            System.out.println("Starting server on port " + port);
            voip.start(port); //Start the server
            return;
        }

        //Otherwise start the client
        System.out.println("Client starting...");

        VOIPClient client = new VOIPClient();
        client.start();

    }
}
