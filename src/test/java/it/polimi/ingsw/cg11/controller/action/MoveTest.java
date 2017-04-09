package it.polimi.ingsw.cg11.controller.action;

import static org.junit.Assert.*;
import it.polimi.ingsw.cg11.controller.Controller;
import it.polimi.ingsw.cg11.model.GameState;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.map.EscapeSector;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.Player;
import it.polimi.ingsw.cg11.model.players.PlayerState;
import it.polimi.ingsw.cg11.model.players.PlayerType;

import org.junit.Before;
import org.junit.Test;

public class MoveTest {

    Model m;
    Player p0, p1;
    Move move;
    Controller c;
    //tests for the move action

    @Before
    public void setUp() throws Exception {

        m = new Model(2, "Galilei");
        p0 = m.getCurrPlayer();
        if(p0.getType() == PlayerType.HUMAN){
            p0.setPlayerState(PlayerState.WAITING);
            m.nextPlayer();
        }
        m.setGameState(GameState.BEGUN);
        c = new Controller(m);
    }

    /**
     * Test that the alien when an alien goes into a safe sector its state changes to IS_SAFE
     */
    @Test
    public void testAlienSafe() {

        move = new Move(m, new Coordinate('L',4));
        c.execute(move);
        assertEquals(new Coordinate('L',4), m.getCurrPlayer().getCurrPosition());
        assertEquals(PlayerState.IS_SAFE, p0.getPlayerState());
    }

    /**
     * Test that the alien when an alien goes into a dangerous sector its state changes to IS_IN_DANGER
     */
    @Test
    public void testAlienDangerous(){
        move = new Move(m, new Coordinate('L',5));
        c.execute(move);
        assertEquals(PlayerState.IS_IN_DANGER, m.getCurrPlayer().getPlayerState());
    }

    /**
     * Checks that the super alien can move through three sectors
     */
    @Test
    public void testSuperAlien(){
        p0.setHasGainedSpeed(true);
        move = new Move(m, new Coordinate('L',3));
        c.execute(move);
        assertEquals(new Coordinate('L',3), m.getCurrPlayer().getCurrPosition());
    }

    /**
     * tests that a moved is not performed when a wrong coordinate is passed
     */
    @Test
    public void testInvalidMove(){
        move = new Move(m, new Coordinate('L',6));
        c.execute(move);
        assertEquals(m.getMap().getAlienStart(), p0.getCurrPosition());
    }

    /**
     * tests the 
     */
    @Test
    public void testHumanMove(){
        m.getCurrPlayer().setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p1 = m.getCurrPlayer();
        move = new Move(m, new Coordinate('L',9));
        c.execute(move);
        assertEquals( new Coordinate('L',9), p1.getCurrPosition());	
    }

    /**
     * Tests that when a human player used a Sedatives Item prior to moving 
     * he or she doesn't have to pick a sector card even when moving in a dangerous sector
     */
    @Test
    public void testSedatives(){
        m.getCurrPlayer().setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p1 = m.getCurrPlayer();
        ((HumanPlayer) p1).setUsedSedatives(true);
        move = new Move(m, new Coordinate('M',8)); //this is the coordinate of a dangerous sector
        c.execute(move);
        assertEquals(PlayerState.IS_SAFE, p1.getPlayerState());
    }

    /**
     * Tests that when a human player moves into a dangerous sector
     * its state changes to IS_IN_DANGER so he has to pick a sector card
     */
    @Test
    public void testHumanDangerous(){
        m.getCurrPlayer().setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p1 = m.getCurrPlayer();
        move = new Move(m, new Coordinate('M',8));
        c.execute(move);
        assertEquals(PlayerState.IS_IN_DANGER, p1.getPlayerState());
    }

    /**
     * Tests that when a human player moves into a sector hatch he can only
     * perform an Escape action next
     */
    @Test
    public void testEscape(){
        m.getCurrPlayer().setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p1 = m.getCurrPlayer();
        p1.setCurrPosition(new Coordinate('V',1));
        move = new Move(m, new Coordinate('V',2));
        c.execute(move);
        assertEquals(PlayerState.ESCAPE_LOCK, p1.getPlayerState()); //this state leaves the player with only the Escape action available
    }

    /**
     * Tests that a player can't go into a blocked escape hatch
     */
    @Test
    public void testEscapeBlocked(){
        m.getCurrPlayer().setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p1 = m.getCurrPlayer();
        p1.setCurrPosition(new Coordinate('V',1));
        Coordinate escape = new Coordinate('V',2);
        EscapeSector escapeSector = (EscapeSector) m.getMap().getSectors().get(escape);
        escapeSector.setBlocked(true);
        move = new Move(m, escape);
        c.execute(move);
        assertEquals(PlayerState.BEGIN_TURN, p1.getPlayerState());
    }

    /**
     * Tests that an alien player can't land into an escape hatch, ever.
     */
    @Test
    public void testEscapeAlien(){
        p0.setCurrPosition(new Coordinate('V',1));
        Coordinate escape = new Coordinate('V',2);
        move = new Move(m, escape);
        c.execute(move);
        assertEquals(PlayerState.BEGIN_TURN, p0.getPlayerState());
    }

    /**
     * Tests that the player can't move into the sector he is currently in
     */
    @Test
    public void testMoveInYourSector(){
        m.getCurrPlayer().setPlayerState(PlayerState.WAITING);
        m.nextPlayer();
        p1 = m.getCurrPlayer();
        move = new Move(m, new Coordinate('L',8));
        assertFalse(move.execute());
    }
}