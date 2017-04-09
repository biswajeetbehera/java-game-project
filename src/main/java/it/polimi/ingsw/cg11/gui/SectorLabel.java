package it.polimi.ingsw.cg11.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * The Label representing a SectorLabel
 * @author GerlandoSavio
 *
 */
public class SectorLabel extends JLabel {

    private static final long serialVersionUID = 1034208276399842767L;
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");

    private char column;
    private int row;
    private JLabel coordLabel;
    private boolean selected;
    private boolean currPosition;
    private transient Image currPositionImage;
    private transient Image selectedImage;
    private char sectorType;

    //for animation purposes
    private int r=0;

    private Timer timer;

    private boolean noiseEvent;
    private boolean attackEvent;

    private int speed = 2;
    private int count = 0;

    /**
     * Constructor for the sector label
     * @param sectorChar
     * @param column
     * @param row
     * @param n
     */
    public SectorLabel(char sectorChar, char column, int row, int n){
        super();
        this.column = column;
        this.row = row;
        this.sectorType = sectorChar;

        this.setSize(44, 38);

        selected=false;
        currPosition=false;

        try {
            selectedImage = ImageIO.read(new File("./src/main/resources/images/selected.png"));
            currPositionImage = ImageIO.read(new File("./src/main/resources/images/currposition.png"));
        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, "Can't load sector images", e1);
        }

        this.setIcon(new ImageIcon("./src/main/resources/images/" + sectorType + ".png"));



        coordLabel = new JLabel("coordinate");

        String coordinate = null; 

        if(sectorType == 'D' || sectorType == 'S'){
            coordinate = getCoordinate();
            coordLabel.setFont(GUIHelper.sourcecodepro(Font.PLAIN,12));
            coordLabel.setLocation(11, 0);
            coordLabel.setForeground(Color.WHITE);
        }

        if(sectorType == 'E'){
            coordinate = Integer.toString(n);
            coordLabel.setFont(GUIHelper.sourcecodepro(Font.BOLD, 20));
            coordLabel.setLocation(15, 0);
            coordLabel.setForeground(Color.BLACK);
        }


        coordLabel.setText(coordinate);
        coordLabel.setSize(22,38);
        coordLabel.setHorizontalTextPosition(SwingConstants.CENTER);


        coordLabel.setOpaque(false);

        this.add(coordLabel);
        this.setVisible(true);

        timer = new Timer(40, new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                r+=speed;
                repaint();
            }

        });

    }

    /**
     * @return a string representing the coordinate
     */
    public String getCoordinate(){
        String col = Character.toString(column);
        String rw = row<=9 ? "0"+row : Integer.toString(row);
        return col+rw;
    }

    /**
     * Where the magic happens.
     */
    @Override
    public void paint(Graphics g) {

        super.paint(g);

        if(selected)
            g.drawImage(selectedImage, 0, 0, 44, 38, null);

        if(currPosition)
            g.drawImage(currPositionImage, 0, 0, 44, 38, null);

        if(noiseEvent){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.getHSBColor(0.25f, 1.0f, 1.0f));
            g2.setStroke(new BasicStroke(3));

            Polygon hexagon = hexagon();

            g2.drawPolygon(hexagon);            
            g2.setColor(new Color(54, 255, 0, 80));
            g.fillPolygon(hexagon);

            if(r>20){
                speed = -speed;
            }
            if(r<0){
                speed = -speed;
                count++;
                if(count>1){
                    count=0;
                    setNoise(false);
                }
            }
        }

        if(attackEvent){
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(237,20,112));
            g2.setStroke(new BasicStroke(4));

            Polygon hexagon = hexagon();

            g2.drawPolygon(hexagon);            
            g2.setColor(new Color(0, 0, 0, 80));
            g.fillPolygon(hexagon);

            if(r>20){
                speed = -speed;
            }
            if(r<0){
                speed = -speed;
                count++;
                if(count>1){
                    count=0;
                    setAttack(false);
                }
            }
        }
    }


    /**
     * @param noise the boolean to set (true to start the noise animation)
     */
    public synchronized void setNoise(boolean noise){
        this.noiseEvent=noise;
        if(noise){
            timer.start();
            playClip(new File("./src/main/resources/sounds/noiseclip.wav"));
        }
        else
            timer.stop();
    }

    /**
     * @param attack the boolean to set (true to start the attack animation)
     */
    public synchronized void setAttack(boolean attack) {
        this.attackEvent = attack;
        if(attack){

            timer.start();

            playClip(new File("./src/main/resources/sounds/attackclip.wav"));
        }
        else
            timer.stop();

    }

    /**
     * @param file the audio file to play
     */
    public void playClip(File file){
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            LOGGER.log(Level.SEVERE, "Can't load attack sound clip", e);
        }
    }

    /**
     * @param selected the boolean to set (true) if the sector is selected
     */
    public void setSelected(boolean selected){
        this.selected = selected;
        repaint();
    }

    /**
     * 
     * @param currPosition
     */
    public void setCurrPosition(boolean currPosition){
        this.currPosition = currPosition;
        repaint();
    }

    /**
     * creates an hexagon with the center in the center of the sector (for animation purposes)
     * @return a new hexagon polygon
     */
    private Polygon hexagon(){
        int x=22;
        int y=19;


        Polygon hex = new Polygon();
        for(int i=0; i<6; i++) {
            hex.addPoint((int) (x + r*Math.cos(i*2*Math.PI/6)), (int)( y + r*Math.sin(i*2*Math.PI/6)));
        }

        return hex;
    }
}
