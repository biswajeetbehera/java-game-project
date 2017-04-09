package it.polimi.ingsw.cg11.communication.commons;

import java.io.IOException;
import java.rmi.Remote;
/**
 * The interface with the method needed from the broker to send a message to the subscriber (client)
 * @author GerlandoSavio
 *
 */
public interface SubscriberInterface extends Remote {
    /**
     * The method used to dispatch an event message. 
     * @param message
     * @throws IOException
     */
    public void dispatchMessage(EventMessage message) throws IOException;
}
