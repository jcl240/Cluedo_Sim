package GUI;

import agents.Action;
import agents.Agent;
import agents.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoActionPanel extends JPanel {
    private static final String actionCard = "actionCard";
    private static final String infoCard = "infoCard";
    private CardLayout cl;
    private JTextArea infoLabel;
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
        infoLabel = new JTextArea("");
        infoLabel.setEditable(false);
        infoLabel.setOpaque(false);
        infoLabel.setLineWrap(true);
        infoLabel.setFont(new Font("Serif", Font.BOLD, 16));
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
        panel.add(suggestButton);
        panel.add(accuseButton);
    }

    private void suggest() {
        GUI.suggestDialog();
    }

    private void accuse() {
        GUI.accuseDialog();
    }

    public void updateInfo(Action actionTaken, Player currentPlayer) {
        String playerIndex = Integer.toString(((Agent) currentPlayer).playerIndex);
        if(actionTaken.actionType.equals("move"))
            setInfoText("Player " + playerIndex + " moved");
        else if(actionTaken.actionType.equals("useSecretPassage"))
            setInfoText("Player " + playerIndex + " used a secret passage!");
        else if(actionTaken.actionType.equals("suggest"))
            setInfoText("Player " + playerIndex + " suggested " +
                    actionTaken.suggestion[0].cardName + ", " +
                    actionTaken.suggestion[1].cardName + ", " +
                    actionTaken.suggestion[2].cardName);
        else if(actionTaken.actionType.equals("accuse"))
            setInfoText("Player " + playerIndex + " accused " +
                    actionTaken.accusation[0].cardName + ", " +
                    actionTaken.accusation[1].cardName + ", " +
                    actionTaken.accusation[2].cardName);
        else if(actionTaken.actionType.equals("showCard"))
            setInfoText("Player " + playerIndex + " showed Player " +
                    ((Agent)actionTaken.shownTo).playerIndex + " " + actionTaken.cardShown.cardName);
        cl.show(this, infoCard);
    }
}
