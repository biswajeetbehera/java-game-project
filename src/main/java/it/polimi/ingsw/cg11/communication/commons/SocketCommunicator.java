package it.polimi.ingsw.cg11.communication.commons;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A communicator to exchange objects through the sockets
 * @author GerlandoSavio
 *
 */
public class SocketCommunicator implements Communicator {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Constructor for the socket communicator, creates a canal that goes both ways (client->server, server->client)
     * sets the timeout of the socket to 30 seconds (the server has to answer in 30s max)
     * @param s
     * @throws IOException
     */
    public SocketCommunicator(Socket s) throws IOException {
        socket=s;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        socket.setSoTimeout(30 * 1000);
    }

    /**
     * Sends an object
     */
    @Override
    public void send(Object msg) {

        try {
            out.writeObject(msg);
            out.flush(); 
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error", e);
        } 
    }

    /**
     * receives an object
     */
    @Override
    public Object receive() throws ClassNotFoundException, IOException{

        return in.readObject();
    }

    /**
     * closes the communication
     */
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error closing the socket, no matter", e);
        } finally {
            socket = null;
        }
    }

}
