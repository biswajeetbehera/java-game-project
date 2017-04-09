package it.polimi.ingsw.cg11.gui;


import java.awt.Graphics;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
/**
 * The sub-panel containing the spaceship map
 * @author GerlandoSavio
 *
 */
public class SpaceshipPanel extends JPanel {

    private static final long serialVersionUID = 7736799817589875044L;

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");

    private final String mapName;
    private transient Map<String,SectorLabel> sectors;
    private transient ClientMediator mediator;
    private SectorLabel selectedSector;
    private String currPosition;

    /**
     * Constructor for the panel, initializes the class attributes and asks to populate the map with all the sector labels
     * @param mapName
     */
    public SpaceshipPanel(String mapName){
        super();
        this.setOpaque(true);
        this.mapName = mapName;
        this.mediator = ClientMediator.getInstance();
        this.setLayout(null);
        this.sectors = new HashMap<String,SectorLabel>();
        this.addMouseListener(new Click());
        populatePanel();
        refreshPosition();
    }

    /**
     * reads the file corresponding to the map name 
     * and for each coordinate creates a new SectorLabel
     */
    public void populatePanel(){
        File file = new File("./src/main/resources/" + mapName);
        Scanner input;
        try {
            input = new Scanner(file);

            while(input.hasNextLine()){
                String line = input.nextLine();
                char sectorType = line.charAt(0);
                int x = Integer.parseInt(line.substring(2,4));
                int y = Integer.parseInt(line.substring(5,7));
                int n=0;

                if (sectorType == 'E')
                    n = Integer.parseInt(line.substring(8,10));

                addSector(sectorType, x, y, n);
            }
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error while generating map", e);
        }
    }

    /**
     * Overridden to paint the background
     */
    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);
        try {
            g.drawImage(ImageIO.read(new File("./src/main/resources/images/spacebg.png")), 0, 0, null);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Can't load background image", e);
        }
    }

    /**
     * Adds a sector label into the panel correctly positioned to replicate the hex grid
     * @param sectorType the character representing the type of the sector (E scape, D angerous, A lien, H uman, S afe)
     * @param x the column
     * @param y the row
     * @param n the number of the escape sector
     */
    public void addSector(char sectorType, int x, int y, int n){
        char column = (char) (x+65);  
        int row = y+1;

        SectorLabel s; 

        s = new SectorLabel(sectorType, column, row, n);

        sectors.put(s.getCoordinate(), s);

        s.setLocation(x*33 + 97, y*38 + x%2 * 19);

        this.add(s);
    }

    /**
     * @return The map connecting the coordinates (in string form) to the SectorLabels
     */
    public Map<String,SectorLabel> getSectors(){
        return sectors;
    }


    /**
     * Method to set the new current position of the player, which we indicated with a red glow over the sector.
     * Ask what is the current position of the player and if it's different from the one the panel was saving before 
     * than we delete the red glow of the old position and put it over the new sector.
     */
    public void refreshPosition(){

        String newPosition = mediator.query("currposition").getOptionalMessage();


        if(currPosition == null)
            this.currPosition = newPosition;

        else if(!newPosition.equals(currPosition)){
            this.sectors.get(currPosition).setCurrPosition(false);
            this.currPosition = newPosition;
        }

        this.sectors.get(currPosition).setCurrPosition(true);

        this.repaint();
    }

    /**
     * We define the controller inside every panel, that way if we want to change the behaviour (or look) of a component we have to modify only
     * the class of the component
     * @author GerlandoSavio
     *
     */
    private class Click extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e){
            Point mousePosition = e.getPoint();

            for(SectorLabel s: sectors.values()){
                if(s.getBounds().contains(mousePosition)){
                    if(selectedSector != null)
                        selectedSector.setSelected(false);
                    selectedSector = s;
                    break;
                }   
            }

            try{

                selectedSector.setSelected(true);
            }catch(NullPointerException e1){
                LOGGER.log(Level.INFO, "You didn't click on a sector, but no worries", e1);
            }
        }
    }
    
    /**
     * sets up the animation for the noise
     * @param coordinate
     */
    public void makeNoise(String coordinate){
        SectorLabel s = sectors.get(coordinate);
        s.setNoise(true);
    }
    
    /**
     * @return the sector selected with the mouse
     */
    public SectorLabel getSelectedSector(){
        return selectedSector;
    }
    
    /**
     * sets up the animation for the attack
     * @param coordinate
     */
    public void makeAttack(String coordinate) {
        SectorLabel s = sectors.get(coordinate);
        s.setAttack(true);
    }
}
