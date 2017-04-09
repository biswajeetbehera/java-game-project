package it.polimi.ingsw.cg11.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
/**
 * The panel which contains the information about the player's character, with buttons to perform actions and items available for use or discard.
 * It also includes the round number.
 * @author GerlandoSavio
 *
 */
public class PlayerPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");
    private static final long serialVersionUID = -8491954996994721444L;

    //the type that handles all communication from the client to the server
    private final transient ClientMediator mediator;

    /**
     * the panels that include the actual buttons and labels for actions and items
     */
    private ActionPanel actionPanel;
    private ItemPanel itemPanel;
    private MusicPanel musicPanel;

    private String character;
    private JLabel roundLabel;

    /**
     * Constructor for the player panel
     * The player panel uses a border layout for the components in the top bottom and center.
     * The center component is another sub-panel that uses a box layout to 
     */
    public PlayerPanel(){
        super();

        //gets the shared instance of the client mediator
        this.mediator = ClientMediator.getInstance();


        this.setLayout(new BorderLayout());
        this.setAlignmentX(LEFT_ALIGNMENT);
        this.setPreferredSize(new Dimension(310,360));


        //we leave some space to the left for readability
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        this.setBackground(Color.BLACK);

        //panel with the informations about the character
        this.add(playerInfo(), BorderLayout.NORTH);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel,BoxLayout.Y_AXIS));
        subPanel.setOpaque(false);

        //simple label
        JLabel avActions = GUIHelper.createLabel("Available Actions");
        avActions.setAlignmentX(Component.LEFT_ALIGNMENT);
        avActions.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        subPanel.add(avActions);


        //the panel with the action buttons
        this.actionPanel = new ActionPanel();
        actionPanel.setOpaque(false);
        actionPanel.setVisible(true);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subPanel.add(actionPanel);

        //simple label
        JLabel avItems = GUIHelper.createLabel("Available Items");
        avItems.setAlignmentX(Component.LEFT_ALIGNMENT);
        avItems.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        subPanel.add(avItems);

        this.itemPanel = new ItemPanel();
        itemPanel.setOpaque(false);
        itemPanel.setVisible(true);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subPanel.add(itemPanel);

        JLabel music = GUIHelper.createLabel("Music");
        music.setAlignmentX(Component.LEFT_ALIGNMENT);
        music.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        subPanel.add(music);

        this.musicPanel = new MusicPanel();
        musicPanel.setOpaque(false);
        musicPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subPanel.add(musicPanel);


        this.add(subPanel, BorderLayout.CENTER);

        //the label that indicates what round we are currently playing

        roundLabel = GUIHelper.createLabel("Round Number: 0");
        refreshRoundNumber();
        this.add(roundLabel, BorderLayout.SOUTH);
    }

    private JLabel playerInfo() {

        String characterInfo = mediator.query("playerinfo").getOptionalMessage();

        if(characterInfo.contains("alien"))
            mediator.setPlayerType("Alien");
        else
            mediator.setPlayerType("Human");

        String[] characterInfos = characterInfo.split(",");

        String[] roles = {"captain", "pilot", "soldier", "psychologist", "first", "second", "third", "fourth"};

        for(String s: roles){
            if(characterInfo.contains(s)){
                this.character = s.toUpperCase();
                break;
            }
        }

        JLabel characterLabel = new JLabel("character");
        characterLabel.setText("<html>" + characterInfos[0] + "<br />" + characterInfos[1]+ "</html>");
        characterLabel.setFont(GUIHelper.sourcecodepro(Font.PLAIN, 12));
        characterLabel.setForeground(Color.WHITE);

        return characterLabel;


    }
/**
 * @return the item subpanel
 */
    public ItemPanel getItemPanel(){
        return itemPanel;
    }
/**
 * @return the action subpanel
 */
    public ActionPanel getActionPanel(){
        return actionPanel;
    }

    /**
     * refreses the items
     */
    public void refreshItems(){
        itemPanel.refreshItems();
    }
    
    /**
     * Overridden to paint the picture of the character of the player into the background
     */
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        try {
            g.drawImage(ImageIO.read(new File("./src/main/resources/images/" + character + ".png")), 73, 0, null);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error loading character picture", e);
        }
    }
    
    /**
     * refreshes the round number label to the current round number
     */
    public synchronized void refreshRoundNumber(){
        String roundNumber = mediator.query("roundnumber").getOptionalMessage();
        roundLabel.setText("Round Number: " + roundNumber);
        this.revalidate();
        this.repaint();

    }
}
