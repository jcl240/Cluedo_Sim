import javax.swing.*;
import java.awt.*;

public class TransparentButton extends JButton {
    public TransparentButton(String next) {
        this.setText(next);
        this.setOpaque(false);
        this.setFont(new Font("Serif", Font.BOLD, 16));
        this.setForeground(new Color(255, 237, 211));
        this.setBackground(new Color(43, 30, 25));
    }

    public TransparentButton(ImageIcon imageIcon){
        super(imageIcon);
        this.setOpaque(false);
        this.setBackground(new Color(43, 30, 25));
    }

    @Override
    public void paint(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1));
        super.paint(g2);
        g2.dispose();
    }
}
