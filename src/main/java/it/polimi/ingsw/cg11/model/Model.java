package it.polimi.ingsw.cg11.model;

import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.EventMessage.EventType;
import it.polimi.ingsw.cg11.model.cards.Deck;
import it.polimi.ingsw.cg11.model.cards.EscapeCard;
import it.polimi.ingsw.cg11.model.cards.EscapeDeck;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemDeck;
import it.polimi.ingsw.cg11.model.cards.SectorCard;
import it.polimi.ingsw.cg11.model.cards.SectorDeck;
import it.polimi.ingsw.cg11.model.map.SpaceshipMap;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.UUID;

/**
 * Is the model of the game, it contains all the information of the state of one single game
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class Model extends Observable {
    /**
     * the set of players in the game
     */
    private final List<Player> players;
    /**
     * the spaceship where the game is taking place
     */
    private final SpaceshipMap map;
    /**
     * The Deck of Item Cards
     */
    private Deck<ItemCard> itemCards;
    /**
     * The Deck of Sector Cards
     */
    private Deck<SectorCard> sectorCards;
    /**
     * The Deck of Escape Cards
     */
    private Deck<EscapeCard> escapeCards;
    /**
     * The current game state
     */
    private GameState gameState;
    /**
     * The game playing for this turn
     */
    private Player currPlayer;
    /**
     * Iterator for the set of players
     */
    private Iterator<Player> playerIterator;
    /**
     * The number of rounds played
     */
    private int roundNumber;
    /**
     * The list of events
     */
    private List<EventMessage> eventsLog;

    private Map<UUID,Player> playersById;

    /**
     * Constructor of the Model, it calls the static methods createMap and createPlaayer
     * @param numberOfPlayers
     * @param mapName
     * @throws FileNotFoundException
     */
    public Model(int numberOfPlayers, String mapName) throws FileNotFoundException{
        //the constructors for the Decks return a Deck that's already shuffled
        this.itemCards = new ItemDeck(); 
        this.sectorCards = new SectorDeck();
        this.escapeCards = new EscapeDeck();
        this.map = CreationHelper.createMap(mapName);
        this.players = CreationHelper.createPlayer(numberOfPlayers, this.map);
        this.playerIterator = players.iterator();
        this.currPlayer = playerIterator.next();
        this.currPlayer.setPlayerState(PlayerState.BEGIN_TURN);
        this.eventsLog = new ArrayList<EventMessage>();
        this.roundNumber = 1;
        this.playersById = new HashMap<UUID,Player>();
    }



    /**
     * Sets the current player to the next player who is waiting to start his turn.
     * When the round is completed we increment the round number so the controller
     * can check that the game has not run for more than 39 rounds.
     */
    public synchronized void nextPlayer() {

        if(gameState == GameState.BEGUN){
            Player nextPlayer;

            if(this.playerIterator.hasNext()){

                do{
                    nextPlayer = playerIterator.next();

                }while(nextPlayer.getPlayerState() != PlayerState.WAITING);

                nextPlayer.setPlayerState(PlayerState.BEGIN_TURN);
                this.currPlayer = nextPlayer;
                this.addEvent("It's now the turn of " + nextPlayer.toString());

            }
            else {
                roundNumber++; //we completed a round
                this.playerIterator = players.iterator();
                this.nextPlayer();
            }
        }

    }

    /**
     * Creates a set of players that have not yet been assigned to a client. I.E. without an UUID from a user
     * @return the set of players without ad UUID
     */
    public Set<Player> getPendingPlayers(){
        Set<Player> pendingPlayers = new HashSet<Player>();
        for(Player p: this.players){
            if(!p.hasBeenAssignedToClient()){
                pendingPlayers.add(p);
            }
        }
        return pendingPlayers;
    }


    //GETTERS AND SETTERS

    /**
     * @return the map that associates each player to its assigned UUID of the client
     */
    public Map<UUID,Player> getPlayerById(){
        return playersById;
    }

    /**
     * adds an event to the list, notifies the observers (in our case the broker) in order to broadcast the message to all the players
     * @param event the EventMessage to share
     */
    public synchronized void addEvent(EventMessage event){
        this.eventsLog.add(event);
        this.setChanged();
        this.notifyObservers(event);
    }

    /**
     * Overloaded method to support our first implementation of the controller
     * It is called only when an event in the game happens so we create a new Event Message of type game_event to notify through the above method
     * @param event the string to put into the new EventMessage 
     */
    public synchronized void addEvent(String event){
        this.addEvent(new EventMessage(EventType.GAME_EVENT, event));
    }

    /**
     * @param currPlayer the currPlayer to set
     */
    public void setCurrPlayer(Player currPlayer) {
        this.currPlayer = currPlayer;
    }

    /**
     * @return the current player
     */
    public Player getCurrPlayer() {
        return currPlayer;
    }

    /**
     * set Game State
     * @param gameState
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        String event = "THE GAME HAS " + gameState.toString();
        this.addEvent(event);
    }


    /**
     * @return the players
     */
    public List<Player> getPlayers() {
        return players;
    }


    /**
     * @return the map
     */
    public SpaceshipMap getMap() {
        return map;
    }


    /**
     * @return the itemCards
     */
    public Deck<ItemCard> getItemCards() {
        return itemCards;
    }


    /**
     * @return the sectorCards
     */
    public Deck<SectorCard> getSectorCards() {
        return sectorCards;
    }


    /**
     * @return the escapeCards
     */
    public Deck<EscapeCard> getEscapeCards() {
        return escapeCards;
    }


    /**
     * @return the roundNumber
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * @return the GameState
     */
    public GameState getGameState() {
        return gameState;
    }


    /**
     * @return the events
     */
    public List<EventMessage> getEvents() {
        return eventsLog;
    }
    
    /**
     * @param roundNumber the round number to set
     */
    public void setRoundNumber(int roundNumber){
        this.roundNumber = roundNumber;
    }


}
