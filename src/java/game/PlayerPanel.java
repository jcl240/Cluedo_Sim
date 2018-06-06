import javax.swing.*;
import java.awt.*;

public class PlayerPanel extends JPanel {

    public PlayerPanel() {
        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY,2));
        this.setPreferredSize(new Dimension(215,715));
        this.setLayout(new OverlayLayout(this));
        addWoodPanel();
        setPlayerCards(new String[]{"leadpipe","mustard","lounge","knife"});
    }

    private void addWoodPanel() {
        JPanel woodPanel = new JPanel();
        woodPanel.setBackground(Color.DARK_GRAY);
        JLabel wood = new JLabel();
        woodPanel.add(wood);
        this.add(woodPanel,-1);
        ImageIcon image = new ImageIcon(
                getClass().getResource(
                        "wood.jpg"));
        wood.setIcon(image);
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
        this.add(cardPanel,0);
        for(String cardName: cards){
            Image cardImage = new ImageIcon(getClass().getResource("/cards/"+cardName +".jpg")).getImage();
            Image scaledCardImage = cardImage.getScaledInstance(81,126,java.awt.Image.SCALE_SMOOTH);
            JLabel card = new JLabel(new ImageIcon(scaledCardImage));
            cardPanel.add(card);
        }
    }
}
