package partmaster;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author AVLAB-USER3
 */
public class GridbagHelper
{
    public static void addComponent(Component cmp, JFrame jf, GridBagConstraints gbc, int weightx, int weighty, int gridwidth, int gridheight, int anchor, int fill, Insets i)
    {
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.gridheight = gridheight;
        gbc.gridwidth = gridwidth;
//        gbc.gridx = gridx;
//        gbc.gridy = gridy;
        gbc.insets = i;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        
        jf.add(cmp,gbc);
    }

    
    public static void addComponent(Component cmp, JFrame frame, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty, int anchor, int fill)
    {
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridheight = gridheight;
        gbc.gridwidth = gridwidth;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        frame.add(cmp,gbc);
    }
    
    public static void addComponent(Component cmp, JComponent jcmp, GridBagConstraints gbc, int gridx, int gridy, int gridwidth, int gridheight, int weightx, int weighty, int anchor, int fill)
    {
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.gridheight = gridheight;
        gbc.gridwidth = gridwidth;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        jcmp.add(cmp,gbc);
    }
}
