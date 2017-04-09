package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.CommunicationType;
import it.polimi.ingsw.cg11.communication.commons.RequestMessage;
import it.polimi.ingsw.cg11.communication.commons.RequestType;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.util.UUID;

/**
 * It is the object that computes the Request Messages of the client and generates the correct Response Message
 * calling the appropriate methods of the OmniView
 * @author GerlandoSavio
 *
 */
public class MessageInterpreter {

    private static MessageInterpreter instance = new MessageInterpreter();
    private GamesManager gameManager; 

    private MessageInterpreter(){
        gameManager = GamesManager.getInstance();
    }

    /**
     * @return the shared instance of the message interpreter
     */
    public static synchronized MessageInterpreter getInstance() {
        return instance;
    }


    /**
     * It's the first method of the interpreter to receive the request message, it returns the response to the caller (usually the
     * Client Handler) properly calculated by the appropriate method.
     * @param request
     * @return ResposeMessage to be delivered to the client
     * @throws NumberFormatException
     * @throws IOException 
     */
    public synchronized ResponseMessage compute(RequestMessage request) throws IOException{

        RequestType type = request.getRequestType();

        switch(type){

        case INIT:
            String username = request.getMessage();
            UUID clientID = gameManager.addUser(username);
            return new ResponseMessage(true, clientID.toString());

        case CREATE_GAME:
            return this.createGame(request);

        case JOIN_GAME:
            return joinGame(request);

        case QUERY:
            return gameManager.query(request.getClientID(), request.getMessage());

        case ACTION: 
            boolean outcome = gameManager.action(request.getClientID(), request.getMessage(), request.getParameters());
            return new ResponseMessage(outcome);

        case CHAT:
            gameManager.chat(request.getClientID(), request.getMessage());
            return new ResponseMessage(true);

        default:
            return new ResponseMessage(false, "The request can't be computed");
        }


    }


    /**
     * delivers the username of the new client to the MultiGameController and returns the response message of the server with the 
     * outcome of the operation and the newly generated UUID.
     * @param request
     * @return ResponseMessage with the UUID of the client that just joined the server
     * @throws AlreadyBoundException 
     */
    private ResponseMessage createGame(RequestMessage request) throws IOException{
        boolean outcome = gameManager.createGame(Integer.valueOf(request.getMessage()), request.getParameters(), request.getClientID(), CommunicationType.SOCKETS, null);

        if(outcome){
            return new ResponseMessage(true, "Game created!");
        }

        return new ResponseMessage(false, "You are already in a game!");
    }

    /**
     * delivers the username of the new client to the MultiGameController and returns the response message of the server with the 
     * outcome of the operation and the newly generated UUID.
     * @param request
     * @return ResponseMessage with the UUID of the client that just joined the server
     * @throws AlreadyBoundException 
     */
    private ResponseMessage joinGame(RequestMessage request) throws IOException{
        if(gameManager.joinGame(request.getClientID(), Integer.valueOf(request.getMessage()), CommunicationType.SOCKETS, null))
            return new ResponseMessage(true, "You correctly joined the game!!");
        else
            return new ResponseMessage(false, "You can't join this game.");
    }



}
