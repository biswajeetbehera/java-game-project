package it.polimi.ingsw.cg11.communication.client;

import it.polimi.ingsw.cg11.communication.commons.CommunicationType;
import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.GamesManagerRemote;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;
import it.polimi.ingsw.cg11.communication.commons.SubscriberInterface;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The methods for the RMI communication
 * @author GerlandoSavio
 *
 */
public class ClientRMI extends Client implements SubscriberInterface, Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * general logger for the client
     */
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.client");

    /**
     * the stub of the game manager on which we call the methods
     */
    private GamesManagerRemote gameManager;
    /**
     * the registry on which we look for the game manager and broker stubs
     */
    private transient Registry registry;

    /**
     * Constructor for the RMI Client
     * @param ip address of the host
     * @param port on which we locate the registry
     */
    public ClientRMI(String ip, int port){
        super(ip,port);
        getInterface();
    }

    /**
     * looks into the registry to find the game manager stub
     */
    public void getInterface() {
        try {
            registry = LocateRegistry.getRegistry(ip, port);
            GamesManagerRemote gameManagerStub = (GamesManagerRemote) registry.lookup("GAMEMANAGER");
            this.gameManager = gameManagerStub;

        } catch (RemoteException | NotBoundException e) {
            LOGGER.log(Level.WARNING, "Error while getting the game manager stub", e);
        }
    }

    /**
     * {@inheritDoc}
     * In RMI this calls the addUser method on the remote GamesManager
     */
    @Override
    public void connect(String username) throws RemoteException {
        this.clientId = gameManager.addUser(username);
    }

    /**
     * {@inheritDoc}
     * In RMI this calls the createGame method on the remote GamesManager, providing all the informations necessary to carry out the operation
     */
    @Override
    public boolean createGame(String numberOfPlayers, String mapName) throws IOException {
        return gameManager.createGame(Integer.valueOf(numberOfPlayers), mapName, clientId, 
                CommunicationType.RMI, (SubscriberInterface)UnicastRemoteObject.exportObject(this, 0));

    }
    /**
     * {@inheritDoc}
     * IN RMI this calls the joinGame method on the remote GamesManager, providing all the informations necessary to carry out the operation
     */
    @Override
    public boolean joinGame(String gameNumber) throws IOException {

        return gameManager.joinGame(clientId, Integer.valueOf(gameNumber), CommunicationType.RMI, 
                (SubscriberInterface)UnicastRemoteObject.exportObject(this, 0));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean action(String actionName, String parameters) throws RemoteException {
        return gameManager.action(clientId, actionName, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage query(String queryType) throws RemoteException {
        return gameManager.query(clientId, queryType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chat(String message) throws RemoteException {
        gameManager.chat(clientId, message);
    }
    
    /**
     * The method called from the Broker to dispatch an EventMessage, which then calls the method to notify the observers
     */
    @Override
    public void dispatchMessage(EventMessage message) throws RemoteException {
        this.event(message);
    }


}
