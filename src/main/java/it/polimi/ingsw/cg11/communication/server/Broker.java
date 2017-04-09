package it.polimi.ingsw.cg11.communication.server;

import it.polimi.ingsw.cg11.communication.commons.BrokerInterface;
import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.SubscriberInterface;
import it.polimi.ingsw.cg11.communication.commons.EventMessage.EventType;
import it.polimi.ingsw.cg11.model.Model;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * The Broker of the server, it is the publisher of the event messages to send to the clients
 * @author GerlandoSavio
 *
 */
public class Broker implements BrokerInterface, Observer, Serializable {

    private static final long serialVersionUID = 1L;
    protected transient Map<Model,List<SubscriberInterface>> subscribersPerGame;
    private transient ServerSocket publisherSocket;
    private transient Executor executor;
    private transient Registry registry;
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");
    
    
    /**
     * Constructor for the broker
     * Initializes the map of the topics and creates an executor.
     */
    public Broker(){
        this.subscribersPerGame = new HashMap<Model,List<SubscriberInterface>>();

        try {
            this.executor = Executors.newCachedThreadPool();
            this.publisherSocket = new ServerSocket(7777);
            this.registry = LocateRegistry.getRegistry(1099);

            BrokerInterface broker = (BrokerInterface) UnicastRemoteObject.exportObject(this,0);

            registry.bind("Broker", broker);

        } catch (IOException | AlreadyBoundException e) {
            LOGGER.log(Level.WARNING, "Exception", e);
        }
    }

    /**
     * simple method to add a game to the broker and initialize the list of subscribers;
     * @param newGame
     */
    public void addTopic(Model newGame){
        List<SubscriberInterface> subscribers = new ArrayList<SubscriberInterface>();
        this.subscribersPerGame.put(newGame, subscribers);
    }


    /**
     * Method to delete a game from the ones the broker expects updates.
     * @param game the topic to remove
     */
    public void removeTopic(Model game){

        this.subscribersPerGame.remove(game);
        this.subscribersPerGame.keySet().remove(game);
    }

    /**
     * The broker observers all the games in the game manager, so that when we receive an update from the model we can send a message to 
     * all the subscribers relative to that game.
     * @param game the model that notified a change
     * @param eventMessage the message we need to dispatch to the subscribers
     */
    @Override
    public void update(Observable game, Object eventMessage) {
        EventMessage event = (EventMessage) eventMessage;

        //forgive us fathers for we have sinned
        //we need a way to tell the timer to set a game for removal, so we need to get the timer from it
        if(event.getType() == EventType.GAME_OVER){
            GamesManager.getInstance().getTimer().setForRemoval((Model)game);
        }

        for(SubscriberInterface subscriber : this.subscribersPerGame.get(game)){
            try {
                if(subscriber != null)
                    subscriber.dispatchMessage((EventMessage) eventMessage);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Exception", e);
            }
        }

    }

    /**
     * The subscribe method of the broker provides an unified way for rmi and sockets clients to subscribe to a game.
     * If the communication is sockets we create a new subscriber handler and run it, otherwise we look up on the registry the subscriber
     * interface for the client on which we call the dispatch message method.
     * 
     * @param clientId the unique id of the client we use to look him up on the registry
     */
    @Override
    public void subscribe(SubscriberInterface subscriber, UUID clientId) {

        Model game = GamesManager.getInstance().getGamePerPlayer().get(clientId);

        subscribersPerGame.get(game).add(subscriber);
    }

    /**
     * Since a client that uses sockets can't send its subscriberhandler through the createGame or joinGame method we create one just for it
     * @return new subscriber interface through sockets
     */
    public SubscriberInterface createSocketSubscriber(){
        try {
            Socket socketForHandler = publisherSocket.accept();
            SubscriberHandler socketSubscriber = new SubscriberHandler(socketForHandler);
            executor.execute(socketSubscriber);
            return socketSubscriber;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error while creating the subscriber handler", e);
        }

        return null;
    }
}



