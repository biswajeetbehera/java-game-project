package it.polimi.ingsw.cg11.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
/**
 * The sub-panel holding the items
 * @author GerlandoSavio
 *
 */
public class ItemPanel extends JPanel {

    private static final long serialVersionUID = -787397659464555196L;
    private transient ClientMediator mediator;
    private transient List<ItemLabel> items;
    private ItemLabel selectedItem;

    /**
     * the constructor for the item panel
     */
    public ItemPanel(){
        super();
        this.setPreferredSize(new Dimension(310,75));
        this.setMinimumSize(new Dimension(310,75));
        this.mediator = ClientMediator.getInstance();
        this.items = new ArrayList<ItemLabel>();
        this.selectedItem = null;
        this.addMouseListener(new Click());
    }
    /**
     * creates a new Item Label
     * @param itemName
     */
    public void addItem(String itemName){
        ItemLabel item = new ItemLabel(itemName);
        this.items.add(item);
        this.add(item);
    }

    /**
     * Asks the server for the available items for the player
     * and adds a new ItemLabel for each one
     */
    public synchronized void refreshItems(){
        this.removeAll();
        this.items.clear();

        String availableItems = mediator.query("availableitems").getOptionalMessage();
        availableItems = availableItems.replaceAll("\\p{P}","");
        String[] itemsInString = availableItems.split(" ");

        for(String item: itemsInString){
            if(item.length()>1)
                addItem(item.toLowerCase());
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * We define the controller inside every panel, that way if we want to change the behaviour (or look) of a component we have to modify only
     * the class of the component
     * @author GerlandoSavio
     *
     */
    private class Click extends MouseAdapter {
/**
 * Selects the item that is located in the point where the mouse clicked
 */
        @Override
        public void mouseClicked(MouseEvent e){
            Point mousePosition = e.getPoint();

            for(ItemLabel i: items){
                if(i.getBounds().contains(mousePosition)){
                    if(selectedItem != null)
                        selectedItem.setSelected(false);
                    selectedItem = i;
                    break;
                }   
            }
            selectedItem.setSelected(true);
        }

        /**
         * Switches image on a mouse over to show name
         */
        @Override
        public void mouseEntered(MouseEvent e){
            for(ItemLabel item: items){
                item.setIcon(new ImageIcon("./src/main/resources/images/" + item.getName() + "1.png"));
                item.repaint();
            }

        }

        /**
         * Switches back to the transparent image when the mouse exits
         */
        @Override
        public void mouseExited(MouseEvent e){
            for(ItemLabel item: items){
                item.setIcon(new ImageIcon("./src/main/resources/images/" + item.getName() + ".png"));
                item.repaint();
            }
        }
    }

/**
 * @return the selected label
 */
    public ItemLabel getSelectedItem(){
        return selectedItem;
    }
}
