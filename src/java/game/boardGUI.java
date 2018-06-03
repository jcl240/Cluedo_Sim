import javax.swing.*;
import java.awt.*;

import static java.awt.GridBagConstraints.BOTH;

public class boardGUI {
    private JFrame frame = new JFrame();
    private JPanel mainPanel = new JPanel();
    private JButton[][] tiles = new JButton[24][25];
    private JPanel tilePanel = new JPanel();
    private JPanel backgroundPanel = new JPanel();
    private GridBagConstraints c = new GridBagConstraints();

    public boardGUI() {
        frame.add(mainPanel);

        mainPanel.setPreferredSize(new Dimension(672,705));
        mainPanel.setLayout(new OverlayLayout(mainPanel));
        frame.setResizable(false);
        tilePanel.setLayout(new GridBagLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        mainPanel.add(tilePanel,0);
        mainPanel.add(backgroundPanel,-1);


        addBoard();
        addTiles();

        frame.pack();
        frame.setVisible(true);
    }

    private void addBoard() {
        JLabel board = new JLabel();
        backgroundPanel.add(board);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "clue_board_v2.jpg"));
        board.setIcon(image);
    }

    private void addTiles() {
        tilePanel.setOpaque(false);
        c.fill = BOTH;
        c.weighty=c.weightx=1;
        for(int y = 0; y < 25; y++){
            for(int x = 0; x < 24; x++){
                c.gridx=x;
                c.gridy=y;
                tiles[x][y] = new JButton();
                tiles[x][y].setOpaque(false);
                tiles[x][y].setContentAreaFilled(false);
                //tiles[x][y].setBorder( null );
                tilePanel.add(tiles[x][y], c);
            }
        }
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                boardGUI board = new boardGUI();
            }
        });
    }
}
