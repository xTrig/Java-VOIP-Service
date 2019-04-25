package com.trig.voip.server.commands;

import com.trig.voip.server.Client;

/***
 * @author Steven
 * This class will take the command prefix from the packet and construct a command from it
 */
public class CommandResolver {

    /***
     * Resolves the given packet data to a command object
     * @param client The client that sent this data
     * @param data The data that the client sent
     * @return The Command object based on the command prefix
     */
    public static AbstractCommand resolve(Client client, String data) {
        if(data.length() < 3) { //If the data is < 3 characters, nothing can be used, we might as well dispose of it.
            return null;
        }
        String cmdPrefix = data.substring(0, 2); //Strips the first 2 characters from the packet to get the prefix
        System.out.println("Command prefix: " + cmdPrefix);
        data = data.substring(3, data.length()); //Data = Data minus the command prefix
        System.out.println("Command data: " + data);

        switch(cmdPrefix) { //Construct command objects
            case "01":
                return new HelloCommand(client, data);
            case "02":
                return new SoundCommand(client, data);
            case "05":
                return new VoiceCommand(client, data);
            default:
                return new InvalidCommand(client, cmdPrefix + " " + data);
        }

    }
}
