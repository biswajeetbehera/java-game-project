package it.polimi.ingsw.cg11.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
/**
 * The event sub-panel of the gamepanel.
 * @author GerlandoSavio
 *
 */
public class EventPanel extends JPanel {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");
    private static final long serialVersionUID = -5784260192234303099L;
    private static final String NEWLINE = "\n";

    private JTextArea eventList;
    private JScrollPane scrollPane;
    /**
     * Constructor for the EventPanel, puts all the components into its place
     */
    public EventPanel(){
        super();
        this.setLayout(new BorderLayout());

        this.setSize(330,360);
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        JLabel events = GUIHelper.createLabel("EVENTS");

        this.add(events, BorderLayout.PAGE_START);

        eventList = new JTextArea();
        eventList.setBackground(Color.BLACK);
        eventList.setForeground(Color.WHITE);
        eventList.setFont(GUIHelper.sourcecodepro(Font.PLAIN, 11));
        eventList.setEditable(false);
        eventList.setBorder(BorderFactory.createEmptyBorder());
        eventList.setLineWrap(true);
        eventList.setWrapStyleWord(true);

        scrollPane = new JScrollPane(eventList);
        scrollPane.setPreferredSize(new Dimension(310,335));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);


        this.setBackground(Color.BLACK);
        this.add(scrollPane, BorderLayout.PAGE_END);
        this.setVisible(true);
    }
    /**
     * @param event appends an event to the text area
     */
    public void addEvent(String event){

        eventList.append(event.toUpperCase() + NEWLINE);
        eventList.append("--------------------" + NEWLINE);
        eventList.setCaretPosition(eventList.getDocument().getLength());

        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("./src/main/resources/sounds/eventclip.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            LOGGER.log(Level.WARNING, "Error loading the event sound effect", e);
        }

    }

}
