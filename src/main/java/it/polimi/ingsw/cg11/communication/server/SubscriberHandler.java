package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.SubscriberInterface;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Thread that is created from the broker to send event messages to the client communicating over Sockets
 * @author GerlandoSavio
 *
 */
public class SubscriberHandler implements Runnable, SubscriberInterface {
    private Socket brokerSocket;
    private ObjectOutputStream socketOut;
    private Queue<EventMessage> messagesBuffer;
    private boolean isRunning;
    private int numberOfErrors;
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");

    /**
     * Constructor for the subscriber handler 
     * @param socket the canal of the communication
     * @throws IOException
     */
    public SubscriberHandler(Socket socket) throws IOException{
        this.brokerSocket = socket;
        this.socketOut = new ObjectOutputStream(socket.getOutputStream());
        this.messagesBuffer = new ConcurrentLinkedQueue<EventMessage>();
        this.isRunning = true;
        this.numberOfErrors = 0;
    }

    /**
     * Waits for new message to be queued on the messages buffer and when there's a new one it's send to the subscriber
     * if we can't reach the subscriber for a couple of times we close the socket
     */
    @Override
    public void run(){
        while(isRunning){

            EventMessage message = messagesBuffer.poll();

            if(message!=null){
                try {
                    send(message);
                } catch (IOException e1) {
                    LOGGER.log(Level.WARNING, "Error trying to send message to subscriber", e1);
                    numberOfErrors++;
                }
            }

            else{
                try{
                    synchronized(messagesBuffer){
                        messagesBuffer.wait();
                    } 
                } catch (InterruptedException e){
                    LOGGER.log(Level.WARNING, "Error", e);
                }
            }

            if(numberOfErrors>1){
                LOGGER.log(Level.INFO, "Closing the subscriber handler causing the problems");
                isRunning = false;
            }
        }
        this.close();

    }

    /**
     * adds the message to the queue of messages to send
     */
    @Override
    public void dispatchMessage(EventMessage message) throws IOException{
        messagesBuffer.add(message);

        synchronized(messagesBuffer){
            messagesBuffer.notify();
        }
    }

    /**
     * writes on the socket the EventMessage we need to send
     * @param message
     * @throws IOException 
     */
    private void send(EventMessage message) throws IOException {
        socketOut.writeObject(message);
        socketOut.flush();
    }

    /**
     * closes the communication
     */
    private void close(){
        try {
            brokerSocket.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing the subscriber handler", e);
        } finally {
            brokerSocket = null;
            socketOut = null;
        }
    }


}
