package it.polimi.ingsw.cg11.communication.client;

import it.polimi.ingsw.cg11.communication.commons.RequestMessage;
import it.polimi.ingsw.cg11.communication.commons.RequestType;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;
import it.polimi.ingsw.cg11.communication.commons.SocketCommunicator;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The client subclass for communicating over sockets only
 * @author GerlandoSavio
 *
 */
public class ClientSocket extends Client {
    /**
     * general logger for the client
     */
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.client");

    /**
     * constructor 
     * @param ip
     * @param port
     */
    public ClientSocket(String ip, int port){
        super(ip,port);
    }

    /**
     * {@inheritDoc}
     * In "Sockets" we wrap the request into a request message and wait for the server to respond
     */
    @Override
    public void connect(String username) throws ClassNotFoundException, IOException{
        RequestMessage connectionRequest = new RequestMessage(clientId, RequestType.INIT, username, null);
        ResponseMessage response = sendToServer(connectionRequest);
        this.clientId = UUID.fromString(response.getOptionalMessage());
    }

    /**
     * General method to send a request to the server and wait until we receive its response
     * @param request the request message we are sending
     * @return response the response message from the server
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public ResponseMessage sendToServer(RequestMessage request) throws IOException, ClassNotFoundException{
        Socket socket;
        socket = new Socket(ip, port);

        SocketCommunicator server = new SocketCommunicator(socket);

        server.send(request);

        ResponseMessage response = (ResponseMessage) server.receive();

        server.close();

        return response;
    }

    /**
     * {@inheritDoc}
     * In Sockets we wrap the request into a RequestMessage, which interprets it, and then call the subscribe method
     */
    @Override
    public boolean createGame(String numberOfPlayers, String mapName) throws IOException, ClassNotFoundException{

        RequestMessage createGameRequest = new RequestMessage(clientId, RequestType.CREATE_GAME, numberOfPlayers, mapName);
        subscribe();
        ResponseMessage response = sendToServer(createGameRequest);
        return response.isAck();
    }

    /**
     * Creates a new thread on which we receive updates from the server
     * @throws IOException
     */
    public void subscribe() throws IOException{
        boolean subscribed = false;
        ExecutorService executor = Executors.newFixedThreadPool(1);
        while(!subscribed){
            try{
                executor.execute(new ClientSocketSubThread(ip, 7777, this));
                subscribed=true;
            }catch (IOException e){
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e1) {
                    LOGGER.log(Level.WARNING, "interrupted", e1);
                    continue;
                }
                LOGGER.log(Level.WARNING, "Exception while executing the subscriber thread", e);
            }
        }

    }

    /**
     * {@inheritDoc}
     * In Sockets we wrap the request in a RequestMessage and send it to the server, which interprets it.
     */
    @Override
    public boolean joinGame(String gameNumber) throws IOException, ClassNotFoundException {

        RequestMessage request = new RequestMessage(clientId,RequestType.JOIN_GAME, gameNumber, null);
        subscribe();
        ResponseMessage response = sendToServer(request);

        return response.isAck();
    }


    /**
     * {@inheritDoc}
     * In Sockets we wrap the request in a RequestMessage and send it to the server, which interprets it.
     */
    @Override
    public boolean action(String actionType, String parameters) throws ClassNotFoundException, IOException {
        RequestMessage request = new RequestMessage(clientId,RequestType.ACTION, actionType, parameters);
        return sendToServer(request).isAck();
    }

    /**
     * {@inheritDoc}
     * In Sockets we wrap the request in a RequestMessage and send it to the server, which interprets it.
     */
    @Override
    public ResponseMessage query(String queryType) throws ClassNotFoundException, IOException {
        RequestMessage request = new RequestMessage(clientId,RequestType.QUERY, queryType, null);
        return sendToServer(request);
    }

    /**
     * {@inheritDoc}
     * In Sockets we wrap the request in a RequestMessage and send it to the server, which interprets it.
     */
    @Override
    public void chat(String message) throws ClassNotFoundException, IOException {
        RequestMessage request = new RequestMessage(clientId,RequestType.CHAT, message, null);
        sendToServer(request);
    }





}
