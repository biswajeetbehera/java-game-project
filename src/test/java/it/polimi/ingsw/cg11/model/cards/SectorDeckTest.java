package it.polimi.ingsw.cg11.model.cards;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class SectorDeckTest {

	SectorDeck sectorDeck;
	
	@Before
	public void setUp() throws Exception {
		sectorDeck = new SectorDeck();
	}

	/**
	 * Simple test to check that the number of elements in the sector deck is what we expect
	 */
	@Test
	public void testSize() {
		assertEquals(25, sectorDeck.cardsToPick.size());
	}
	
	/**
	 * Tests that the number of the elements in the cards to pick pile and the discard cards pile is what we expect
	 */
	@Test
	public void testPickCardEmptyDeck(){
		sectorDeck.discardedCards.addAll(sectorDeck.cardsToPick);
		sectorDeck.cardsToPick.clear();
		sectorDeck.pickCard();
		assertEquals(24, sectorDeck.cardsToPick.size());
		assertEquals(1, sectorDeck.discardedCards.size());
	}

}
