package it.polimi.ingsw.cg11.communication.commons;

import java.io.Serializable;
import java.util.UUID;
/**
 * Defines the object that is sent through the socket from the client to the server.
 * Used in the sockets communication
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public class RequestMessage implements Serializable {
    /**
     * generated UID
     */
    private static final long serialVersionUID = -525388295285365976L;

    private final UUID clientID;
    private final RequestType requestType;
    private final String message;
    private final String parameters;

    /**
     * Constructor for the message 
     * @param clientID the unique id of the client making the request
     * @param requestType for example chat, query or action
     * @param message an optional string with a message from the client
     * @param parameters optional parameters for the request (for example, coordinates or the name of a card)
     */
    public RequestMessage(UUID clientID, RequestType requestType, String message, String parameters){
        this.clientID = clientID;
        this.requestType = requestType;
        this.message = message;
        this.parameters = parameters;
    }
    
    /**
     * @return the id of the client included in the request
     */
    public UUID getClientID() {
        return clientID;
    }

    /**
     * @return the type of request
     */
    public RequestType getRequestType() {
        return requestType;
    }

    /** 
     * @return the message sent to the server
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the optional parameters
     */
    public String getParameters() {
        return parameters;
    }
}
