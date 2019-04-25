package com.trig.voip.server;

import com.trig.voip.server.commands.AbstractCommand;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/***
 * @author Steven
 *
 * Main VOIPServer class to handle all connections and commands
 */
public class VOIPServer {

    private int port; //The port number the server will run on
    private ServerSocket server; //The ServerSocket to accept connections with
    private ServerSocket voiceServer; //The ServerSocket to handle voice communications
    private boolean running = false; //Whether or not the server is currently running
    private static VOIPServer instance; //The Singleton instance

    private ArrayList<Client> clients = new ArrayList<Client>(); //List of all currently connected clients

    private VOIPServer() {
    }

    /***
     * Starts the VOIP Server with the specified port
     * @param port The port number to start the server on
     */
    public void start(int port) {
        if(running) { //If the server is already running, throw an exception
            throw new RuntimeException("Server is already running!");
        }
        this.port = port; //Store the port for later use
        System.out.println("Server is starting on port: " + port);

        //Start creating the server socket
        try {
            server = new ServerSocket(port);
            voiceServer = new ServerSocket(port + 1); //Add 1 to the original port number.
            System.out.println("Listening on port: " + port);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        running = true; //Set the running flag to true
        //We will accept incoming connections on the main thread
        Thread mainServerThread = new Thread() {
            public void run() {
                while(running) {
                    try {
                        Socket socket = server.accept(); //Accept all connections
                        Client client = new Client(socket); //Create a Client with the socket and default name
                        System.out.println("Connection started with " + client.getConnectionInfo());
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }
        };

        Thread voiceServerThread = new Thread() {
            public void run() {
                while(running) {
                    try {
                        Socket socket = voiceServer.accept(); //Accept all connections
                        Client client = new Client(socket); //Create a Client with the socket and default name
                        System.out.println("Connection started with " + client.getConnectionInfo());
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }
                }
            }
        };

        mainServerThread.start();
        voiceServerThread.start();
    }

    /***
     * Interprets a command from a socket
     * @param cmd
     */
    public void acceptCommand(AbstractCommand cmd) {
        System.out.println("Interpreting command " + cmd.getClass().getName());
        cmd.interpret();
    }

    public Client getByPort(int port) {
        for(Client c : clients) {
            if(c.getSocket().getLocalPort() == port) {
                return c;
            }
        }
        return null;
    }

    public void attach(Client client, Client mic) {
        clients.remove(mic); //Remove mic from the list of clients
        client.attachMic(mic);
        System.out.println("Attached mic socket " + mic + " to " + client);
    }

    /***
     * Destroys the given client and frees up resources
     * @param client The client to destroy
     */
    public void dispose(Client client) {
        System.out.println("Closing connection with " + client);
        client.dispose(); //Call dispose on the client so it can handle its own garbage
        clients.remove(client); //Remove the client from the list
        client = null; //Help with GC
        System.out.println("Connection disposed");
    }

    /***
     * Sends voice data received from some client to all connected users
     * @param client The client that sent this data
     * @param voiceData The data that was received
     * @param length The length of the voice data received
     */
    public void sendVoice(Client client, byte[] voiceData, int length) {
        for(Client c : clients) {
            //For debugging purposes, we'll send the voice data to the original client as well.
            //We may need the length variable later.
            c.sendVoice(voiceData);
        }
    }

    /***
     * Returns the instance of VOIPServer, or creates one if none exists
     * @return An instance of VOIPServer
     */
    public static VOIPServer getInstance() {
        if(instance == null) {
            instance = new VOIPServer();
        }
        return instance;
    }
}
