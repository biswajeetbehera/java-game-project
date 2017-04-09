package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.Communicator;
import it.polimi.ingsw.cg11.communication.commons.RequestMessage;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the commands from the client and sends them to the server for manipulation
 * closes right after it's done
 */
public class ClientHandler implements Runnable  {

    private Communicator client;
    private MessageInterpreter interpreter;
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");

    /**
     * Constructor for the client handler
     * @param client the implementation of the interface on which we can call the receive and send methods
     * @throws IOException
     */
    public ClientHandler(Communicator client) throws IOException {
        this.client=client;
        this.interpreter = MessageInterpreter.getInstance();
    }

    /**
     * Receives a request from the client, asks the interpreter to computer a response, sends it to the client and closes the communicator
     */
    @Override
    public void run() {

        RequestMessage request;

        try {
            request = (RequestMessage) client.receive();

            ResponseMessage response = interpreter.compute(request);

            client.send(response);

            client.close();

        } catch (ClassNotFoundException | IOException e) {
            LOGGER.log(Level.SEVERE, "Could not send the request to the server", e);
            ResponseMessage response = new ResponseMessage(false, e.getMessage());
            client.send(response);
            client.close();

        } 
    }


}
