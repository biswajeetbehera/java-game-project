package it.polimi.ingsw.cg11.model.map;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * The class that defines the type for a spaceship map object. 
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class SpaceshipMap{
    private final String name;
    private final Map<Coordinate, Sector> sectors;
    private Coordinate humanStart;
    private Coordinate alienStart;

    /**
     * Constructor for a spaceship map
     * @param name 
     */
    public SpaceshipMap(String name){
        this.name=name;
        this.sectors= new HashMap<Coordinate, Sector>();
    }
    /**
     * adds a single sector to the map, it's used during the creation of the map (see Creation helper class)
     * @param c
     * @param s
     */
    public void addSector(Coordinate c, Sector s){
        this.sectors.put(c, s);
    }
    /**
     * @param c
     * @return the sectors that are at distance 1 from the coordinate we pass as a parameter
     * @see "http://www.redblobgames.com/grids/hexagons/#neighbors"
     */
    public Map<Coordinate, Sector> findNearSectors(Coordinate c){
        Map<Coordinate,Sector> nearSectors = new HashMap<Coordinate,Sector>();

        //I check for each of these directions, relative to the coordinate we are passing
        //as a parameter, if a sector exists in our map, in that case I return it.
        Coordinate[] directions = {new Coordinate(0,-1), new Coordinate(+1,0), new Coordinate(+1,+1),new Coordinate(0,+1), new Coordinate(-1,+1), new Coordinate(-1,0)};


        //we are implementing the map using the odd-q grid of the reference
        //so the direction of the neighbors change if the column is odd or even
        if(c.getX()%2==0){
            Coordinate[] directions2 = {new Coordinate(0,-1), new Coordinate(+1,-1), new Coordinate(+1,0),
                    new Coordinate(0,+1), new Coordinate(-1,0), new Coordinate(-1,-1)};
            directions = directions2;
        }


        for(Coordinate i: directions){
            Coordinate neighbour = c.sumCoordinate(i);
            if(this.sectors.containsKey(neighbour)){
                Sector s = this.sectors.get(neighbour);
                if(!(s.getType()== SectorType.HUMAN ||s.getType() == SectorType.ALIEN)){
                    nearSectors.put(neighbour, s);
                }
            }

        }
        return nearSectors;
    }

    /**
     * DFS to check if a destination is within a distance of n.
     * Arguably a BFS could have been a better choice for this application, but
     * we get good performance with DFS for our average use (distance is usually less than 3,
     * often if the player is moving forward it performs only one check)
     * @param start
     * @param destination
     * @param n
     * @return true or false
     */
    public boolean isReachableInNMoves(Coordinate start, Coordinate destination, int n){
        if(n>0){
            Set<Coordinate> reach = new HashSet<Coordinate>();
            reach = this.findNearSectors(start).keySet();

            if(reach.contains(destination)) 
                return true;


            for(Coordinate c: reach){
                if(isReachableInNMoves(c, destination, n-1))
                    return true;
            }

        }
        return false;
    }
    /**
     * @return the sectors with their coordinates
     */
    public Map<Coordinate, Sector> getSectors() {
        return sectors;
    }
    /**
     * @return the coordinate where the humans start (for the beginning of the game and teleportation)
     */
    public Coordinate getHumanStart() {
        return humanStart;
    }
    /**
     * sets the humanstart
     * @param humanStart
     */
    public void setHumanStart(Coordinate humanStart) {
        this.humanStart = humanStart;
    }
    /**
     * @return the coordinate where the aliens start (for the beginning of the game and teleportation)
     */
    public Coordinate getAlienStart() {
        return alienStart;
    }
    /**
     * sets the alien start
     * @param alienStart
     */
    public void setAlienStart(Coordinate alienStart) {
        this.alienStart = alienStart;
    }
    /**
     * 
     * @return the name of the map (for printing purposes)
     */
    public String getName(){
        return this.name;
    }
    /**
     * toString method
     */
    @Override
    public String toString() {
        return "Map [sectors=" + sectors + "]";
    }

}
