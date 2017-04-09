package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemCard;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;

import org.junit.Before;
import org.junit.Test;

public class NoiseTest {

    Model m;
    Player p1, p2;
    Noise noise;
    Controller c;


    @Before
    public void setUp() throws Exception {
        m = new Model(2, "Galilei");
        p1 = m.getCurrPlayer();
        if(p1.getType() == PlayerType.HUMAN){
            p1.setPlayerState(PlayerState.WAITING);
            m.nextPlayer();
        }
        m.setGameState(GameState.BEGUN);
        c = new Controller(m);
        c = new Controller(m);
    }

    /**
     * Tests the when an alien picks a noise in any sector card (sector card)
     * he can't attack after performing the Noise action
     */
    @Test
    public void testAlienNoise() {
        c.parseAction("move", "M08");
        p1.setPlayerState(PlayerState.NOISE_LOCK);
        c.parseAction("noise", "M08");
        assertEquals(PlayerState.HAS_ATTACKED, p1.getPlayerState());
    }

    /**
     * Tests that even when the only available action is Noise
     * if the player is holding more than four item cards it has to discard one
     */
    @Test 
    public void testFourCardsAlien() {
        ItemCard item1 = new ItemCard(ItemType.ADRENALINE);
        ItemCard item2 = new ItemCard(ItemType.ADRENALINE);
        ItemCard item3 = new ItemCard(ItemType.ADRENALINE);
        ItemCard item4 = new ItemCard(ItemType.ADRENALINE);
        p1.getItems().add(item1);
        p1.getItems().add(item2);
        p1.getItems().add(item3);
        p1.getItems().add(item4);
        Move move = new Move(m, new Coordinate('M',8));
        move.execute();
        move.nextState();
        p1.setPlayerState(PlayerState.NOISE_LOCK);
        noise = new Noise(m, p1.getCurrPosition());
        c.execute(noise);
        assertEquals(PlayerState.ITEM_LOCK, p1.getPlayerState());
    }

    /**
     * Same as above, but for the human player (it can't discard or use an item)
     */
    @Test 
    public void testFourCardsHuman() {
        p1.setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p2 = m.getCurrPlayer();
        p2.setPlayerState(PlayerState.NOISE_LOCK);
        noise = new Noise(m, p2.getCurrPosition());
        c.execute(noise);
        assertEquals(PlayerState.IS_SAFE, p2.getPlayerState());
    }

    /**
     * Tests that we don't make a noise in a non existing coordinate
     */
    @Test
    public void testNotFoundCoordinate(){
        p1.setPlayerState(PlayerState.NOISE_LOCK);
        noise = new Noise(m, new Coordinate('W',7));
        noise.execute();
        assertFalse(noise.execute());
    }



}
