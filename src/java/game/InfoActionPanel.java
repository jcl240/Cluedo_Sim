import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoActionPanel extends JPanel {
    private static final String actionCard = "actionCard";
    private static final String infoCard = "infoCard";
    private CardLayout cl;
    private JLabel infoLabel;

    public InfoActionPanel() {
        this.setOpaque(false);
        this.setLayout(new CardLayout());
        addActionCard();
        addInfoCard();
        cl = (CardLayout)(this.getLayout());
    }

    private void setInfoText(String newText){
        infoLabel.setText(newText);
    }

    private void addInfoCard() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));
        panel.setOpaque(false);
        this.add(panel, infoCard);
        infoLabel = new JLabel("<html>Player 3 is rolling the die.</html>");
        infoLabel.setFont(new Font("Serif", Font.BOLD, 26));
        infoLabel.setForeground(new Color(255, 237, 211));
        panel.add(infoLabel);
        JButton nextButton = new NextButton("Next");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });
        panel.add(nextButton);
    }

    private void next() {
    }

    private void addActionCard() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        this.add(panel, actionCard);
        JLabel label = new JLabel("It's your turn.");
        label.setFont(new Font("Serif", Font.BOLD, 32));
        label.setForeground(new Color(255, 237, 211));
        panel.add(label);
        JButton rollDieButton = new JButton("Roll die");
        rollDieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollDie();
            }
        });
        JButton suggestButton = new JButton("Suggest");
        suggestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suggest();
            }
        });
        JButton accuseButton = new JButton("Accuse");
        accuseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                accuse();
            }
        });
        panel.add(rollDieButton);
        panel.add(suggestButton);
        panel.add(accuseButton);
    }

    private void suggest() {
        cl.show(this,infoCard);
    }

    private void accuse() {
        setInfoText("You accused!");
    }

    private void rollDie() {
    }
}
