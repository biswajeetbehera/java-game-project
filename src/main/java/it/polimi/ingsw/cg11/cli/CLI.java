package it.polimi.ingsw.cg11.cli;

import it.polimi.ingsw.cg11.communication.client.Client;
import it.polimi.ingsw.cg11.communication.client.ClientRMI;
import it.polimi.ingsw.cg11.communication.client.ClientSocket;
import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The CLI view of the game
 * @author GerlandoSavio
 *
 */
public class CLI implements Observer {
    /**
     * logger mainly used for errors.
     */
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.cli");

    /**
     * The scanner on which we get the commands from the console
     */
    private final Scanner stdin = new Scanner(System.in);
    /**
     * The client type handling the communication to the server
     */
    private Client client;
    /**
     * represents if the game is running, when changes from true to false we close the cli.
     */
    private boolean gameIsRunning;
    
    private int waitingTime = 0;

    /**
     * Constructor for the CLI
     */
    public CLI(){
        this.gameIsRunning = false;
    }

    /**
     * Simple wrapper for the sysout method
     * @param string
     */
    public static void println(String string){
        System.out.println(string);
    }
    
    /**
     * Groups together the operations needed to connect to the server and initilize the client
     */
    private void start(){

        String command;

        println("Tell me the ip address of the server: ");
        String ip = stdin.nextLine();

        println("What kind of communication would you like to use?");
        println("[S] Sockets");
        println("[R] RMI");

        command = stdin.nextLine();
        boolean socket = command.toUpperCase().startsWith("S");

        if(socket)
            client = new ClientSocket(ip,1337);
        else 
            client = new ClientRMI(ip,1099);

        client.addObserver(this);

        println("Choose a username");
        command = stdin.nextLine();
        try {
            client.connect(command);
        } catch (ClassNotFoundException | IOException e2) {
            LOGGER.log(Level.SEVERE, "Error connecting to the server. Stop right there.", e2);
        }


        println("Mi sono connesso con successo con UUID" + client.getClientID().toString());

        connectToGame();
    }
    
    /**
     * Asks the users for the action they want to perform and kindly asks the client to send it to the server
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void action() throws  IOException, ClassNotFoundException{
        println("What kind of Action?");
        String action = stdin.nextLine();
        if("move".equalsIgnoreCase(action) || "noise".equalsIgnoreCase(action)){
            println("Tell me the coordinate");
            String coordinate = stdin.nextLine();
            println(String.valueOf(client.action(action, coordinate)));
        }

        else if("useitem".equalsIgnoreCase(action) || "discarditem".equalsIgnoreCase(action)){
            println("Tell me the item");
            String item = stdin.nextLine();
            println(String.valueOf(client.action(action, item)));
        }

        else
            println(String.valueOf(client.action(action, null)));

    }
    
    /**
     * Asks the users what kind of informations they need to get from the remote server and asks the client to send the query
     * that provides it
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void query() throws ClassNotFoundException, IOException {
        println("What kind of Query? [AvailableItems, currPosition, AvailableActions]");
        String command = stdin.nextLine();
        ResponseMessage response = client.query(command);
        println(response.getOptionalMessage());
    }

    /**
     * Gets a message to send to the server 
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void chat() throws ClassNotFoundException, IOException{
        println("Send Your Message:");
        String command = stdin.nextLine();
        client.chat(command);
    }


    /**
     * Groups together all the operations to create or join a game
     */
    private void connectToGame(){
        String command = null;
        boolean inAGame = false;
        while(!inAGame){

            println("Would you like to [C]reate a game or [J]oin one?");
            try {
                command = stdin.nextLine();

                if(command.toLowerCase().startsWith("c")){
                    println("Tell me the number of players: ");
                    String numberOfPlayers = stdin.nextLine();
                    println("Tell me the name of the map in which you would like to play");
                    String mapName = stdin.nextLine();

                    //we break the loop if this returns true
                    inAGame = client.createGame(numberOfPlayers, mapName);
                } 

                else if(command.toLowerCase().startsWith("j")){
                    println("We are currently hosting these games:");
                    println(client.query("getGamesInformation").getOptionalMessage());

                    println("Tell me the number of the game you would like to join: ");
                    String gameNumber = stdin.nextLine();

                    inAGame = client.joinGame(gameNumber); //break out of the loop when true
                }
                
                

            }catch (ClassNotFoundException | IOException e) {
                LOGGER.log(Level.WARNING, "Try again.", e);
            }
        }


        while(!gameIsRunning){
            try {
                println("Waiting for players to connect...");
                Thread.sleep(3000);
                waitingTime += 3;
                
                if(waitingTime>240){
                    println("Nobody showed up for yout game... The game is closing NOW!!!11");
                    return;
                }
                
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Problems with thread.sleep, oops..", e);
                continue;
            }
        }

        try {
            println("[IDENTITY] " + client.query("playerinfo").getOptionalMessage());
        } catch (ClassNotFoundException | IOException e1) {
            LOGGER.log(Level.WARNING, "Problem getting your identity", e1);
        }

        gaming();
    }
    
    /**
     * groups together the commands we loop while playing the game
     */
    private void gaming(){ 
       String command = null;
       
        while(gameIsRunning){
            try{
                println("What do you want to do? [Action/Query/Chat]");
                command = stdin.nextLine();

                switch(command.toLowerCase()){
                case "action":
                    action();
                    break;

                case "query":
                    query();
                    break;

                case "chat":
                    chat();
                    break;

                default:
                    break;
                }
                
            }catch(IOException | ClassNotFoundException e){
                LOGGER.log(Level.WARNING, "There was a problem with this request", e);
                continue;
            }

        }

        println("The game ended! Goodbye.");

        stdin.close();

    }
    
    /**
     * the cli is an observer of the client 
     */
    @Override
    public void update(Observable o, Object arg) {
        EventMessage message = (EventMessage) arg;

        switch(message.getType()){
        case GAME_BEGUN:
            this.gameIsRunning =  true;
            break;

        case GAME_OVER:
            println(message.getMessage());
            this.gameIsRunning = false;
            break;

        case TURN_CHANGE:
            break;

        default:
            println(message.getMessage());
            break;
        }

    }

    /**
     * Starts the CLI
     * @param args
     */
    public static void main(String[] args) {
        CLI cli = new CLI();
        cli.start();
        return;
    }
}
