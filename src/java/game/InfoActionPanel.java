import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoActionPanel extends JPanel {
    private static final String actionCard = "actionCard";
    private static final String infoCard = "infoCard";
    private CardLayout cl;
    private JLabel infoLabel;
    private BoardGUI GUI;

    public InfoActionPanel(BoardGUI GUI) {
        this.GUI = GUI;
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
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        this.add(panel, infoCard);
        infoLabel = new JLabel("<html>agents.Player 3 is rolling the die.</html>");
        infoLabel.setFont(new Font("Serif", Font.BOLD, 26));
        infoLabel.setForeground(new Color(255, 237, 211));
        infoLabel.setAlignmentX(CENTER_ALIGNMENT);
        panel.add(infoLabel);
        JButton nextButton = new TransparentButton("Next");
        nextButton.setAlignmentX(CENTER_ALIGNMENT);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI.next();
            }
        });
        panel.add(nextButton);
    }


    private void addActionCard() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        this.add(panel, actionCard);
        JLabel label = new JLabel("It's your turn.");
        label.setFont(new Font("Serif", Font.BOLD, 32));
        label.setForeground(new Color(255, 237, 211));
        panel.add(label);
        JButton rollDieButton = new TransparentButton("Roll die");
        rollDieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollDie();
            }
        });
        JButton suggestButton = new TransparentButton("Suggest");
        suggestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                suggest();
            }
        });
        JButton accuseButton = new TransparentButton("Accuse");
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
        GUI.suggestDialog();
    }

    private void accuse() {
        GUI.accuseDialog();
    }

    private void rollDie() {
        GUI.rollDie();
    }
}
