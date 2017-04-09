package it.polimi.ingsw.cg11.communication.commons;

import java.io.IOException;
/**
 * Interface for the exchanging serializable objects
 * @author GerlandoSavio
 *
 */
public interface Communicator {
    /**
     * sends the object
     * @param msg
     */
    void send(Object msg);

    /**
     * Receives an object
     * @return the Object received
     * @throws ClassNotFoundException
     * @throws IOException
     */
    Object receive() throws ClassNotFoundException, IOException;

    /**
     * closes the communicator
     */
    void close();

}

