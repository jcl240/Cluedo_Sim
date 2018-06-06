import javax.swing.*;
import java.awt.*;

public class NextButton extends JButton {
    public NextButton(String next) {
        this.setText(next);
        this.setOpaque(false);
        this.setFont(new Font("Serif", Font.BOLD, 26));
        this.setForeground(new Color(255, 237, 211));
        this.setBackground(new Color(43, 30, 25));
    }
    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 0.75));
        super.paint(g2);
        g2.dispose();
    }
}
