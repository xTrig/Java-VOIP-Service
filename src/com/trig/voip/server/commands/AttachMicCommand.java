package com.trig.voip.server.commands;

import com.trig.voip.server.Client;
import com.trig.voip.server.VOIPServer;

public class AttachMicCommand extends AbstractCommand {

    public AttachMicCommand(Client client, String data) {
        super(client, data);
    }

    @Override
    protected boolean validate() {
        int port = Integer.parseInt(data); //The data field for this packet will contain the port number of its control socket for verification
        Client c = VOIPServer.getInstance().getByPort(port);

        if(c == null) { //No Client was found with the specified port
            return false;
        }
        if(c.getSocket().getInetAddress().equals(client.getSocket().getInetAddress())) { //Verify that the InetAddresses are equal
            return true;
        }
        return false;
    }

    @Override
    public void interpret() {
        if(validate()) {
            Client c = VOIPServer.getInstance().getByPort(Integer.parseInt(data));
            VOIPServer.getInstance().attach(c, client);
        } else {
            System.out.println("Received invalid AttachMicCommand : Sender=" + client);
        }
    }
}
