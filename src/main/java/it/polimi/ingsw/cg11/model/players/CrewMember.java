package it.polimi.ingsw.cg11.model.players;
/**
 * The enumeration of all the characters included in the game
 * @author GerlandoSavio
 *
 */
public enum CrewMember {
    //alien characters
    FIRST("Piero Ceccarella"),
    SECOND("Vittorio Martana"),
    THIRD("Maria Galbani"),
    FOURTH("Paolo Landon"),
    //human characters
    CAPTAIN("Ennio Maria Dominoni"),
    PILOT("Julia Niguloti"),
    PSYCHOLOGIST("Silvano Porpora"),
    SOLDIER("Tuccio Brendon");

    /**
     * name of the character
     */
    private final String name;


    /**
     * constructor for the enum
     * @param name
     */

    private CrewMember(String name) {
        this.name=name;
    }

    /**
     * @return only the name
     */
    public String getName() {
        return name;
    }




}
