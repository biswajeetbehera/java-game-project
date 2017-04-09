package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.CommunicationType;
import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.GamesManagerRemote;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;
import it.polimi.ingsw.cg11.communication.commons.SubscriberInterface;
import it.polimi.ingsw.cg11.communication.commons.EventMessage.EventType;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton. 
 * It's the entity of the server that knows what games are currently playing and offers a common interface for our communication system
 * to actually create, join, play a game (and more!) It is also responsible to sending an action to the controller of a single game
 *
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class GamesManager implements GamesManagerRemote {
    /**
     * 
     */
    private static final long serialVersionUID = 2196389129691289159L;
    /**
     * logger of the server
     */
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");
    private static GamesManager instance = new GamesManager();

    private transient List<Model> gamesHosted;
    private transient Map<UUID,Model> gamePerPlayer;
    private transient Map<UUID,String> users;
    private transient GamesTimer gamesTimer;
    private Broker broker;


    /**
     * Private constructor of the game broker, singleton.
     */
    private GamesManager(){
        this.gamesHosted = new ArrayList<Model>();
        this.gamePerPlayer = new HashMap<UUID,Model>();
        this.users = new HashMap<UUID,String>();
        this.broker = new Broker();
        this.gamesTimer = new GamesTimer();
    }   


    /**
     * @return the only instance of the GamesManager
     */
    public static synchronized GamesManager getInstance() {
        return instance;
    }

    /**
     * Adds a user to the 'server' with its nickname, and returns its UUID. It is used when initializing a new client.
     * @param username
     * @return clientID
     */
    @Override
    public synchronized UUID addUser(String username){

        UUID clientId = UUID.randomUUID();
        users.put(clientId, username);
        LOGGER.info("Adding the client with username: " + username + " to the server with UUID: " + clientId.toString());
        return clientId;
    }



    /**
     * Generates a list with the information regarding the games currently running on the server.
     * Those information include the number of players of the game, the number of clients that need to join the game before starting and
     * the map in which the game is taking place.
     * 
     * @return List<String> one string for each game
     */
    @Override
    public synchronized List<String> getGamesInformation() {
        List<String> gamesPending = new ArrayList<String>();

        String gameString;
        int gameNumber=1;

        for(Model m: gamesHosted){

            String mapName = m.getMap().getName();
            int numberOfPlayers = m.getPlayers().size();

            if(!(m.getPendingPlayers().isEmpty())){

                int playersPending = m.getPendingPlayers().size();
                gameString = String.format("Game no.%d, Map: %s, TotalNumberOfPlayers: %d, Waiting for %d players %n", gameNumber, mapName, numberOfPlayers, playersPending);
                gamesPending.add(gameString);
            }

            else{

                gameString = String.format("Game no.%d, Map: %s, TotalNumberOfPlayers: %d, GAME FULL %n", gameNumber, mapName, numberOfPlayers);
                gamesPending.add(gameString);
            }

            gameNumber++;
        }
        return gamesPending;
    }



    /**
     * Creates a new game to host and sets it in a state in which it's waiting for all the players to join.
     * The new game is added to the list of games currently hosted by the server and the first player of the game has its UUID assigned
     * 
     * @param numberOfPlayers
     * @param mapName
     * @param clientId the UUID of the client
     * @return a boolean which represents the outcome, false if the UUID is already assigned to a game.
     * @throws IOException 
     */
    @Override
    public synchronized boolean createGame(int numberOfPlayers, String mapName, UUID clientId, CommunicationType communicationType, SubscriberInterface subscriber) throws IOException{

        LOGGER.info("[CREATEGAME] #Players: " + numberOfPlayers + " Map: " + mapName +
                " [COMMUNICATION] " + communicationType + " [FROM] " + clientId.toString() );

        if(!gamePerPlayer.containsKey(clientId)){
            Model newGame = new Model(numberOfPlayers, mapName);
            gamesHosted.add(newGame);
            newGame.setGameState(GameState.WAITING_FOR_PLAYERS);

            newGame.addObserver(broker);

            broker.addTopic(newGame);

            this.assignClientToGame(subscriber, clientId, newGame, communicationType);

            //starts a timer that removes the game if it doesn't start in 4 minutes
            //meaning not enough people join the game to start
            gamesTimer.setForRemoval(newGame);

            gamesTimer.createTurnTimer(newGame);



            return true;
        }
        else
            return false;


    }

    /**
     * Does all the operation to assign a client to a player to a game 
     * @param clientId the identificator of the player
     * @param game the game the player is playing.
     * @param communicationType RMI or Socket
     * @throws IOException 
     */
    private void assignClientToGame(SubscriberInterface subscriber, UUID clientId, Model game, CommunicationType communicationType) throws IOException{
        gamePerPlayer.put(clientId, game);
        List<Player> pendingPlayers = new ArrayList<Player>();
        pendingPlayers.addAll(game.getPendingPlayers());
        Collections.shuffle(pendingPlayers);
        Player newPlayer = pendingPlayers.iterator().next();

        newPlayer.setClientId(clientId);
        newPlayer.setName(users.get(clientId));
        game.getPlayerById().put(clientId, newPlayer);

        if(communicationType == CommunicationType.SOCKETS){
            broker.subscribe(broker.createSocketSubscriber(),clientId);
        }
        else
            broker.subscribe(subscriber, clientId);

    }



    /**
     * Adds a client as a player to a game already created. If all the players are assigned to a client then the game can start.
     * 
     * @param clientId the id of the player requesting to join a game
     * @param gameNumber the number of the game as it appears on the list of games hosted
     * @param communicationType RMI or Sockets, so as to let the broker know how to send messages to the player
     * @return boolean false if the client is already in another game or the game is already full
     * @throws IOException 
     */

    @Override
    public synchronized boolean joinGame(UUID clientId, int gameNumber, CommunicationType communicationType, SubscriberInterface subscriber) throws IOException{
        LOGGER.info("[JOINGAME] " + gameNumber + " [COMMUNICATION] " + communicationType + " [FROM] " + clientId.toString() );
        try{
            Model gameToJoin = gamesHosted.get(gameNumber-1);

            if(!gamePerPlayer.containsKey(clientId) && !gameToJoin.getPendingPlayers().isEmpty())
                assignClientToGame(subscriber, clientId, gameToJoin, communicationType);

            if(gameToJoin.getPendingPlayers().isEmpty()){

                gameToJoin.addEvent(new EventMessage(EventType.GAME_BEGUN, ""));

                gameToJoin.setGameState(GameState.BEGUN);

                gameToJoin.getCurrPlayer().setPlayerState(PlayerState.BEGIN_TURN);

                gameToJoin.addEvent("It's the turn of: " + gameToJoin.getCurrPlayer().getName());
            }

            return true;

        }catch (IndexOutOfBoundsException e){
            LOGGER.log(Level.SEVERE, "There was an error joining the game", e);
            return false;
        }

    }




    /**
     * Sends an action to the 
     * If it's not the turn of the player sending the action it returns false;
     * 
     * @param clientId the id of the sender
     * @param actionType the name of the action
     * @param parameters the optional parameters for the action, i.e. a coordinate in string form or the name of an item
     * @return boolean the outcome of the action
     */
    @Override
    public synchronized boolean action(UUID clientId, String actionType, String parameters){

        LOGGER.info("[ACTION] " + actionType + " [FROM] " + clientId.toString());
        Model game = gamePerPlayer.get(clientId);
        boolean isClientTurn = game.getCurrPlayer().getClientId().equals(clientId);

        if(isClientTurn){

            Controller controller = new Controller(game);

            return controller.parseAction(actionType, parameters);
        }

        return false;

    }

    /**
     * The first handler of a query, if the client asks for informations about all the games currently hosted on the server then the games manager
     * replies directly, otherwise if it's a query specific to a game we delegate the task to a controller
     * @return the response message to send to the client
     */
    @Override
    public synchronized ResponseMessage query(UUID clientId, String queryType){

        LOGGER.info("[QUERY] " + queryType + " [FROM] " + clientId.toString());
        Model game = gamePerPlayer.get(clientId);

        if("getGamesInformation".equals(queryType))
            return new ResponseMessage(true, getGamesInformation().toString());
        else{
            Controller controller = new Controller(game);
            return controller.computeQuery(clientId, queryType);
        }
    }

    /**
     * Sends a chat message from a player to all the other players participating in its game
     * 
     * @param clientId the id of the sender
     * @param chatMessage the message to share
     */
    @Override
    public synchronized void chat(UUID clientId, String chatMessage){
        LOGGER.info("[CHAT] " + chatMessage + " [FROM] " + clientId.toString());
        Model game = gamePerPlayer.get(clientId);
        String chatString = game.getPlayerById().get(clientId).getName() + " : " + chatMessage;
        game.addEvent(new EventMessage(EventType.CHAT, chatString));
    }

    /**
     * @return the map of the games played by a client
     */
    public synchronized Map<UUID, Model> getGamePerPlayer() {
        return gamePerPlayer;
    }


    /**
     * @return the timer 
     */
    public GamesTimer getTimer(){
        return gamesTimer;
    }

    /**
     * @param game the game to remove
     */
    public void removeGame(Model game){

        gamesHosted.remove(game);

        for(Player p: game.getPlayers()){
            if(p.hasBeenAssignedToClient())
                gamePerPlayer.remove(p.getClientId());
        }

        broker.removeTopic(game);
    }

}
