/*
 * DebugFrame.java
 * New change
 * Created on 2008. május 6., 12:03
 */

package mcts.game.catan.display;

import mcts.game.Game;
import mcts.game.GameFactory;
import mcts.game.catan.Catan;
import mcts.game.catan.CatanConfig;

/**
 *
 * @author  szityu, sorinMD
 */
public class DebugFrame extends javax.swing.JFrame {

	public GameFactory gameFactory;
    public Game boardlayout;
    
    /** Creates new form DebugFrame */
    public DebugFrame() {
        initComponents();
        
        settlersPanel1.setBoardLayoutSize();
        gameFactory = new GameFactory(new CatanConfig(), null);
        Catan.initBoard();
        boardlayout = gameFactory.getNewGame();//new BoardLayout(settlersPanel1.getWidth(),settlersPanel1.getHeight());
        settlersPanel1.SetBoardLayout(boardlayout);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settlersPanel1 = new SettlersPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settlersPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 607, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(settlersPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DebugFrame().setVisible(true); //quick test
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private SettlersPanel settlersPanel1;
    // End of variables declaration//GEN-END:variables
 
    public void setbl(Game bl){
    	boardlayout = bl;
    	settlersPanel1.SetBoardLayout(bl);
    }
    
}