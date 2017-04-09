package it.polimi.ingsw.cg11.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
/**
 * The panel that groups the buttons needed to send an action
 * @author GerlandoSavio
 *
 */
public class ActionPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -2612615074773774880L;
    private final transient ClientMediator mediator;

    /**
     * constructor for the action panel
     */
    public ActionPanel(){
        this.setPreferredSize(new Dimension(310,75));
        this.setMaximumSize(new Dimension(310,75));

        this.mediator = ClientMediator.getInstance();
        refreshActions();
    }

    /**
     * Asks the server (through a chain of responsability panel -> mediator -> client -> communicator -> server)
     * what are the available actions for the player and creates the buttons for it
     */
    public synchronized void refreshActions(){
        this.removeAll();


        String availableActions = mediator.query("availableactions").getOptionalMessage();
        availableActions = availableActions.replaceAll("\\p{P}","");
        String[] actions = availableActions.split(" ");

        for(String action: actions){
            if(action.length()>1)
                addActionButton(action);
        }

        this.revalidate();
        this.repaint();

    }

    /**
     * Creates a JButton for the action
     * @param actionName
     */
    public void addActionButton(String actionName){
        JButton action = new JButton(actionName);
        action.addActionListener(this);
        action.setActionCommand(actionName);
        this.add(action);
    }


    /**
     * Creates a new worker thread for sending the action 
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String actionName = e.getActionCommand();

        SwingWorker<Boolean,Void> worker = new ActionWorker(actionName);
        worker.execute();
    }

    /**
     * sends the action
     * @author GerlandoSavio
     *
     */
    private class ActionWorker extends SwingWorker<Boolean,Void>{
        private String actionName;

        protected ActionWorker(String actionName){
            this.actionName = actionName;
        }

        @Override
        protected Boolean doInBackground() throws Exception {
            boolean outcome = mediator.action(actionName);
            refreshActions();
            return outcome;
        }

    }

}
