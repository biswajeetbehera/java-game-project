package it.polimi.ingsw.cg11.gui;

import it.polimi.ingsw.cg11.communication.commons.EventMessage;
import it.polimi.ingsw.cg11.communication.commons.EventMessage.EventType;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * The main panel where you play the game and have lots of fun.
 * @author GerlandoSavio
 *
 */
public class GamePanel extends JPanel{

    private static final long serialVersionUID = -3930316461023281396L;

    private final transient ClientMediator mediator;

    private ChatPanel chatArea;
    private EventPanel eventArea;
    private SpaceshipPanel spaceship;
    private PlayerPanel playerPanel;
    /**
     * The task for refreshing the view
     */
    private transient Runnable doRefresh = new Runnable(){

        @Override
        public void run() {
            spaceship.refreshPosition();
            playerPanel.refreshItems();
            playerPanel.getActionPanel().refreshActions();
            playerPanel.refreshRoundNumber();
        }

    };

    /**
     * Constructor for the game panel, calls the prepare method that puts all the sub-panels into place and running
     */
    public GamePanel() {
        super();
        mediator = ClientMediator.getInstance();
        prepare();
    }



    /**
     * Sets all the components into place
     */
    private void prepare(){

        this.setLayout(null);
        this.setSize(1280, 720);
        this.setOpaque(false);
        this.setVisible(true);


        String map = mediator.query("spaceship").getOptionalMessage();


        spaceship = new SpaceshipPanel(map);

        spaceship.setBounds(0 , 0 , 970, 551);

        this.add(spaceship);



        playerPanel = new PlayerPanel();
        playerPanel.setBounds(970, 0, 310, 360);
        this.add(playerPanel);

        eventArea = new EventPanel();
        eventArea.setBounds(970,360,310,360);
        this.add(eventArea);



        chatArea = new ChatPanel();
        chatArea.setBounds(0, 551, 970, 169);
        this.add(chatArea);



    }


    /**
     * Wrapper for the refresh task, it adds the task to be executed into the event dispatch thread
     */
    public void refresh(){
        SwingUtilities.invokeLater(doRefresh);
    }
    /**
     * Receives an event message and handles it
     * @param message
     */
    public void manageEvent(final EventMessage message) {

        if(message.getType() == EventType.CHAT){
            chatArea.addChatMessage(message.getMessage());
        }

        if(message.getType() == EventType.GAME_EVENT){
            eventArea.addEvent(message.getMessage());
        }
        
        if(message.getType() == EventType.TURN_CHANGE){

            String myId = mediator.getClientID();
            String turnId = message.getMessage();


            if(myId != null && turnId.equals(myId)){
                    refresh();
            }
        }
    }

    /**
     * @return the SpaceshipPanel
     */
    public SpaceshipPanel getSpaceship(){
        return spaceship;
    }

    /**
     * @return the coordinate of the selected sector on the screen
     */
    public String getSelectedCoordinate() {
        return spaceship.getSelectedSector().getCoordinate();
    }
    /**
     * @return the name of the item 
     */
    public String getSelectedItemName(){
        return playerPanel.getItemPanel().getSelectedItem().getName();
    }


}
