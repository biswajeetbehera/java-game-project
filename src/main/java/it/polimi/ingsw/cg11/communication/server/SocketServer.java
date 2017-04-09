package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.SocketCommunicator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The thread of the server which handles the socket communication. Waits for connections of clients
 * @author GerlandoSavio
 *
 */
public class SocketServer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");
    private ServerSocket serverSocket;
    private final int port;
    private boolean running;
    private Executor executor;
    
    /**
     * Constructor for the SocketServer, we need to specify the port on which we want to create the socket
     * @param port
     */
    public SocketServer(int port) {
        this.running=true;
        this.port = port;
        this.executor = Executors.newCachedThreadPool();
    }

    /**
     * while the thread is running it waits for new connections from clients
     */
    @Override
    public void run() {

        //we start the socket
        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, "Exception creating the server socket", e1);
        }

        LOGGER.info("Server ready");

        //we wait for connections indefinitely
        while(running){
            try{

                Socket socket = serverSocket.accept(); //stops here until a connection is received 
                executor.execute(new ClientHandler(new SocketCommunicator(socket))); //creates a new thread to handle communication with that client

            }catch(IOException e){
                LOGGER.log(Level.SEVERE, "Error while accepting a new connection", e);
            }
        }

        //we close the socket
        try {
            serverSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing the server, no matter", e);
        }finally {
            serverSocket = null;
        }

    }

    /**
     * @param running the value to set
     */
    public void setRunning(boolean running){
        this.running = running;
    }


}
