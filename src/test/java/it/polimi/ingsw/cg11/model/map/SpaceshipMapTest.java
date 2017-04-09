package it.polimi.ingsw.cg11.model.map;

import static org.junit.Assert.*;
import it.polimi.ingsw.cg11.model.CreationHelper;

import org.junit.Before;
import org.junit.Test;

public class SpaceshipMapTest {

	SpaceshipMap map;

	@Before
	public void setUp() throws Exception {
		map = CreationHelper.createMap("Fermi");
	}


	/**
	 * Test findNearSectors we assert that the number of neighbors 
	 * sectors are what we expected looking at the printed map
	 */
	@Test
	public void testFindNearSectors() {
		int nearsectors = map.findNearSectors(new Coordinate(11,10)).keySet().size();
		assertEquals(3, nearsectors);
	}

	/**
	 * We test that the coordinate is distant the number we expect (1, 2 or 3)
	 */
	@Test
	public void testIsReachableInNMoves() {
		boolean outcome = map.isReachableInNMoves(new Coordinate('O',6), new Coordinate('N',7), 2);
		assertTrue(outcome);
	}

}
