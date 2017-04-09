package it.polimi.ingsw.cg11.model.map;


/**
 * The type for a coordinate of a sector in the spaceship.
 * @author GerlandoSavio
 *
 */
public class Coordinate {

    private int x;
    private int y;

    /**
     * Constructor based on 
     * @param x
     * @param y
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    /**
     * constructor for replicating exactly the parameters of a coordinate of the game
     * For example we can pass: L11, C05 and get a coordinate of two integers that
     * works with our code
     * @param column
     * @param row
     * @throws IllegalArgumentException
     */
    public Coordinate(char column, int row) {
        int columnNum = (column) - 65;

        if(row<0 || row>14 || columnNum<0 || columnNum>22){
            throw new IllegalArgumentException("There's no such coordinate");
        }
        this.x =  columnNum;
        this.y=(row-1);
    }
    /**
     * @return x
     */
    public int getX() {
        return x;
    }
    /**
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * sums the coordinate with another. It is used when calculating neighbors (see SpaceshipMap class)
     * @param c
     * @return the sum of the two coordinate
     */
    public Coordinate sumCoordinate(Coordinate c){
        return new Coordinate(this.x+c.getX(), this.y+c.getY());
    }
    /**
     * @return string of a coordinate re-converted to follow the physical game coordinate convention
     */
    @Override
    public String toString() {
        char letter = (char) (x+65);
        int z = y+1;
        String number = z>9 ? Integer.toString(z) : "0" + z;
        return letter + number;
    }

    /**
     * hashCode
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Coordinate))
            return false;
        Coordinate other = (Coordinate) obj;
        if (x != other.x || y != other.y )
            return false;

        return true;
    }




}
