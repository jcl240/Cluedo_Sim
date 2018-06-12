package GUI;

import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.BOTH;

public class MainPanel extends JPanel {

    private boolean[][] map;
    private JButton[][] tiles = new JButton[24][25];
    private JPanel tilePanel = new JPanel();
    private JPanel backgroundPanel = new JPanel();
    private GridBagConstraints c = new GridBagConstraints();
    private int[][] pieceLocations = new int[4][2];
    private ImageIcon[] gamePieceIcons = {new ImageIcon(getClass().getResource("/pieces/gamePieceBlue.png")),
            new ImageIcon(getClass().getResource("/pieces/gamePieceGreen.png")),
            new ImageIcon(getClass().getResource("/pieces/gamePieceRed.png")),
            new ImageIcon(getClass().getResource("/pieces/gamePieceYellow.png"))};
    private BoardGUI GUI;
    private int[][] startingLocations = new int[][]{{0,5},{9,24},{23,7},{16,0}};


    public MainPanel(BoardGUI GUI) {
        this.GUI = GUI;
        this.setLayout(new OverlayLayout(this));
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
        this.setBackground(Color.DARK_GRAY);

    }

    public void initialize(boolean[][] map){
        this.map = map;
        addBoard();
        addTiles();
        setPieces(startingLocations);
    }
    /**
     * Adds the board image to the GUI.MainPanel
     */
    private void addBoard() {
        this.add(backgroundPanel,-1);
        JLabel board = new JLabel();
        backgroundPanel.add(board);
        backgroundPanel.setOpaque(false);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "/clue_board_v2.jpg"));
        board.setIcon(image);
    }

    /**
     *  Adds the buttons over each tile
     */
    private void addTiles() {
        tilePanel.setLayout(new GridBagLayout());
        this.add(tilePanel,0);
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
                if(!map[x][y]) {tiles[x][y].setBorderPainted(false); tiles[x][y].setEnabled(false);}
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

}
