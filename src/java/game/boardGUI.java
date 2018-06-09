import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.BOTH;

public class boardGUI {
    private JFrame frame = new JFrame();
    private PlayerPanel playerPanel = new PlayerPanel(this);
    private MainPanel mainPanel;
    private GridBagConstraints c = new GridBagConstraints();
    private static String[] weapons = {"Knife","Candlestick","Lead pipe","Wrench","Revolver","Rope"};
    private static String[] suspects = {"Colonel Mustard", "Mrs. White", "Miss Scarlet", "Mrs. Peacock", "Professor Plum", "Mr. Green"};
    private static String[] rooms = {"Kitchen", "Lounge", "Ballroom", "Billiard room", "Dining Room", "Conservatory", "Library","Hall","Study"};

    /**
     * boardGUI constructor
     * Runs in a Swing Thread and sets up entire GUI
     */
    public boardGUI(boolean[][] map) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setTitle("Cluedo_Sim");
                frame.setLayout(new GridBagLayout());
                addMainPanel(map);
                addPlayerPanel();
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.pack();
                frame.setVisible(true);

                //showCard("lol", "candlestick");
            }
        });
    }

    private void addMainPanel(boolean[][] map) {
        mainPanel = new MainPanel(map, this);
        c.gridx = c.gridy = 0;
        frame.add(mainPanel);
    }


    /**
     * Adds the player panel to the right with a wood background
     */
    private void addPlayerPanel() {
        c.gridx=1;c.gridy=0;
        frame.add(playerPanel,c);
    }

    /**
     * Method that creates a dialog for when a player shows you a card
     * then ends your turn upon clicking 'End Turn'
     * @param player
     * @param cardName
     */
    private void showCard(String player, String cardName){
        JDialog cardDialog = new JDialog();
        cardDialog.setLayout(new BoxLayout(cardDialog.getContentPane(),BoxLayout.Y_AXIS));
        cardDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JLabel text = new JLabel(player+" has shown you:");
        JLabel image = new JLabel(new ImageIcon(getClass().getResource("/cards/"+cardName +".jpg")));
        JButton okayButton = new JButton("End Turn");
        initializeDialogButton(okayButton, cardDialog, "endTurn");

        text.setAlignmentX(Component.CENTER_ALIGNMENT);
        image.setAlignmentX(Component.CENTER_ALIGNMENT);
        okayButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardDialog.setPreferredSize(new Dimension(200,425));

        cardDialog.add(text);
        cardDialog.add(image);
        cardDialog.add(okayButton);
        cardDialog.pack();
        cardDialog.setVisible(true);
    }

    /**
     * Helper method for adding an action listener to the End Turn button
     * @param button
     * @param cardDialog
     */
    private void initializeDialogButton(JButton button, JDialog dialog, String type) {
            button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                if(type == "endTurn") endTurn();
                else if(type == "accuse") accuse();
                else if(type == "suggest") suggest();
                else if (type == "falsify") falsify();
            }
        });
    }

    public void falsifyDialog(String player, String[] cards){
        JDialog falsifyDialog = new JDialog();
        falsifyDialog.setLayout(new GridBagLayout());
        falsifyDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JLabel text = new JLabel("Pick a card to prove "+player+"'s suggestion false:");
        c.gridx=1;c.gridy=0;
        falsifyDialog.add(text,c);
        int i = 0;
        for(String cardName:cards){
            Image cardImage = new ImageIcon(getClass().getResource("/cards/"+cardName +".jpg")).getImage();
            Image scaledCardImage = cardImage.getScaledInstance(81,126,java.awt.Image.SCALE_SMOOTH);
            TransparentButton cardButton = new TransparentButton(new ImageIcon(scaledCardImage));
            initializeDialogButton(cardButton, falsifyDialog, "falsify");
            c.gridx=i;c.gridy=1;
            falsifyDialog.add(cardButton,c);
            i++;
        }
        falsifyDialog.pack();
        falsifyDialog.setVisible(true);
    }

    public void suggestDialog() {
        JDialog suggestDialog = new JDialog();
        JLabel text = new JLabel("Pick the weapon, suspect and room you want to suggest:");
        JButton okayButton = new JButton("End Turn");
        initializeComboDialog(suggestDialog, text, okayButton, "suggest");
    }

    public void accuseDialog() {
        JDialog accuseDialog = new JDialog();
        JLabel text = new JLabel("Pick the weapon, suspect and room you want to accuse:");
        JButton okayButton = new JButton("End Turn");
        initializeComboDialog(accuseDialog, text, okayButton, "accuse");
    }

    private void initializeComboDialog(JDialog dialog, JLabel text, JButton button, String type){
        dialog.setLayout(new GridBagLayout());
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        initializeDialogButton(button, dialog, type);
        //accuseDialog.setPreferredSize(new Dimension(200,425));

        JComboBox weaponsCombo = new JComboBox(weapons);
        JComboBox suspectsCombo = new JComboBox(suspects);
        JComboBox roomsCombo = new JComboBox(rooms);

        c.gridx=1;c.gridy=0;
        dialog.add(text,c);
        c.gridx=0;c.gridy=1;
        dialog.add(weaponsCombo,c);
        c.gridx=1;
        dialog.add(suspectsCombo,c);
        c.gridx=2;
        dialog.add(roomsCombo,c);
        c.gridx=1;c.gridy=2;
        dialog.add(button,c);
        dialog.pack();
        dialog.setVisible(true);
    }

    public void rollDie() {
        falsifyDialog("Player 1", new String[]{"knife", "green","lounge"});
    }

    private void suggest() {
    }

    private void accuse() {
    }

    private void falsify() {
    }

    public void next() {
    }


    /**
     *  Ends the player's turn by prompting the game
     */
    private void endTurn(){

    }

}
