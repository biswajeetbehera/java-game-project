package it.polimi.ingsw.cg11.model.players;

import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.map.Coordinate;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

/**
 * The main class for the players. It contains all the attributes and methods to 
 * keep track of a single player's state.
 * @author GerlandoSavio, Matteo Pagliari
 * @version 1.0
 *
 */
public abstract class Player extends Observable {
    /**
     * a unique number for the player
     */
    protected int number;
    /**
     * the list of items currently held by the player
     */
    protected List<ItemCard> items;

    /**
     * current coordinate of the player
     */
    protected Coordinate currPosition;
    /**
     * current state of the player
     */
    protected PlayerState playerState;
    /**
     * number of sectors a player can move through in a move
     */
    protected int movableDistance;

    /**
     * the type of the player (really, why should I document this?)
     */
    protected PlayerType type;
    /**
     * UUID to identify the player with the client sending the actions
     */
    protected UUID clientId;
    /**
     * name of the player, it is set when a client is associated with a player
     */
    protected String name;
    /**
     * boolean for when a player can move one sector forward
     */
    protected boolean hasGainedSpeed;

    private final CrewMember role;
    
    private boolean hasMoved;

    /**
     * The constructor for the player
     * @param startPosition
     * @param number
     * @param role the role of the character
     */
    public Player(Coordinate startPosition, int number, String role){
        this.number = number;
        this.currPosition = startPosition;
        this.playerState = PlayerState.WAITING;
        this.items = new ArrayList<ItemCard>(4);
        this.hasGainedSpeed = false;
        this.role=CrewMember.valueOf(role);
        this.hasMoved = false;
    }

    /**
     * setter for current position and adding the move into a log
     * @param destination
     */
    public void setCurrPosition(Coordinate destination){
        this.currPosition=destination;

    } 

    /**
     * checks if a client has been assigned to this player for control
     * @return boolean true iff the player has had a clientId set
     */
    public boolean hasBeenAssignedToClient(){
        return !(clientId == null);
    }

    /**
     * 
     * @return the previously assigned clientID
     */
    public UUID getClientId() {
        return clientId;
    }

    /**
     * sets clientId
     * @param clientID
     */
    public void setClientId(UUID clientID) {
        this.clientId= clientID;
    }
    /**
     * @return name
     */
    public String getName(){
        return name;
    }
    /**
     * set name
     * @param name
     */
    public void setName(String name){
        this.name=name;
    }
    /**
     * adds item to collection of items a player can hold
     * @param card
     */
    public void pickItem(ItemCard card) {
        this.items.add(card);
    }

    /**
     * @return player state
     */
    public PlayerState getPlayerState() {
        return playerState;
    }
    /**
     * set player state
     * @param playerState
     */
    public void setPlayerState(PlayerState playerState) {

        this.setChanged();
        this.notifyObservers(playerState);
        this.playerState = playerState;
    }
    /**
     * @return current position
     */
    public Coordinate getCurrPosition() {
        return currPosition;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }
    /**
     * 
     * @return list of items a player is holding
     */
    public List<ItemCard> getItems() {
        return items;
    }
    /**
     * @return string for player
     */
    @Override
    public String toString() {
        return this.name + " (Player no." + this.number + ")";
    }


    /**
     * @return the movableDistance
     */
    public int getMovableDistance() {
        return movableDistance;
    }

    /**
     * @return the type
     */
    public PlayerType getType() {
        return type;
    }

    /**
     * abstract method for available actions. It needs to be overridden in alien and human class
     * @return the list of the names of available actions
     */
    public abstract List<String> getAvailableActions();


    /**
     * @return the hasGainedSpeed
     */
    public boolean hasGainedSpeed() {
        return hasGainedSpeed;
    }

    /**
     * @return the character information
     */
    public CrewMember getRole(){
        return role;
    }

    /**
     * @param hasGainedSpeed the hasGainedSpeed to set
     */
    public void setHasGainedSpeed(boolean hasGainedSpeed) {
        this.hasGainedSpeed = hasGainedSpeed;
    }

    /**
     * Little sophisticated way to return a string containing all the character informations (Name, role on the spaceship)
     * @return identity string
     */
    public String getIdentity(){
        String identity = "You are the " + type.toString().toLowerCase() + " " + role.getName() + ", the " + role.toString().toLowerCase();

        if(type == PlayerType.ALIEN)
            identity += " alien";

        return identity;
    }

    /**
     * @return boolean that tells if the player has moved during this turn, used in the Spotlight 
     * action to decide the next state of the player.
     */
    public boolean hasMoved() {
        return hasMoved;
    }

    /**
     * @param hasMoved the value to set for the hasMoved attribute, changed in the move action
     */
    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }


}
