package it.polimi.ingsw.cg11.communication.client;

import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;

import java.io.IOException;
import java.util.Observable;
import java.util.UUID;

/**
 * The abstract class for the general client, provides a unified interface for both type of communication, its main responsibility is to receive updates 
 * from a model and send commands to the controller. Of course the actual view
 * is going to be the CLI or GUI which are completely interchangeable and can handle the task of receiving commands and showing result
 * however they want. 
 * @author GerlandoSavio
 *
 */
public abstract class Client extends Observable {
    /**
     * the unique id of client is a common attribute for the client type
     */
    protected UUID clientId; 
    /**
     * the address of the host
     */
    protected String ip;
    /**
     * the port of the socket
     */
    protected int port;

    /**
     * Constructor for the client
     * @param ip address of the host
     * @param port of the socket
     */
    public Client(String ip, int port){
        this.port=port;
        this.ip=ip;
    }

    /**
     * connect to the server after providing a nickname
     * @param username
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public abstract void connect(String username) throws ClassNotFoundException, IOException;

    /**
     * asks the server to create a new game
     * @param numberOfPlayers
     * @param mapName
     * @return boolean representing the outcome
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public abstract boolean createGame(String numberOfPlayers, String mapName)  throws IOException, ClassNotFoundException;

    /**
     * asks the server to join a game 
     * @param gameNumber as it appears in the list of hosted games
     * @return boolean representing the outcome
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public abstract boolean joinGame(String gameNumber) throws IOException, ClassNotFoundException;

    /**
     * In-game command: asks the server for informations about the game
     * @param queryType an identifier of the query, to see all the queries we can handle look into the Controller class
     * @return ResponseMessage boolean + string
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public abstract ResponseMessage query(String queryType) throws ClassNotFoundException, IOException;

    /**
     * sends a string to the server which is going to be shown to all your play-mates.
     * @param message
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public abstract void chat(String message) throws ClassNotFoundException, IOException;

    /**
     * 
     * @param actionName name of the action
     * @param parameters the optional string with the coordinate or an item
     * @return boolean representing the action
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public abstract boolean action(String actionName, String parameters) throws ClassNotFoundException, IOException;

    /**
     * Getter for the unique id of the client
     * @return the id of the client
     */
    public UUID getClientID(){
        return clientId;
    }

    /**
     * When a model sends an update the client notifies its observers (the CLI and GUI) that a new message has come. 
     * The programmer can then decide how to handle the showing of these messages
     * @param message
     */
    public void event(EventMessage message){
        this.setChanged();
        this.notifyObservers(message);
    }


}
