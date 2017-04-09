package it.polimi.ingsw.cg11.communication.commons;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;
/**
 * The interface on which the client calls the remote methods on the GamesManager
 * @author GerlandoSavio
 *
 */
public interface GamesManagerRemote extends Remote, Serializable {
    
    /**
     * Adds a new user to the server and returns its UUID to him or her
     * @param username the nickname chosen from the user
     * @return UUID which identifies the client
     * @throws RemoteException
     */
    public UUID addUser(String username) throws RemoteException;
    
    /**
     * Returns a list of the games currently hosted by the server and other details 
     * such as the number of players of the game, the name of the spaceship and the number of pending 
     * players (how many others need to join)
     * @return list of strings, one per game hosted
     * @throws RemoteException
     */
    public List<String> getGamesInformation() throws RemoteException;
    
    /**
     * Recognizes the client sending the action, finds the game it belongs to and sends the name and the parameters to the controller
     * @param clientId the unique id of the client
     * @param actionName the name of the action 
     * @param actionParameter the parameters some actions need to be performed
     * @return outcome boolean
     * @throws RemoteException
     */
    public boolean action(UUID clientId, String actionName, String actionParameter) throws RemoteException;
    
    /**
     * Recognizes the client sending the query, finds the game it belongs to and delivers it to the controller
     * @param clientId the unique id of the client
     * @param queryType the name of the query
     * @return a response message (boolean + optional string)
     * @throws RemoteException
     */
    public ResponseMessage query(UUID clientId, String queryType) throws RemoteException;
    
    /**
     * Recognizes the client sending the query, finds the game it belongs to and delivers it to the controller
     * @param clientId
     * @param chatMessage
     * @throws RemoteException
     */
    public void chat(UUID clientId, String chatMessage) throws RemoteException;
    
    /**
     * Method to create a game 
     * @param numberOfPlayers the number of players 
     * @param mapName the name of the map in which the game is going to take place
     * @param clientId the id of the client requesting to create the game
     * @param communicationType the type of communication used by the client (RMI or Sockets)
     * @param subscriber the interface which provides the dispatch message method
     * @return boolean representing the outcome
     * @throws IOException
     */
    boolean createGame(int numberOfPlayers, String mapName, UUID clientId,
            CommunicationType communicationType, SubscriberInterface subscriber)
                    throws IOException;
    /**
     * Method to join a game
     * @param clientId the unique id of the client requesting to join a game
     * @param gameNumber the number of the game to join as it appears in the list of games informations
     * @param communicationType the type of communication used by the client (RMI or Sockets)
     * @param subscriber the interface which provides the dispatch message method
     * @return boolean representing the outcome
     * @throws IOException
     */
    boolean joinGame(UUID clientId, int gameNumber,
            CommunicationType communicationType, SubscriberInterface subscriber)
                    throws IOException;




}
