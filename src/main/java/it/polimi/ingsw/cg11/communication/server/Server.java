package it.polimi.ingsw.cg11.communication.server;


import java.io.IOException;
import java.rmi.RemoteException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The main class of the server, it creates two separate "subservers" one for RMI communication and one for sockets only communication
 * @author GerlandoSavio
 *
 */
public class Server {
    /**
     * the port for the socket communication
     */
    private int portSockets;
    /**
     * the port for rmi communication
     */
    private int portRMI;
    /**
     * the executor for the subservers
     */
    private Executor executor;

    /**
     * the logger of the server on which we monitor all activity and errors
     */
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");

    /**
     * The constructor for the server
     * @param portSockets 
     * @param portRMI
     * @throws IOException
     */
    public Server(int portSockets, int portRMI) throws IOException {
        this.portSockets=portSockets;
        this.portRMI=portRMI;

        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * method to start the server, sets the rmi server and sockets server for execution
     * @throws RemoteException
     */
    public void startServer() throws RemoteException {

        SocketServer socketComm = new SocketServer(portSockets);
        RMIStarter rmiComm = new RMIStarter(portRMI);

        executor.execute(socketComm);
        executor.execute(rmiComm);
    }


    /**
     * Main method to starts the server
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException { 
        //we star the sockets port at 1337 and the rmi port to 1099
        Server server = new Server(1337,1099);
        try{
            server.startServer();
        }catch(RemoteException s){
            LOGGER.log(Level.WARNING, "Error", s);
        }
    }

}