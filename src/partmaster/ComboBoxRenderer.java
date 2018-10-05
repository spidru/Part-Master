package partmaster;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;

public class ComboBoxRenderer extends JLabel
    implements ListCellRenderer
{
    private final JComboBox cb;
    
    public ComboBoxRenderer(JComboBox cb)
    {
        this.cb = cb;
        setOpaque(false);
//        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
        Image img = FileHandler.getInstance().getImage(FileHandler.SEARCH_ICON);
        img = img.getScaledInstance(10,10,Image.SCALE_SMOOTH);
        setIcon(new ImageIcon(img));
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        final String[] filterSearchNames = {"All","Name","Description","Package","Stock"};
        int selectedIndex = ((Integer)value).intValue();
		
        setOpaque(false);
        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            if (cb.isPopupVisible())
                setOpaque(true);
//            try {
//                Image img = ImageIO.read(getClass().getClassLoader().getResource("search-icon.gif"));
//                img = img.getScaledInstance(10,10,Image.SCALE_SMOOTH);
//                setIcon(new ImageIcon(img));
//            } catch (IOException ex) {
//                Logger.getLogger(ComboBoxRenderer.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setText(filterSearchNames[selectedIndex]);
        return this;
    }
    
}
