package it.polimi.ingsw.cg11.gui;

import it.polimi.ingsw.cg11.communication.client.Client;
import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;
import it.polimi.ingsw.cg11.communication.commons.EventMessage.EventType;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Mediator class for the GUI. After a new Client object is created we can then use it to send commands and requests to the server
 * though this singleton class from arcoss the GUI Panels.
 * @author GerlandoSavio
 *
 */
public class ClientMediator implements Observer {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");
    private static ClientMediator instance = new ClientMediator();
    
    private Client client;
    private GamePanel gamePanel;
    private UUID clientId;
    private String playerType;

    private ClientMediator(){
        this.client = null;
    }
    
    /**
     * @return the shared instance of the ClientMediator
     */
    public static ClientMediator getInstance(){
        return instance;
    }
    
    /**
     * Sends the request to join a game to the server through the client object
     * @param gameNumber the number of the game to join as it appears on the list
     */
    public void joinGame(String gameNumber){
        try {
            client.joinGame(gameNumber);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.log(Level.WARNING, "Join Game Error", e);
            JOptionPane.showMessageDialog(gamePanel.getParent(), "There was an error while trying to join this game.");
        }

    }

    /**
     * Sends the request to create a game to the server through the client object
     * @param numberOfPlayers the number of players we wish to have in this game
     * @param mapName
     */
    public void createGame(String numberOfPlayers, String mapName){
        try {
            client.createGame(numberOfPlayers, mapName);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.log(Level.WARNING, "Create Game Error", e);
            JOptionPane.showMessageDialog(gamePanel.getParent(), "Error creating the game");
        }

    }

    /**
     * Sends the request to perform an action to the server through the client object
     * @param actionName the name of the action
     * @return boolean representing the outcome
     */
    public synchronized boolean action(String actionName){
        String parameters = null;

        if("Move".equals(actionName) || "Noise".equals(actionName) || "Spotlight".equals(actionName)){
            parameters = gamePanel.getSelectedCoordinate();
        }

        if("UseItem".equals(actionName) || "DiscardItem".equals(actionName)){
            try{

                parameters = gamePanel.getSelectedItemName().toUpperCase();
            } catch (NullPointerException e){
                LOGGER.log(Level.WARNING, "Item not selected", e);
                JOptionPane.showMessageDialog(gamePanel.getParent(), "You need to click on the image of an Item first");
            }
        }


        try {
            client.action(actionName, parameters);
            gamePanel.refresh();
            return true;
        } catch (ClassNotFoundException | IOException e) {

            LOGGER.log(Level.WARNING, "Error sending the action", e);
            JOptionPane.showMessageDialog(gamePanel.getParent(), "Something went wrong with this aciton. Try again later");
        }


        return false;
    }
    
    /**
     * Sends a query to the server through the client object
     * @param queryName the name of the query
     * @return the response message as computed from the server
     */
    public synchronized ResponseMessage query(String queryName){

        try {
            return client.query(queryName);
        } catch (ClassNotFoundException | IOException e) {
            LOGGER.log(Level.WARNING, "Query error", e);
            JOptionPane.showMessageDialog(gamePanel.getParent(), "Cannot compute query!");
        }

        return null;

    }
    
    /**
     * Sends a message to be shared with all the players 
     * @param chatMessage
     */
    public synchronized void chat(String chatMessage){

        try {
            client.chat(chatMessage);
        } catch (ClassNotFoundException | IOException e) {

            LOGGER.log(Level.WARNING, "Chat error", e);
            JOptionPane.showMessageDialog(gamePanel.getParent(), "There was an error sending your message");
        }
    }
    
    /**
     * @param client the client to set
     */
    public void setClient(Client client){
        this.client=client;
        this.clientId=client.getClientID();
        this.client.addObserver(this);
    }
    
    /**
     * @param panel the main game panel of the gui
     */
    public void setGamePanel(GamePanel panel){
        this.gamePanel = panel;
    }
    
    /**
     * Observes the client and when a new update is received it handles appropriately
     */
    @Override
    public void update(Observable o, Object arg) {

        final EventMessage eventMessage = (EventMessage) arg;

        if(eventMessage.getType() == EventType.GAME_BEGUN){
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run(){
                    GUI.worker.setGameIsRunning();
                }
            });

        }
        
        if(eventMessage.getType() == EventType.GAME_OVER){
            JOptionPane.showMessageDialog(gamePanel.getParent(), "GAME OVER \n" + eventMessage.getMessage() + " Thank you for playing! The game is going to close now");
            GUI.close();
        }
        
        if(eventMessage.getType() == EventType.GAME_TIMEOUT){
            SwingUtilities.invokeLater(new Runnable(){
                @Override
                public void run(){
                    GUI.worker.setGameTimeout();
                }
            });

        }

        if(eventMessage.getType() == EventType.GAME_EVENT){
            String message = eventMessage.getMessage();
            String coordinate;
            if(message.contains("Noise")){
                coordinate = message.substring(message.lastIndexOf(" ")+1);
                gamePanel.getSpaceship().makeNoise(coordinate);
            }
            if(message.contains("attacked")){
                coordinate = message.substring(message.lastIndexOf(" ")+1);
                gamePanel.getSpaceship().makeAttack(coordinate);
            }
        }

        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run(){
                while(gamePanel == null)
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        LOGGER.log(Level.WARNING, "Interrupted exception", e);
                    }
                gamePanel.manageEvent(eventMessage);
            }
        });
    }

    /**
     * @return the string of the client id
     */
    public String getClientID() {

        return this.clientId.toString();
    }
    /**
     * @return the string with the player type ALIEN OR HUMAN
     */
    public String getPlayerType(){
        return playerType;
    }
    /**
     * @param playerType the playerType to set
     */
    public void setPlayerType(String playerType){
        this.playerType = playerType;
    }
}
