import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.BOTH;

public class boardGUI {
    private JFrame frame = new JFrame();
    private JPanel mainPanel = new JPanel();
    private JButton[][] tiles = new JButton[24][25];
    private JPanel tilePanel = new JPanel();
    private JPanel backgroundPanel = new JPanel();
    private GridBagConstraints c = new GridBagConstraints();
    private int[][] pieceLocations = new int[4][2];
    ImageIcon[] gamePieceIcons = {new ImageIcon(getClass().getResource("pieces/gamePieceBlue.png")),
            new ImageIcon(getClass().getResource("pieces/gamePieceGreen.png")),
            new ImageIcon(getClass().getResource("pieces/gamePieceRed.png")),
            new ImageIcon(getClass().getResource("pieces/gamePieceYellow.png"))};
    private JPanel playerPanel = new JPanel();

    /**
     * boardGUI constructor
     * Runs in a Swing Thread and sets up entire GUI
     */
    public boardGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                frame.setTitle("Cluedo_Sim");
                frame.setLayout(new GridBagLayout());
                c.gridx = c.gridy = 0;
                mainPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
                frame.add(mainPanel);
                addPlayerPanel();

                mainPanel.setLayout(new OverlayLayout(mainPanel));
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                addBoard();
                addTiles();

                frame.pack();
                frame.setVisible(true);

                //showCard("lol", "candlestick");
            }
        });
    }

    /**
     * Adds the board image to the MainPanel
     */
    private void addBoard() {
        mainPanel.add(backgroundPanel,-1);
        JLabel board = new JLabel();
        backgroundPanel.add(board);
        backgroundPanel.setBackground(Color.DARK_GRAY);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "clue_board_v2.jpg"));
        board.setIcon(image);
    }

    /**
     *  Adds the buttons over each tile
     */
    private void addTiles() {
        tilePanel.setLayout(new GridBagLayout());
        mainPanel.add(tilePanel,0);
        tilePanel.setOpaque(false);
        c.fill = BOTH;
        c.weighty=c.weightx=1;
        for(int y = 0; y < 25; y++){
            for(int x = 0; x < 24; x++){
                c.gridx=x;
                c.gridy=y;
                tiles[x][y] = new JButton();
                tiles[x][y].setPreferredSize(new Dimension(28,28));
                tiles[x][y].setOpaque(false);
                tiles[x][y].setContentAreaFilled(false);
                tilePanel.add(tiles[x][y], c);
            }
        }
    }

    /**
     * Sets the locations and draws the game pieces
     * @param locations
     */
    public void setPieces(int[][] locations){
        removeOldPieces();
        this.pieceLocations = locations;
        for(int player = 0; player < 4; player++){
            int x = pieceLocations[player][0];
            int y = pieceLocations[player][1];
            tiles[x][y].setIcon(gamePieceIcons[player]);
            //tiles[x][y].repaint();
        }

    }

    /**
     * Helper method for removing old pieces from the board before
     * setting new locations
     */
    private void removeOldPieces() {
        for(int player = 0; player < 4; player++){
            int x = pieceLocations[player][0];
            int y = pieceLocations[player][1];
            tiles[x][y].setIcon(null);
            //tiles[x][y].repaint();
        }
    }

    /**
     * Adds the player panel to the right with a wood background
     */
    private void addPlayerPanel() {
        c.gridx=1;c.gridy=0;
        playerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
        playerPanel.setPreferredSize(new Dimension(215,715));
        playerPanel.setLayout(new OverlayLayout(playerPanel));
        frame.add(playerPanel,c);
        JPanel woodPanel = new JPanel();
        woodPanel.setBackground(Color.DARK_GRAY);
        JLabel wood = new JLabel();
        woodPanel.add(wood);
        playerPanel.add(woodPanel,-1);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "wood.jpg"));
        wood.setIcon(image);
        setPlayerCards(new String[]{"leadpipe","mustard","lounge","knife"});
    }

    /**
     * Helper method for setting player cards and adding them
     * to the player panel
     * @param cards
     */
    private void setPlayerCards(String[] cards){
        JPanel cardPanel = new JPanel();
        cardPanel.setOpaque(false);
        JLabel textLabel = new JLabel("Your Cards:");
        textLabel.setFont(new Font("Serif", Font.BOLD, 32));
        textLabel.setForeground(new Color(255, 237, 211));
        cardPanel.add(textLabel);
        playerPanel.add(cardPanel,0);
        for(String cardName: cards){
            Image cardImage = new ImageIcon(getClass().getResource("/cards/"+cardName +".jpg")).getImage();
            Image scaledCardImage = cardImage.getScaledInstance(81,126,java.awt.Image.SCALE_SMOOTH);
            JLabel card = new JLabel(new ImageIcon(scaledCardImage));
            cardPanel.add(card);
        }
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

    public static void main(String[] args){
            boardGUI board = new boardGUI();

    }
}
