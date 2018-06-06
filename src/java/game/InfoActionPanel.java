import javax.swing.*;
import java.awt.*;

public class InfoActionPanel extends JPanel {

    public InfoActionPanel() {
        this.setOpaque(false);
        this.setLayout(new CardLayout());
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        this.add(panel);
        JLabel label = new JLabel("Check");
        label.setFont(new Font("Serif", Font.BOLD, 32));
        label.setForeground(new Color(255, 237, 211));
        panel.add(label);
    }
}
