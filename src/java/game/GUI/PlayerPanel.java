package GUI;

import agents.Action;
import agents.Player;

import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends JPanel {

    JPanel sidePanel = new JPanel();
    InfoActionPanel infoActionPanel;
    private BoardGUI GUI;

    public PlayerPanel(BoardGUI GUI) {
        this.GUI = GUI;
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
        this.setPreferredSize(new Dimension(215,715));
        this.setLayout(new OverlayLayout(this));
        addWoodPanel();
    }

    public void addSidePanel(int rows) {
        sidePanel.setOpaque(false);
        sidePanel.setPreferredSize(new Dimension(215,715));
        sidePanel.setLayout(new GridLayout(rows,1));
        this.add(sidePanel,0);
        addActionPanel();
    }

    private void addActionPanel() {
        infoActionPanel = new InfoActionPanel(GUI);
        sidePanel.add(infoActionPanel);
    }

    private void addWoodPanel() {
        JPanel woodPanel = new JPanel();
        woodPanel.setBackground(Color.DARK_GRAY);
        JLabel wood = new JLabel();
        woodPanel.add(wood);
        this.add(woodPanel,-1);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "/wood.jpg"));
        wood.setIcon(image);
    }

    /**
     * Helper method for setting player cards and adding them
     * to the player panel
     * @param cards
     */
    public void setPlayerCards(String[] cards, String text, int[] cardSize){
        JPanel cardPanel = new JPanel();
        cardPanel.setOpaque(false);
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Serif", Font.BOLD, 26));
        textLabel.setForeground(new Color(255, 237, 211));
        cardPanel.add(textLabel);
        sidePanel.add(cardPanel);
        for(String cardName: cards){
            Image cardImage = new ImageIcon(getClass().getResource("/cards/"+cardName +".jpg")).getImage();
            Image scaledCardImage = cardImage.getScaledInstance(cardSize[0],cardSize[1],java.awt.Image.SCALE_SMOOTH);
            JLabel card = new JLabel(new ImageIcon(scaledCardImage));
            card.setToolTipText("<html><img src=\"" + (getClass().getResource("/cards/"+cardName +".jpg")) + "\"></img></html>");
            cardPanel.add(card);
        }
    }

    public void setBotCards(String[][] hands){
        int i = 0;
        for(String[] hand: hands){
            setPlayerCards(hand,"Player " + i + "'s cards:", new int[]{36,56});
            i++;
        }
    }

    public void passAction(Action actionTaken, Player currentPlayer) {
        infoActionPanel.updateInfo(actionTaken,currentPlayer);
    }
}
