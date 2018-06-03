import javax.swing.*;
import java.awt.*;

public class boardGUI {
    private JFrame frame = new JFrame();
    private JPanel mainPanel = new JPanel();
    private JButton[][] tiles = new JButton[24][25];
    private JPanel tilePanel = new JPanel();
    private JPanel backgroundPanel = new JPanel();

    public boardGUI() {
        frame.add(mainPanel);

        mainPanel.setPreferredSize(new Dimension(740,700));
        mainPanel.setLayout(new OverlayLayout(mainPanel));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        mainPanel.add(tilePanel,0);
        tilePanel.setLayout(new GridLayout(25,24));
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
        for(int y = 0; y < 25; y++){
            for(int x = 0; x < 24; x++){
                tiles[x][y] = new JButton();
                tiles[x][y].setOpaque(false);
                tiles[x][y].setContentAreaFilled(false);
                //tiles[x][y].setBorder( null );
                tilePanel.add(tiles[x][y]);
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
