package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.GamesManagerRemote;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The RMI side of the server, it publishes on the registry the remote interface for the rmi client to use.
 * @author GerlandoSavio
 *
 */
public class RMIStarter implements Runnable {
    /**
     * logger of the server
     */
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");
    /**
     * reference to the instance of the game manager we need to publish on the registry
     */
    private final GamesManager gameManager;
    /**
     * The registry on which we bind the gameManager interface
     */
    private final Registry registry;


    /**
     * Constructor for the thread of the rmi server
     * @param port the port on which we create the registry
     * @throws RemoteException
     */
    public RMIStarter(int port) throws RemoteException{

        this.registry = LocateRegistry.createRegistry(port);
        this.gameManager = GamesManager.getInstance();
    }

    /**
     * Binds the game manager remote to the registry
     */
    @Override
    public void run() {

        try {
            
            GamesManagerRemote gameRemote = (GamesManagerRemote) UnicastRemoteObject.exportObject(gameManager, 0);
            registry.bind("GAMEMANAGER", gameRemote);

        } catch (RemoteException | AlreadyBoundException e1) {
            LOGGER.log(Level.WARNING, "Error binding the game remote interface", e1);
        }




    }

}
