package it.polimi.ingsw.cg11.gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * The label representing an Item in teh game
 * @author GerlandoSavio
 *
 */
public class ItemLabel extends JLabel {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");
    private static final long serialVersionUID = 5690251012969182918L;
    private String name;
    private transient Image selectedImage;
    private boolean selected;
    /**
     * Constructor for the Item type, loads all the images it neeeds
     * @param name
     */
    public ItemLabel(String name){
        super();
        this.selected = false;
        this.name = name;       



        try {
            this.selectedImage = ImageIO.read(new File("./src/main/resources/images/itemselect.png"));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "error loading selection image", e);
        }



        this.setIcon(new ImageIcon("./src/main/resources/images/" + this.name + ".png"));
        this.setSize(65, 65);
        this.setOpaque(false);
    }

    /**
     * Adds a nice looking picture over it when is selected
     */
    @Override
    public void paint(Graphics g) {

        super.paint(g);

        if(selected){

            g.drawImage(selectedImage, 0, 0, 65, 65, null);

        }



    }
    /**
     * @param selected the boolean to set
     */
    public void setSelected(boolean selected){
        this.selected = selected;
        repaint();
    }

    /**
 @return the name of the item
     */
    @Override
    public String getName(){
        return name;
    }

}
