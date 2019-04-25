package com.trig.voip.server;

import com.trig.voip.server.commands.AbstractCommand;
import com.trig.voip.server.commands.CommandResolver;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket; //The socket that this Client belongs to
    private Socket micSocket; //The socket dedicated to transferring voice data
    private boolean isMicSocket = false;
    private String name; //The name of this Client
    private ClientHandler handler; //Class to handle Client Input
    private ClientSender sender; //Class to handle Client output

    //private MicReader micReader; //Class that will handle mic input from the client
    //private MicWriter micWriter; //Class that will handle voice output to the client
    private Client mic;
    private VOIPServer server = VOIPServer.getInstance(); //The server instance

    /***
     * Creates a Client
     * @param socket The socket that this client connected with
     * @param name The name for this client
     */
    public Client(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
        handler = new ClientHandler();
        handler.start();
        sender = new ClientSender();
        sender.init();

    }

    /***
     * Creates a Client. Calls Client(Socket, Name) with a default name.
     * @param socket The socket that this client connected with
     */
    public Client(Socket socket) {

        this(socket, "Unknown");
    }

    private void setMicSocket(boolean isMicSocket) {
        this.isMicSocket = isMicSocket;
    }

    /***
     * Attaches a voice communication socket to this client
     * @param mic The Client to be used as the voice communication line
     */
    public void attachMic(Client mic) {
        this.mic = mic;
        this.mic.setMicSocket(true);
    }

    public void sendVoice(byte[] data) {
        if(mic != null) {
            mic.sendRaw(data);
        } else {
            throw new NullPointerException("Client at " + this + " does not have a mic socket attached!");
        }

    }

    /***
     * Sends a packet to the client
     * @param data The data to be sent
     */
    public void sendMessage(String data) {

        sender.sendMessage(data);
    }

    /***
     * Sends a raw byte[] to the client
     * @param data The data to be sent
     */
    public void sendRaw(byte[] data) {
        sender.sendRaw(data);
    }

    /***
     *
     * @return The socket that this client handles
     */
    public Socket getSocket() {
        return socket;
    }

    /***
     *
     * @return The name of this Client, or "Unknown" if not set with the HelloCommand
     */
    public String getName() {

        return name;
    }

    /***
     *
     * @param name Sets the name for this client
     */
    public void setName(String name) {

        this.name = name;
    }

    /***
     *
     * @return This Client's IP address : PORT
     */
    public String getConnectionInfo() {

        return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
    }

    /***
     *
     * @return This client's name, along with getConnectionInfo()
     */
    public String toString() {

        return name + "@" + getConnectionInfo();
    }

    /***
     * Prepares this client to be deleted
     */
    public void dispose() {
        handler.stop();
        sender.stop();
        if(!socket.isClosed()) {
            try {
                socket.close();
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    /***
     * @author Steven
     * Class to handle the client's InputStream
     */
    private class ClientHandler extends Thread {

        private BufferedReader reader; //The reader to read the inputstream


        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream())); //Setup the reader

            } catch (Exception exc) {
                exc.printStackTrace();
            }
            if(reader == null) { //If the reader failed to setup, end this thread
                terminate();
            }

            String line; //The line that will be received from transport layer

            try {

                while((line = reader.readLine()) != null) { //While the socket is still valid

                    if(!isMicSocket) {
                        System.out.println("Received data: " + line);
                        AbstractCommand cmd = CommandResolver.resolve(Client.this, line); //Resolve this command
                        if(cmd == null) {
                            continue;
                        }
                        server.acceptCommand(cmd); //Execute the command
                    } else {
                        System.out.println("Received voice data: " + line);
                        byte[] voiceData = line.getBytes();
                        server.sendVoice(Client.this, voiceData, voiceData.length);
                    }

                }

                server.dispose(Client.this); //The socket connection has ended, dispose of this object
            } catch (Exception exc) {
                exc.printStackTrace();
            }

        }

        /***
         * Ends this thread
         */
        private void terminate() {
            try {
                this.join(500); //Wait 500ms for this thread to end
            } catch (InterruptedException exc) {
                exc.printStackTrace();
            }
        }
    }

    /***
     * Class to send packets to the client socket
     */
    private class ClientSender extends Thread {
        private BufferedOutputStream writer; //The writer to send packets with
        private byte[] data; //The data to send


        /***
         * Must be called before packets can be sent to the socket
         */
        private void init() {
            try {
                writer = new BufferedOutputStream(socket.getOutputStream());
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }

        /***
         * Sends a message to the socket
         * @param data The data to be sent to the socket
         */
        private void sendMessage(String data) {

            sendRaw((data + "\n").getBytes());
        }

        private void sendRaw(byte[] data) {
            if(writer == null) {
                throw new RuntimeException("ClientSender not initialized");
            }
            this.data = data;
            run();
        }

        @Override
        public void run() {
            try {
                //System.out.println("Sending data: " + new String(data));
                writer.write(data);
                writer.flush();

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

//    private class MicReader extends Thread {
//
//        private DataInputStream in;
//        private byte[] micBuffer = new byte[2048];
//
//        private void init() {
//            if(micSocket != null) {
//                if(!micSocket.isClosed()) {
//                    try {
//                        in = new DataInputStream(micSocket.getInputStream());
//                    } catch (Exception exc) {
//                        exc.printStackTrace();
//                        System.out.println("Could not setup mic reader for client " + Client.this.toString());
//                    }
//                }
//            }
//        }
//
//        public void run() {
//
//            try {
//                int length = 0;
//                while((length = in.read(micBuffer)) != -1) {
//                    server.sendVoice(Client.this, micBuffer, length);
//                }
//
//                micSocket.close(); //The connection has been closed, so let's make sure the socket is closed and then set it to null
//                micSocket = null;
//            } catch (Exception exc) {
//                exc.printStackTrace();
//                System.out.println("Mic Buffer failed to read");
//            }
//        }
//    }
//
//    private class MicWriter extends Thread {
//
//        private DataOutputStream out;
//        private byte[] data;
//        private int length;
//
//        private void init() {
//
//            if(micSocket != null) {
//                if(!micSocket.isClosed()) {
//                    try {
//                        out = new DataOutputStream(micSocket.getOutputStream());
//                    } catch (Exception exc) {
//                        exc.printStackTrace();
//                        System.out.println("Failed to create mic output stream for " + Client.this.toString());
//                    }
//
//                }
//            }
//        }
//
//        private void sendData(byte[] data, int length) {
//            this.data = data;
//            this.length = length;
//        }
//
//        public void run() {
//            if(out != null) {
//                try {
//                    out.write(data, 0, length);
//                } catch (Exception exc) {
//                    exc.printStackTrace();
//                }
//            }
//        }
//    }
}
