package partmaster;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

/**
 *
 * @author Andrew Spiteri
 */
public class JSearchTextField extends JIconTextField implements FocusListener, KeyListener
{
    private final String defaultText;
    
    public JSearchTextField()
    {
        super();
        this.defaultText = "Search";
        this.addFocusListener((FocusListener)this);
        
        // TODO Move code below to JIconTextField
        Image img = FileHandler.getInstance().getImage(FileHandler.SEARCH_ICON);
        img = img.getScaledInstance(10,10,Image.SCALE_SMOOTH);
        this.setIcon(new ImageIcon(img));
        this.addKeyListener((KeyListener)this);
        this.setFocusable(true);
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (!this.hasFocus() && this.getText().equals(""))
        {
            int width = this.getWidth();
            int height = this.getHeight();
            Font prev = g.getFont();
            Font italic = prev.deriveFont(Font.ITALIC);
            Color prevColor = g.getColor();
            g.setFont(italic);
            g.setColor(UIManager.getColor("textInactiveText"));
            int h = g.getFontMetrics().getHeight();
            int textBottom = (height-h)/2 + h-2;
            int x = this.getInsets().left;
            Graphics2D g2d = (Graphics2D) g;
            RenderingHints hints = g2d.getRenderingHints();
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2d.drawString(defaultText, x, textBottom);
            g2d.setRenderingHints(hints);
            g.setFont(prev);
            g.setColor(prevColor);
        }
    }
    
    @Override
    public void focusGained(FocusEvent e)
    {
        this.repaint();
    }
    @Override
    public void focusLost(FocusEvent e)
    {
       this.repaint();
    }

    @Override
    public void keyTyped(KeyEvent ke) {}
    @Override
    public void keyPressed(KeyEvent ke) {}
    @Override
    public void keyReleased(KeyEvent ke)
    {
            PartMasterGUI.getInstance().searchRecords();
    }
}
