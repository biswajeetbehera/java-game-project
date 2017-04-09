package it.polimi.ingsw.cg11.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
/**
 * The main class of the GUI
 * @author GerlandoSavio
 *
 */
public class GUI {

    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");
    private static JFrame mainFrame;
    static Waiting worker;

    private GUI(){

    }
    /**
     * prepares the gui to show the choice panel
     */
    protected static void createAndShowGUI() {
        mainFrame = new JFrame("Escape From The Aliens In Outer Space - 1.0.0");

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 700);
        mainFrame.setResizable(false);

        mainFrame.getContentPane().setBackground(Color.BLACK);

        JPanel choiceScreen = new ChoicePanel();
        mainFrame.add(choiceScreen);

        ImageIcon icon = new ImageIcon("./src/main/resources/images/icon.png");
        mainFrame.setIconImage(icon.getImage());

        mainFrame.setVisible(true);
        mainFrame.setLocationRelativeTo(null);
    }

    /**
     * Calls the worker to prepare the game
     */
    protected static void setupGame(){

        mainFrame.getContentPane().removeAll();

        worker = new Waiting();
        worker.execute();


    }
    /**
     * Worker that creates the game panel, asks to the server all the informations it needs
     * and waits untill all the player have connected to start playing the game
     * @author GerlandoSavio
     *
     */
    public static class Waiting extends SwingWorker<GamePanel, String>{

        private boolean gameIsRunning = false;
        private boolean gameTimeout = false;

        private JLabel progress = GUIHelper.createLabel("");
        /**
         * creates game panel in background
         */
        @Override
        protected GamePanel doInBackground() throws Exception {
            publish("Setting up your game...");
            GamePanel gamePanel = new GamePanel();
            ClientMediator.getInstance().setGamePanel(gamePanel);


            publish("Waiting for players to connect...");

            while(true){
                if(gameIsRunning)
                    break;
                if(gameTimeout){
                    JOptionPane.showMessageDialog(mainFrame, "Nobody showed up for your game, the system is closing NOWWWHHH!!");
                    mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
                }
                Thread.sleep(500);
            }

            //perhaps I'm being naughty, but I like a software that gives me a split second to get ready
            publish("All done! Enjoy!");
            Thread.sleep(500);

            return gamePanel;
        }

        /**
         * sets the game to running
         */
        public void setGameIsRunning(){
            this.gameIsRunning=true;
        }
        /**
         * tells the worker that the game can't start because not enough players connected
         */
        public void setGameTimeout(){
            this.gameTimeout = true;
        }
        /**
         * pritns the current status of the worker to the screen
         */
        @Override
        protected void process(List<String> chunks) {
            mainFrame.add(progress);
            String text = chunks.get(chunks.size()-1);
            progress.setText("");
            progress.setText(text);
        }

        /**
         * sets the frame to host the game panel
         */
        @Override
        protected void done() {

            try {
                progress.setText("");
                progress.setVisible(false);
                mainFrame.remove(progress);
                GamePanel gamePanel = get();
                gamePanel.setPreferredSize(new Dimension(1280,720));
                mainFrame.add(gamePanel);
                mainFrame.pack();
                mainFrame.revalidate();
                mainFrame.repaint();
            } catch (InterruptedException | ExecutionException e) {

                LOGGER.log(Level.SEVERE, "error starting the game", e);
                JOptionPane.showMessageDialog(mainFrame, "I'm very sorry, but something went terribly wrong while preparing your game. \n "
                        + "I can't to anything about that and the game is going to close... :(" );
                close();

            }
        }

    }


    /**
     * main method to start the gui
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run(){
                createAndShowGUI();
            }
        });
    }
    
    /**
     * Adds event for closing the program
     */
    public static void close() {
        mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSING));
    }



}
