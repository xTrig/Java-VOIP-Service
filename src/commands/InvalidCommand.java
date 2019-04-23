package commands;

import server.Client;

/***
 * @author Steven
 * Command that will be executed when invalid data arrives with the proper packet header
 */
public class InvalidCommand extends AbstractCommand {

    public InvalidCommand(Client client, String data) {
        super(client, data);
    }

    @Override
    protected boolean validate() {
        return true;
    }

    @Override
    public void interpret() {
        client.sendMessage("InvalidCommand : Received invalid command {" + data + "}");
    }
}
