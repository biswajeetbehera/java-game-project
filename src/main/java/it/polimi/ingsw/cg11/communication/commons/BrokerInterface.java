package it.polimi.ingsw.cg11.communication.commons;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;
/**
 * Simple broker interface we export to provide a way for the client to subscribe to a game and receive its updates
 * @author GerlandoSavio
 *
 */
public interface BrokerInterface extends Remote {
    /**
     * Subscribes to a game
     * @param subscriber the interface on which the broker calls the dispatch message method
     * @param clientId the unique id of the client to find in which game it belongs
     * @throws RemoteException
     */
    public void subscribe(SubscriberInterface subscriber, UUID clientId) throws RemoteException;
}
