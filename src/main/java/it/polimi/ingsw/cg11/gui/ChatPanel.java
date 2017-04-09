package it.polimi.ingsw.cg11.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

/**
 * Panel for the chat
 * @author GerlandoSavio
 *
 */
public class ChatPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = 6641092454500781097L;
    private static final String NEWLINE = "\n";


    protected JTextArea textArea;
    protected JTextField textField;
    protected transient ClientMediator mediator;

    /**
     * Constructor for the chat area
     */
    public ChatPanel(){

        super();
        this.setLayout(null);
        this.mediator = ClientMediator.getInstance();
        this.setSize(970,169);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setBounds(0,0,970,157);

        Color color1;
        Color color2;

        //we set the color according
        if("Alien".equals(mediator.getPlayerType())){
            color1 = GUIHelper.ALIENCOLOR;
            color2 = GUIHelper.ALIENCOLORDARK;
        }
        else{
            color1 = GUIHelper.HUMANCOLOR;
            color2 = GUIHelper.HUMANCOLORDARK;
        }

        textArea.setBorder(BorderFactory.createMatteBorder(5, 0, 0, 0, color1));
        textArea.setBackground(color2);
        textArea.setForeground(Color.WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBounds(0,0,970,157);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        textArea.setOpaque(true);
        add(scrollPane);


        textField = new JTextField();
        textField.setBounds(0,157,970,12);
        textField.addActionListener(this);
        textField.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                textField.setText("");
            }});
        textField.setBackground(Color.WHITE);
        textField.setForeground(Color.BLACK);
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setText(">Click here to send a message (foul language is endorsed)");

        add(textField);


        this.setVisible(true);

    }


    /**
     * creates a new worker thread to send the message once we press enter
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String text = textField.getText();
        if(text.length()>0){
            SwingWorker<Void, Void> chatWorker = new ChatWorker(text);
            chatWorker.execute();

            textField.setText("");
        }
    }

    
    /**
     * appends a new message to the panel
     * @param message
     */
    public void addChatMessage(String message){
        textArea.append(message + NEWLINE);
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }


    /**
     * Worker thread to send a message to the server through the mediator
     * @author GerlandoSavio
     *
     */
    private class ChatWorker extends SwingWorker<Void,Void>{
        private String chatMessage;

        protected ChatWorker(String chatMessage){
            this.chatMessage = chatMessage;
        }

        @Override
        protected Void doInBackground() throws Exception {
            mediator.chat(chatMessage);

            textField.setText("");
            return null;
        }

    }
}
