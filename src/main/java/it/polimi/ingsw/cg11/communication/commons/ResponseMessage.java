package it.polimi.ingsw.cg11.communication.commons;

import java.io.Serializable;

/** 
 * Is the class of the object that the server sends to the client to communicate the outcome of the
 * command it send, together with a message when needed (for example: send cause of negative outcome, result of query)
 * @author GerlandoSavio, Matteo Pagliari
 */
public class ResponseMessage implements Serializable {
    /**
     * generated UID
     */
    private static final long serialVersionUID = -9135874134159967225L;

    private final boolean ack;
    private String optionalMessage;
    
    /**
     * Constructor for the response message without a string
     * @param ack
     */
    public ResponseMessage(boolean ack) {
        this.ack = ack;
        this.optionalMessage = null;
    }
    
    /**
     * Constructor for the response message with both the boolean and the string
     * @param ack
     * @param optionalMessage
     */
    public ResponseMessage(boolean ack, String optionalMessage){
        this.ack = ack;
        this.optionalMessage = optionalMessage;
    }
    
    /**
     * @return boolean value
     */
    public boolean isAck() {
        return ack;
    }
    
    /**
     * @return the optional message
     */
    public String getOptionalMessage() {
        return optionalMessage;
    }



}
