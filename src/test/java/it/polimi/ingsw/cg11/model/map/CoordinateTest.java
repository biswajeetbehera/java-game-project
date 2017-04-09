package it.polimi.ingsw.cg11.model.map;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CoordinateTest {

	Coordinate c1, c2, c3, c4, c5;
	
	@Before
	public void setUp() throws Exception {
		c1 = new Coordinate(11,10);
		c2 = new Coordinate('L',11);
		c3 = new Coordinate('A',1);
		c4 = new Coordinate(0,0);
		}

	/**
	 * Test if the two constructors produce the same coordinate
	 */
	@Test
	public void testConstructor() {
		assertEquals(c1,c2);
		assertEquals(c3,c4);
	}
	
	
	/**
	 * Tests that the we throw an exception when we receive an incorrect row and/or column
	 */
	@Test(expected=IllegalArgumentException.class)
	public void IllegalArgumentException1() {
	   c5 = new Coordinate('Z',60);
	    
	    
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void IllegalArgumentException2() {
	   c5 = new Coordinate('Z',1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void IllegalArgumentException3() {
	   c5 = new Coordinate('A',60);
	}
	
	
	/**
	 * Test if sumCoordinate produces the expected values
	 */
	@Test
	public void testSumCoordinate(){
		Coordinate c6 = new Coordinate(11,10);
		assertEquals(c6, c1.sumCoordinate(c4));
	}
	
	
	@Test
	public void testSumCoordinateMix(){
		Coordinate c7 = new Coordinate(11,10);
		assertEquals(c7, c1.sumCoordinate(c4));
	}

	/**
	 * Test the equals method when we pass a null object and when we pass a different one
	 */
	@Test
	public void testEquals1(){
		Coordinate cor = new Coordinate('L',10);
		assertFalse(cor.equals(null));
		assertFalse(cor.equals(5));
	}
	
	@Test
	public void testEquals2(){
		Coordinate cor = new Coordinate('L',7);
		assertFalse(c1.equals(c3));
		assertFalse(c1.equals(cor));
	}

}
