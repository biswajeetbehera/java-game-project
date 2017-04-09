package it.polimi.ingsw.cg11.communication.commons;

import java.io.Serializable;
/**
 * The message sent by the broker to the clients
 * @author GerlandoSavio
 *
 */
public class EventMessage implements Serializable {
    /**
     * generated UID
     */
    private static final long serialVersionUID = -6886481351749378925L;

    /**
     * The type of event we send to the client
     */
    private final EventType type;
    
    /**
     * String with a message from the server
     */
    private final String message;

    
    /**
     * Constructor
     * @param type the type of event
     * @param message a string the client receives
     */
    public EventMessage(EventType type, String message){
        this.type=type;
        this.message=message;
    }

    /**
     * @return the type
     */
    public EventType getType() {
        return type;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * simple enumeration differentiating the type of event
     * @author GerlandoSavio
     *
     */
    public enum EventType {
        CHAT,GAME_EVENT,TURN_CHANGE,GAME_BEGUN,GAME_TIMEOUT,GAME_OVER;
    }
}
