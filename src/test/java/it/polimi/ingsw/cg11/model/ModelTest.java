package it.polimi.ingsw.cg11.model;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.UUID;

import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;


import org.junit.Test;

public class ModelTest {
    Player p1,p2, p3,p4;

    /**
     * Tests that the player iterator is working correctly
     * @throws FileNotFoundException
     */
    @Test
    public void testNextPlayer() throws FileNotFoundException {
        Model m= new Model(2,"Fermi");
        p1 = m.getCurrPlayer();
        p1.setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p2 = m.getCurrPlayer();
        p2.setPlayerState(PlayerState.WAITING);
        m.nextPlayer();

        //we want to pass the turn to the first player in waiting after the last one who played
        assertEquals(p1, m.getCurrPlayer());
    }


    /**
     * Tests that the iterator works correctly when we have a dead player 
     * in our hands
     * @throws FileNotFoundException
     */
    @Test
    public void testNextPlayer2() throws FileNotFoundException{
        Model m2 = new Model(3, "Fermi");
        p1 = m2.getCurrPlayer();
        p1.setPlayerState(PlayerState.DEAD);
        m2.setGameState(GameState.BEGUN);
        m2.nextPlayer();
        p2 = m2.getCurrPlayer();
        p2.setPlayerState(PlayerState.WAITING);
        m2.nextPlayer();
        p3 = m2.getCurrPlayer();
        p3.setPlayerState(PlayerState.WAITING);
        m2.nextPlayer();
        assertEquals(p2, m2.getCurrPlayer());
        assertEquals(PlayerState.WAITING, p3.getPlayerState());
        assertEquals(PlayerState.DEAD, p1.getPlayerState());
    }
    
    /**
     * Checks that we return the right pending players (the players
     * that have not yet been assigned to a client)
     * @throws FileNotFoundException
     */
    @Test
    public void testPendingPlayer() throws FileNotFoundException{
        Model game = new Model(2, "Galvani");
        p1 = game.getCurrPlayer();
        p1.setClientId(UUID.randomUUID());
        int pendingPlayers = game.getPendingPlayers().size();
        assertEquals(1, pendingPlayers);
    }

}
