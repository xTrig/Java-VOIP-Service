package commands;

import server.Client;

/***
 * @author Steven
 * Abstract class to define commands
 */
public abstract class AbstractCommand implements Command {

    protected Client client; //The client that the command will operate on
    protected String data; //The data passed along with the packet

    /***
     *
     * @param client The client that this command will operate on
     * @param data The data passed along with the packet
     */
    public AbstractCommand(Client client, String data) {
        this.client = client;
        this.data = data;
    }

    public Client getClient() {
        return client;
    }

    public String getData() {
        return data;
    }

    /***
     * Commands can execute this method to self check the data before execution
     * @return true if the packet self validation was successful
     */
    protected abstract boolean validate();
}
