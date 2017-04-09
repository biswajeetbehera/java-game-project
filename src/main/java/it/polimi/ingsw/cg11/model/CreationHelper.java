package it.polimi.ingsw.cg11.model;

import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.map.EscapeSector;
import it.polimi.ingsw.cg11.model.map.Sector;
import it.polimi.ingsw.cg11.model.map.SectorType;
import it.polimi.ingsw.cg11.model.map.SpaceshipMap;
import it.polimi.ingsw.cg11.model.players.AlienPlayer;
import it.polimi.ingsw.cg11.model.players.HumanPlayer;
import it.polimi.ingsw.cg11.model.players.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Is the class that provides static methods to correctly create the spaceship map in which to play and the set of players
 * @author GerlandoSavio
 *
 */
public final class CreationHelper {

    /**
     * private constructor for utility class
     */
    private CreationHelper(){

    }

    /**
     * creates the set of players for a model with the correct number of aliens and humans and assigns them a role
     * @param numberOfPlayers
     * @param map
     * @return the set of players
     */
    public static final List<Player> createPlayer(int numberOfPlayers, SpaceshipMap map){
        List<Player> players = new ArrayList<Player>();
        String[] humanRoles = {"CAPTAIN", "PILOT", "PSYCHOLOGIST", "SOLDIER"};
        String[] alienRoles = {"FIRST", "SECOND", "THIRD", "FOURTH"};

        shuffleArray(humanRoles);
        shuffleArray(alienRoles);
        
        Coordinate alienStart = map.getAlienStart();
        Coordinate humanStart = map.getHumanStart();

        for(int i=0;i<numberOfPlayers;i++){

            if(i%2==0) {
                Player alien = new AlienPlayer(alienStart, i+1, alienRoles[i/2]);
                players.add(alien);
                map.getSectors().get(alienStart).addPlayer(alien);
            }
            else{
                Player human = new HumanPlayer(humanStart, i+1, humanRoles[i/2]);
                players.add(human);
                map.getSectors().get(humanStart).addPlayer(human);
            }
        }

        return players;
    }

    /**
     * @param s - the name of the map
     * @return the spaceship map
     * @throws FileNotFoundException
     */
    public static final SpaceshipMap createMap(String s) throws FileNotFoundException{

        SpaceshipMap map = new SpaceshipMap(s);

        File file = new File(s);
        Scanner input = new Scanner(file);


        while(input.hasNextLine()){

            String line = input.nextLine();
            char sectorType = line.charAt(0);
            int x = Integer.parseInt(line.substring(2,4));
            int y = Integer.parseInt(line.substring(5,7));
            Coordinate c = new Coordinate(x,y);
            switch(sectorType){
            case 'S': map.addSector(c, new Sector(SectorType.SAFE));
            break;
            case 'A': map.addSector(c, new Sector(SectorType.ALIEN));
            map.setAlienStart(c);
            break;
            case 'D': map.addSector(c, new Sector(SectorType.DANGEROUS));
            break;
            case 'H': map.addSector(c, new Sector(SectorType.HUMAN));
            map.setHumanStart(c);
            break;
            case 'E': int n = Integer.parseInt(line.substring(8,10));
            map.addSector(c, new EscapeSector(n));
            break;
            default: 
                break;
            }
        }

        input.close();
        return map;
    }

    /**
     * Simple array shuffle algorithm that let's us simulate the random picking of the character card
     * @param array
     */
    static void shuffleArray(String[] array){
        Random random = new Random();

        for (int i = array.length - 1; i > 0; i--){
            int index = random.nextInt(i + 1);
            String string = array[index];
            array[index] = array[i];
            array[i] = string;
        }
    }
}
