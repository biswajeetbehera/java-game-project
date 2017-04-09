package it.polimi.ingsw.cg11.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
/**
 * Utility class for the GUI, we put some static types in here that we may need at different times in different classes
 * @author GerlandoSavio
 *
 */
public class GUIHelper {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");

    public static final Color ALIENCOLOR = Color.getHSBColor(0.9305f,0.92f, 0.93f);
    public static final Color ALIENCOLORDARK = Color.getHSBColor(0.92778f, 0.92f, 0.15f);
    public static final Color HUMANCOLOR = Color.getHSBColor(0.56111f,0.70f,0.89f);
    public static final Color HUMANCOLORDARK = Color.getHSBColor(0.5638f, 0.70f, 0.15f);

    private GUIHelper(){
        
    }
    /**
     * Creates a new label in the style we prefer
     * 
     * @param text the text to put into the label
     * @return a new formatted JLabel
     */
    public static JLabel createLabel(String text){
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(alienLeague(Font.PLAIN,24));
        label.setText(text);

        return label;
    }
    
    /**
     * Source Code Pro font, console style
     * @param fontStyle (for example Font.PLAIN)
     * @param size of the font
     * @return the new font derived from the file
     */
    public static Font sourcecodepro(int fontStyle, int size){
        Font scp = null;

        try {
            scp = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/SourceCodePro-Regular.ttf"));
        } catch (FontFormatException | IOException e) {
            LOGGER.log(Level.WARNING, "Can't load font", e);
        }

        return scp.deriveFont(fontStyle, size);
    }
    
    /**
     * The Alien League font used in the labels, very cool if I do say so myself
     * @param fontStyle (for example Font.PLAIN)
     * @param size of the font
     * @return the new font derived from the file
     */
    public static Font alienLeague(int fontStyle, int size){
        Font alien = null;
        try {
            alien = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/fonts/alienleagueiibold.ttf"));
        } catch (FontFormatException | IOException e1) {
            LOGGER.log(Level.WARNING, "Can't load font", e1);
        }
        return alien.deriveFont(fontStyle, size);
    }


}
