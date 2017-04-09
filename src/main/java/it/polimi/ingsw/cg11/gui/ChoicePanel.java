package it.polimi.ingsw.cg11.gui;

import it.polimi.ingsw.cg11.communication.client.Client;
import it.polimi.ingsw.cg11.communication.client.ClientRMI;
import it.polimi.ingsw.cg11.communication.client.ClientSocket;
import it.polimi.ingsw.cg11.communication.commons.CommunicationType;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
/**
 * The Choice Panel, the first screen that appears when opening the guy, connects to the server after inserting a username
 * 
 * @author GerlandoSavio
 *
 */
public class ChoicePanel extends JPanel  {


    private static final Logger LOGGER = Logger.getLogger("it.polimi.ingsw.cg11.gui");
    private static final long serialVersionUID = -2551673772060150061L;
    private static final String CONNECTTOSERVER = "ConnectPanel";
    private static final String CHOOSEGAME = "ChoicePanel";

    private CommunicationType communicationType;
    private JTextField username;
    private JTextField ipAddress;
    private CardLayout c;
    private JPanel cards;
    
    private transient ClientMediator mediator;
    private transient Client client;
    /**
     * Constructor for the ChoicePanel, sets all the component into place
     */
    public ChoicePanel() {
        mediator = ClientMediator.getInstance();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel logo = new JPanel();
        JLabel logoLabel = new JLabel("logo");
        logoLabel.setText(null);
        logoLabel.setIcon(new ImageIcon("./src/main/resources/images/logo.png"));
        logo.setOpaque(false);
        logo.setBorder(BorderFactory.createEmptyBorder(25,0,0,0));
        logo.add(logoLabel);



        c = new CardLayout();
        cards = new JPanel(c);
        cards.setVisible(true);
        cards.setOpaque(false);

        JPanel connect = new Connect();
        cards.add(connect, CONNECTTOSERVER);
        c.layoutContainer(cards);
        c.addLayoutComponent(connect, CONNECTTOSERVER);

        c.first(cards);
        this.add(logo);
        this.add(cards); 
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);
        Dimension size = new Dimension(600,600);
        this.setPreferredSize(size);
        this.setSize(size);
        this.setOpaque(false);

    }




    /**
     * An inner panel for the connection
     * @author GerlandoSavio
     *
     */
    private class Connect extends JPanel implements ActionListener {

        private static final long serialVersionUID = 1L;
        private JButton connectButton;
        private transient ActionListener comListener = new ActionListener(){
            /**
             * Sets the communicationType according to the radio button selected
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                communicationType = CommunicationType.valueOf(e.getActionCommand().toUpperCase());
            }

        };
        /**
         * Actin listener for when we insert the username
         */
        private transient ActionListener nickListener = new ActionListener(){
            /**
             * enables the button once the user presses enter on the keyboard
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                connectButton.setEnabled(true);
            }

        };

        /**
         * Constructor for the connect panel, sets all the components into place
         */
        public Connect(){
            connectButton = new JButton("CONNECT");
            connectButton.setEnabled(false);
            connectButton.addActionListener(this);


            this.setBorder(BorderFactory.createEmptyBorder(0,25,25,25));
            this.setLayout(new GridLayout(7,1,10,10));
            this.setSize(this.getSize());
            this.setOpaque(false);

            this.add(GUIHelper.createLabel("Server IP Address: "));
            ipAddress = new JTextField(20);
            ipAddress.setText("127.0.0.1");
            ipAddress.setHorizontalAlignment(SwingConstants.CENTER);
            this.add(ipAddress);


            this.add(GUIHelper.createLabel("Choose a username"));

            username = new JTextField(20);
            username.setHorizontalAlignment(SwingConstants.CENTER);
            username.addActionListener(nickListener);
            this.add(username);

            this.add(GUIHelper.createLabel("Choose a method of communication:"));

            JPanel connection = new JPanel(new GridLayout(1,2));
            connection.setSize(this.getPreferredSize());
            connection.setOpaque(false);
            JRadioButton rmi = new JRadioButton("RMI");
            rmi.setMnemonic(KeyEvent.VK_R);
            rmi.setActionCommand("RMI");
            rmi.setSelected(true);
            rmi.addActionListener(comListener);
            rmi.setForeground(Color.WHITE);
            rmi.setBackground(Color.BLACK);

            JRadioButton sockets = new JRadioButton("Sockets");
            sockets.setMnemonic(KeyEvent.VK_S);
            sockets.setActionCommand("Sockets");
            sockets.addActionListener(comListener);
            sockets.setForeground(Color.WHITE);
            sockets.setBackground(Color.BLACK);

            ButtonGroup choiceGroup = new ButtonGroup();
            choiceGroup.add(rmi);
            choiceGroup.add(sockets);

            connection.add(rmi);
            connection.add(sockets);


            this.add(connection);
            this.add(this.connectButton);

            this.setOpaque(false);
            this.setVisible(true);
        }

        /**
         * Sends the connection request to the server through the mediator
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            connectButton.setEnabled(false);

            String ip = ipAddress.getText();

            int port;

            if(communicationType == CommunicationType.SOCKETS){
                port = 1337;
                client = new ClientSocket(ip, port);
            }
            else {
                port = 1099;
                client = new ClientRMI(ip, port);
            }

            try {
                client.connect(username.getText());
            } catch (ClassNotFoundException | IOException e1) {
                LOGGER.log(Level.SEVERE, "Error connecting to the server", e1);
                JOptionPane.showMessageDialog(this.getParent(), "Could not connect to the server \n Make sure you used the right IP address and the"
                        + "firewall allows connections on ports 1337 and 1099");
            }  

            mediator.setClient(client);


            JPanel gameChoice = new ChooseGame();
            cards.add(gameChoice, CHOOSEGAME);
            c.addLayoutComponent(gameChoice, CHOOSEGAME);
            c.show(cards, CHOOSEGAME);

        }


    }

    /**
     * SubPanel for Creating a game or joining one
     * @author GerlandoSavio
     *
     */
    @SuppressWarnings("serial")
    private class ChooseGame extends JPanel implements ActionListener{
        JComboBox<String> spaceshipMap;
        JComboBox<String> numberOfPlayers;
        JTextField gameNumber;
        String gamesInformation;
        JTextArea gamesList;
        JScrollPane gameScroll;
        JPanel selection;
        
        /**
         * private constructor for the panel. Puts all the components into place. Kinda complex, but this is the best
         * we could do with no prior experience with swing
         */
        private ChooseGame(){

            this.setLayout(new GridLayout(5,1,10,10));
            this.setBorder(BorderFactory.createEmptyBorder(0,25,25,25));
            this.add(GUIHelper.createLabel("Join a game"));

            gamesInformation = mediator.query("getGamesInformation").getOptionalMessage();
            gamesInformation = gamesInformation.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\,","");

            gamesList = new JTextArea(gamesInformation);

            gamesList.setLineWrap(true);
            gamesList.setEditable(false);
            gameScroll = new JScrollPane(gamesList);

            this.add(gameScroll);


            selection = new JPanel();
            selection.setOpaque(false);
            
            JButton refresh = new JButton("Refresh List");
            refresh.addActionListener(new ActionListener(){

                @Override
                public void actionPerformed(ActionEvent e) {
                    gamesInformation = mediator.query("getGamesInformation").getOptionalMessage();
                    gamesInformation = gamesInformation.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\,","");
                    
                    gamesList.setText(gamesInformation);
                    revalidate();
                    repaint();
                }
                
            });
            selection.add(refresh);
            
            selection.add(GUIHelper.createLabel("Game Number: "));

            gameNumber = new JTextField();
            gameNumber.setColumns(1);
            selection.add(gameNumber);


            JButton join = new JButton("JOIN");
            join.setActionCommand("joinGame");
            join.addActionListener(this);
            selection.add(join);

            this.add(selection);

            this.add(GUIHelper.createLabel("Create a Game"));

            JPanel create = new JPanel();
            create.add(GUIHelper.createLabel("Number of Players: "));

            numberOfPlayers = new JComboBox<String>();
            for(int i=2; i<=8; i++){
                numberOfPlayers.addItem(String.valueOf(i));
            }
            numberOfPlayers.setSelectedIndex(0);

            create.add(numberOfPlayers);

            create.add(GUIHelper.createLabel("Spaceship: "));

            String[] mapNames = {"Galilei","Galvani","Fermi"};
            spaceshipMap = new JComboBox<String>(mapNames);
            spaceshipMap.setSelectedIndex(0);
            create.add(spaceshipMap);

            JButton send = new JButton("CREATE");
            send.setActionCommand("createGame");
            send.addActionListener(this);
            create.add(send);

            create.setOpaque(false);

            this.add(create);

            this.setOpaque(false);
            this.setVisible(true);
        }

        /**
         * Sends the request to the server through the mediator
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if("createGame".equals(command))
                mediator.createGame((String) numberOfPlayers.getSelectedItem(), (String) spaceshipMap.getSelectedItem());

            else
                mediator.joinGame(gameNumber.getText());


            GUI.setupGame();


        }

    }

}

