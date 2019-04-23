package commands;

import server.Client;

/***
 * @author Steven
 * First command expected to be executed by the client upon connection. This command will assign a name to the Client.
 */
public class HelloCommand extends AbstractCommand {

    public HelloCommand(Client client, String name) {
        super(client, name);
    }

    /***
     * Validates the data, and then sets the specified name to the user
     */
    public void interpret() {
        if(validate()) {
            client.setName(data); //Set the name of this client to the name they sent
            System.out.println("Set " + client.getConnectionInfo() + " name to " + data);
        } else { //The data for this command was invalid
            client.sendMessage("Invalid HelloCommand : Name must be <= 16 characters in length");
        }
    }

    protected boolean validate() {
        data = data.replaceAll("\n", ""); //Strip the CR if it the data contains one.
        return data.length() <= 16;
    }


}
