package it.polimi.ingsw.cg11.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Class to play the background music in loop and provide buttons to stop that terrifying sound
 * adapted (and freely copied) from: http://www.developer.com/java/other/article.php/2173111/Java-Sound-Playing-Back-Audio-Files-using-Java.htm
 * @author GerlandoSavio
 *
 */
public class MusicPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");
    private static final long serialVersionUID = 6333790773387096876L;

    private transient AudioFormat audioFormat;
    private transient AudioInputStream audioInputStream;
    private transient SourceDataLine sourceDataLine;
    private JButton play;
    private JButton stop;
    private boolean stopPlayback;
    /**
     * Constructor for the music panel, adds buttos to stop and restart the music
     */
    public MusicPanel() {

        stopPlayback = false;

        play = new JButton("play");
        play.setEnabled(false);

        play.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                play.setEnabled(false);
                stop.setEnabled(true);
                stopPlayback = false;
                playAudio();
            }
        });


        stop = new JButton("stop");
        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                play.setEnabled(true);
                stop.setEnabled(false);
                stopPlayback = true;
            }
        });



        this.add(play);
        this.add(stop);

        playAudio();

    }
    /**
     * Starts the thread that plays the sound track
     */
    public void playAudio(){

        try {
            File soundFile = new File("./src/main/resources/sounds/veryLegalSoundtrack.wav");
            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            audioFormat = audioInputStream.getFormat();

            DataLine.Info dataLineInfo =  new DataLine.Info( SourceDataLine.class, audioFormat);

            sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);

            new PlayThread().start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            LOGGER.log(Level.WARNING, "Error loading audio track", e);
        }
    }
    /**
     * The thread that plays the audio
     * @author GerlandoSavio
     *
     */
    private class PlayThread extends Thread{
        byte[] tempBuffer = new byte[10000];

        /**
         * Reads the data from the the buffered input stream and plays it through the speakers, when the track ends it calls
         * another thread for replaying the track
         */
        @Override
        public void run() {
            try {
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                int count;

                while(!stopPlayback){
                    count = audioInputStream.read(tempBuffer, 0, tempBuffer.length);

                    if(count>0){
                        sourceDataLine.write(tempBuffer, 0, count);
                    }

                    if(count==-1){
                        playAudio();
                        break;
                    }
                }

                sourceDataLine.drain();
                sourceDataLine.close();

            } catch (LineUnavailableException | IOException e) {
                LOGGER.log(Level.WARNING, "Error while reproducing the audio track", e);
            }

        }

    }
    
}
