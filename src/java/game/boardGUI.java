import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.PAGE_END;
import static java.awt.GridBagConstraints.PAGE_START;

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


    public boardGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {

                frame.setLayout(new GridBagLayout());
                c.gridx = c.gridy = 0;
                frame.add(mainPanel);
                addPlayerPanels();

                mainPanel.setPreferredSize(new Dimension(672, 705));
                mainPanel.setLayout(new OverlayLayout(mainPanel));
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                addBoard();
                addTiles();

                frame.pack();
                frame.setVisible(true);

                showCard("lol", "candlestick");
            }
        });
    }

    private void addPlayerPanels() {
        c.gridx=1;c.gridy=0;
        playerPanel.setPreferredSize(new Dimension(200,705));
        frame.add(playerPanel,c);
        JLabel wood = new JLabel();
        playerPanel.add(wood);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "wood.jpg"));
        wood.setIcon(image);
    }

    private void addBoard() {
        mainPanel.add(backgroundPanel,-1);
        JLabel board = new JLabel();
        backgroundPanel.add(board);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "clue_board_v2.jpg"));
        board.setIcon(image);
    }

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

    private void removeOldPieces() {
        for(int player = 0; player < 4; player++){
            int x = pieceLocations[player][0];
            int y = pieceLocations[player][1];
            tiles[x][y].setIcon(null);
            //tiles[x][y].repaint();
        }
    }

    private void setPlayerCards(){

    }

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
        cardDialog.add(text);
        cardDialog.add(image);
        cardDialog.add(okayButton);
        cardDialog.setPreferredSize(new Dimension(200,425));
        cardDialog.pack();
        cardDialog.setVisible(true);
    }

    private void initializeDialogButton(JButton button, JDialog cardDialog) {
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardDialog.dispose();
                endTurn();
            }
        });
    }

    public void endTurn(){

    }

    public static void main(String[] args){
            boardGUI board = new boardGUI();

    }
}
