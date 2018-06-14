package GUI;

import agents.Action;
import agents.Agent;
import agents.Player;
import main.Board;
import main.Card;
import main.Cluedo;
import main.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class BoardGUI {
    private JFrame frame = new JFrame();
    private PlayerPanel playerPanel = new PlayerPanel(this);
    private MainPanel mainPanel = new MainPanel(this);
    private GridBagConstraints c = new GridBagConstraints();
    private static String[] weapons = {"Knife","Candlestick","Lead pipe","Wrench","Revolver","Rope"};
    private static String[] suspects = {"Colonel Mustard", "Mrs. White", "Miss Scarlet", "Mrs. Peacock", "Professor Plum", "Mr. Green"};
    private static String[] rooms = {"Kitchen", "Lounge", "Ballroom", "Billiard room", "Dining main.Room", "Conservatory", "Library","Hall","Study"};
    private Cluedo game;
    private Player humanPlayer;
    public PlayerManager playerManager;
    private JComboBox weaponsCombo;
    private JComboBox suspectsCombo;
    private JComboBox roomsCombo;
    /**
     * GUI.BoardGUI constructor
     * Runs in a Swing Thread and sets up entire GUI
     */
    public BoardGUI(boolean[][] map, Cluedo newGame, Player human) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                humanPlayer = human;
                game = newGame;
                playerManager = new PlayerManager(human, game, playerPanel);
                frame.setTitle("Cluedo_Sim");
                frame.setLayout(new GridBagLayout());
                addMainPanel(map);
                addPlayerPanel();
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                frame.pack();
                frame.setVisible(true);

            }
        });
    }

    private void addMainPanel(boolean[][] map) {
        mainPanel.initialize(map);
        c.gridx = c.gridy = 0;
        frame.add(mainPanel);
    }


    /**
     * Adds the player panel to the right with a wood background
     */
    private void addPlayerPanel() {
        c.gridx=1;c.gridy=0;
        if(game.hasHumanPlayer) {
            playerPanel.addSidePanel(2);
            playerPanel.setPlayerCards(game.getPlayerHands()[0], "Your Cards:", new int[]{81,126});
        }
        else {
            playerPanel.addSidePanel(5);
            playerPanel.setBotCards(game.getPlayerHands());
        }
        frame.add(playerPanel,c);
    }

    /**
     * Method that creates a dialog for when a player shows you a card
     * then ends your turn upon clicking 'End Turn'
     * @param player
     * @param cardName
     */
    public void showCard(String player, String cardName){
        JDialog cardDialog = new JDialog();
        cardDialog.setLayout(new BoxLayout(cardDialog.getContentPane(),BoxLayout.Y_AXIS));
        cardDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        JLabel text = new JLabel(player+" has shown you:");
        JLabel image = new JLabel(new ImageIcon(getClass().getResource("/cards/"+cardName +".jpg")));
        JButton okayButton = new JButton("Done");
        initializeDialogButton(okayButton, cardDialog, "doneViewing", null);

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
    private void initializeDialogButton(JButton button, JDialog dialog, String type, Card card) {
            button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
                if(type == "doneViewing") playerManager.doneViewingCard();
                else if(type == "accuse") playerManager.accuse(getComboBoxSelections());
                else if(type == "suggest") playerManager.suggest(getComboBoxSelections());
                else if (type == "falsify") playerManager.falsify(card);
            }
        });
    }

    private Card[] getComboBoxSelections() {
        String roomName = (String)roomsCombo.getSelectedItem();
        String weaponName = (String)weaponsCombo.getSelectedItem();
        String suspectName = (String)suspectsCombo.getSelectedItem();
        Card roomCard = new Card("room",roomName);
        Card weaponCard = new Card("weapon",weaponName);
        Card suspectCard = new Card("suspect", suspectName);
        return new Card[]{roomCard,weaponCard,suspectCard};
    }

    public void falsifyDialog(String player, Card[] cards){
        JDialog falsifyDialog = new JDialog();
        falsifyDialog.setLayout(new GridBagLayout());
        falsifyDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        JLabel text = new JLabel("Pick a card to prove "+player+"'s suggestion false:");
        c.gridx=1;c.gridy=0;
        falsifyDialog.add(text,c);
        int i = 0;
        for(Card card:cards){
            String cardName = card.cardName;
            Image cardImage = new ImageIcon(getClass().getResource("/cards/"+cardName +".jpg")).getImage();
            Image scaledCardImage = cardImage.getScaledInstance(81,126,java.awt.Image.SCALE_SMOOTH);
            TransparentButton cardButton = new TransparentButton(new ImageIcon(scaledCardImage));
            initializeDialogButton(cardButton, falsifyDialog, "falsify", card);
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

        initializeDialogButton(button, dialog, type, null);
        //accuseDialog.setPreferredSize(new Dimension(200,425));

        weaponsCombo = new JComboBox(weapons);
        suspectsCombo = new JComboBox(suspects);
        if(type.equals("suggest")) {
            roomsCombo = new JComboBox(new String[]{game.board.getRoom(humanPlayer).roomName});
        }
        else
            roomsCombo = new JComboBox(rooms);

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

    public void movePiece(int[][] locations) {
        mainPanel.setPieces(locations);
    }
}
