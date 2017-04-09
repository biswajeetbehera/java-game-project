package it.polimi.ingsw.cg11.model.map;

import it.polimi.ingsw.cg11.model.players.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Defines a type for a single sector. It keeps track of what players are currently
 * in it, to perform game rules later on.
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class Sector {
    private SectorType type;
    private Set<Player> playersInSector;
    
    /**
     * constructor for sector
     * @param type
     */
    public Sector(SectorType type){
        this.type = type;
        this.playersInSector = new HashSet<Player>();
    }
    /**
     * @return a set of players currently in that sector
     */
    public Set<Player> getPlayersInSector() {
        return playersInSector;
    }
    /**
     * adds a player into the sector
     * @param player
     */
    public void addPlayer(Player player) {
        this.playersInSector.add(player);
    }
    /**
     * removes a single player from the sector  
     * @param player
     */
    public void removePlayer(Player player){
        this.playersInSector.remove(player);
    }

    /**
     * @return the type
     */
    public SectorType getType() {
        return type;
    }
    /**
     * toString method
     */
    @Override
    public String toString() {
        return "Sector";
    }
}
