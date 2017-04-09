package it.polimi.ingsw.cg11.controller;

import it.polimi.ingsw.cg11.communication.commons.ResponseMessage;
import it.polimi.ingsw.cg11.controller.action.Action;
import it.polimi.ingsw.cg11.controller.action.Attack;
import it.polimi.ingsw.cg11.controller.action.DiscardItem;
import it.polimi.ingsw.cg11.controller.action.EndTurn;
import it.polimi.ingsw.cg11.controller.action.Escape;
import it.polimi.ingsw.cg11.controller.action.Move;
import it.polimi.ingsw.cg11.controller.action.Noise;
import it.polimi.ingsw.cg11.controller.action.PickCard;
import it.polimi.ingsw.cg11.controller.action.Spotlight;
import it.polimi.ingsw.cg11.controller.action.UseItem;
import it.polimi.ingsw.cg11.model.Model;
import it.polimi.ingsw.cg11.model.cards.ItemType;
import it.polimi.ingsw.cg11.model.map.Coordinate;
import it.polimi.ingsw.cg11.model.players.Player;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The class of the controller, receives commands from the games manager and modifies the state of the model, or queries it for informations to share
 * over the network
 * @author GerlandoSavio
 *
 */
public class Controller {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.communication.server");
    /**
     * The model in which we perform the action
     */
    private Model game;

    /**
     * Constructor for the controller
     * @param game
     */
    public Controller(Model game){
        this.game=game;
    }

    /**
     * The controller gets the Action from the client view and executes it if it is contained in the set of currently available actions that are defined in the 
     * current controller State. Then, if the action successes we change state of our finite state automata.
     * @return boolean with the outcome
     * @param action the Action class implementation on which we call the execute method
     */
    public boolean execute(Action action) {
        List<String> availableMoves = this.game.getCurrPlayer().getAvailableActions();

        if(availableMoves.contains(action.toString())){

            if(action.execute()){
                action.nextState();
                return true;
            }
            return false;
        }


        return false;
    }

    /**
     * translates the request message into an action.
     * The return generates a response to send to the client.
     * @param actionName
     * @param parameters string with optional parameters, like a coordinate in string form.
     * @return outcome of the execute method
     */
    public boolean parseAction(String actionName, String parameters){
        Action action; 

        try{
            switch(actionName.toLowerCase()){
            case "move":
                action = new Move(game, coordinateFromParameters(parameters));
                break;

            case "noise":
                action = new Noise(game, coordinateFromParameters(parameters));
                break;

            case "spotlight":
                action = new Spotlight(game, coordinateFromParameters(parameters));
                break;

            case "attack":
                action = new Attack(game);
                break;

            case "endturn":
                action = new EndTurn(game);
                break;

            case "pickcard":
                action = new PickCard(game);
                break;

            case "useitem":
                action = new UseItem(game, ItemType.valueOf(parameters));
                break;

            case "discarditem":
                action = new DiscardItem(game, ItemType.valueOf(parameters));
                break;

            case "escape":
                action = new Escape(game);
                break;

            default: 
                return false;

            }

        } catch(IllegalArgumentException e){
            LOGGER.log(Level.WARNING, "Exception", e);
            return false;
        }


        return this.execute(action);

    }





    /**
     * A little helper to get us a coordinate from our request message
     * @param coordinate
     * @return a new coordinate
     */
    private Coordinate coordinateFromParameters(String coordinate) {
        try{
            char column = coordinate.charAt(0);
            int row = Integer.valueOf(coordinate.substring(1, 3));
            return new Coordinate(column,row);
        }catch(StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * Queries the model for specific informations
     * @param clientId the sender of the query
     * @param queryType the string with which we distinguish the query
     * @return ResponseMessage with a boolean and a string if necessary
     */
    public ResponseMessage computeQuery(UUID clientId, String queryType) {

        Player queryingPlayer = game.getPlayerById().get(clientId);

        switch(queryType.toLowerCase()){
        case "spaceship":
            return new ResponseMessage(true, game.getMap().getName());

        case "roundnumber":
            return new ResponseMessage(true, String.valueOf(game.getRoundNumber()));

        case "currposition":
            return new ResponseMessage(true, queryingPlayer.getCurrPosition().toString());

        case "availableactions":
            return new ResponseMessage(true, queryingPlayer.getAvailableActions().toString());

        case "availableitems":
            return new ResponseMessage(true, queryingPlayer.getItems().toString());

        case "playerinfo":
            return new ResponseMessage(true, queryingPlayer.getIdentity());

        default:
            return new ResponseMessage(false, "I can't compute this query");
        }
    }

}
