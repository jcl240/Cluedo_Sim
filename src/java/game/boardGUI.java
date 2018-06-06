import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.BOTH;

public class boardGUI {
    private JFrame frame = new JFrame();
    private PlayerPanel playerPanel = new PlayerPanel();
    private MainPanel mainPanel;
    private GridBagConstraints c = new GridBagConstraints();

    /**
     * boardGUI constructor
     * Runs in a Swing Thread and sets up entire GUI
     */
    public boardGUI(boolean[][] map) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                mainPanel = new MainPanel(map);
                frame.setTitle("Cluedo_Sim");
                frame.setLayout(new GridBagLayout());
                addMainPanel();
                addPlayerPanel();
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.pack();
                frame.setVisible(true);

                //showCard("lol", "candlestick");
            }
        });
    }

    private void addMainPanel() {
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
        initializeDialogButton(okayButton, cardDialog);

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
    private void initializeDialogButton(JButton button, JDialog cardDialog) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardDialog.dispose();
                endTurn();
            }
        });
    }

    /**
     *  Ends the player's turn by prompting the game
     */
    public void endTurn(){

    }

}
