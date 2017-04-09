package it.polimi.ingsw.cg11.model.players;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * A list of states a player can be in, the main purpose of the enumeration is to
 * set what kind of actions a player can do in each state.
 * We implemented a finite state automata in order to replicate the game logic
 * where the states are of course the state of the player, and the transitions are
 * the actions
 * @author GerlandoSavio, Matteo Pagliari
 *
 */
public enum PlayerState {
    WAITING {
        @Override
        public List<String> availableActions() {
            return new ArrayList<String>();
        }
    }, 
    WIN {
        @Override
        public List<String> availableActions() {
            return new ArrayList<String>();
        }
    }, 
    DEAD {
        @Override
        public List<String> availableActions() {
            return new ArrayList<String>();
        }
    }, 
    BEGIN_TURN {
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            String[] available = {"Move","UseItem"};
            availableActions.addAll(Arrays.asList(available));
            return availableActions;
        }
    }, 
    IS_SAFE {
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            String[] available = {"EndTurn","Attack","UseItem"};
            availableActions.addAll(Arrays.asList(available));
            return availableActions;
        }
    },
    IS_IN_DANGER{
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            String[] available = {"PickCard","Attack"};
            availableActions.addAll(Arrays.asList(available));
            return availableActions;
        }
    },
    HAS_ATTACKED {
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            availableActions.add("EndTurn");
            return availableActions;
        }
    }, 
    NOISE_LOCK {
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            availableActions.add("Noise");
            return availableActions;
        }
    }, 
    SPOTLIGHT_LOCK{
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            availableActions.add("Spotlight");
            return availableActions;
        }
    },
    ESCAPE_LOCK{
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            availableActions.add("Escape");
            return availableActions;
        }
    },
    ITEM_LOCK {
        @Override
        public List<String> availableActions() {
            availableActions = new ArrayList<String>();
            String[] available = {"UseItem","DiscardItem"};
            availableActions.addAll(Arrays.asList(available));
            return availableActions;
        }
    }, DISCONNECTED {
        @Override
        public List<String> availableActions() {
            return new ArrayList<String>();
        }
    };

    private static List<String> availableActions;
    /**
     * @return the list of the names of the actions that can be performed from the player, based on the state it is in.
     */
    public abstract List<String> availableActions();


}

