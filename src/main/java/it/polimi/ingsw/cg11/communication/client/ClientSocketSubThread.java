package it.polimi.ingsw.cg11.communication.client;

import it.polimi.ingsw.cg11.communication.commons.EventMessage;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The thread that receives messages from the server upon an event (changes in the model, game has started etc.)
 * @author GerlandoSavio
 *
 */
public class ClientSocketSubThread extends Thread {

    /**
     * general logger for the client
     */
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.client");
    /**
     * the canal for this communication
     */
    private Socket subscriberSocket;
    
    /**
     * the port of the socket
     */
    private int port;
    
    /**
     * the address of the broker
     */
    private String address;
    
    /**
     * The communication is one directional from the server to this
     */
    private ObjectInputStream in;
    
    /**
     * boolean representing the state of the 
     */
    private boolean isRunning;
    
    /**
     * Reference to the client running this thread
     */
    private ClientSocket client;
    
    /**
     * Way to keep the number of errors in check and get out of an infinite loop of exception when the server goes down
     */
    private int numberOfErrors;

    /**
     * Constructor for the thread
     * @param address the address of the host
     * @param port the port of the socket
     * @param client the client running this thread
     * @throws IOException
     */
    public ClientSocketSubThread(String address, int port, ClientSocket client) throws IOException{
        this.address = address;
        this.port = port;
        this.isRunning = false;
        this.client = client;
        numberOfErrors=0;
    }

    /**
     * Initialize the communication, then keep waiting for updates
     */
    @Override
    public void run(){

        while(!isRunning){
            try {
                subscriberSocket = new Socket(address,port);
                in = new ObjectInputStream(subscriberSocket.getInputStream());
                isRunning = true;
            } catch (IOException e1) {
                LOGGER.log(Level.WARNING, "error running", e1);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    LOGGER.log(Level.WARNING, "interrupted", e);
                }
            }
        }


        while(isRunning){
            try{
                receive();
                Thread.sleep(50);
            } catch (InterruptedException | IOException | ClassNotFoundException e){
                //if we can't receive for a few times we stop the thread
                numberOfErrors++;
                LOGGER.log(Level.WARNING, "Error while receiving", e);
                if(numberOfErrors>3)
                    isRunning = false;
            }
        }

        close();

    }

    /**
     * We try to read an object from the socket if we received something we tell the client
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void receive() throws IOException, ClassNotFoundException{
        EventMessage message = null;

        message= (EventMessage) in.readObject();
        if(message!=null){
            client.event(message);
        }
    }


    /**
     * Closes the socket
     * @throws IOException
     */
    private void close() {
        try {
        in.close();
        subscriberSocket.close();
        }catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing", e);
        } finally {
            in = null;
            subscriberSocket = null;
        }
    }


}
