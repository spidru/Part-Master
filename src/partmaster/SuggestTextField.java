package partmaster;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SuggestTextField extends JTextField
{
    private List<String> items;
    private JComboBox cb;
    private DefaultComboBoxModel model;
    private Object suggestionData;
    
    public SuggestTextField()
    {
        model = new DefaultComboBoxModel();
        cb = new JComboBox(model)
        {
            @Override
            public Dimension getPreferredSize()
            {
                return new Dimension(super.getPreferredSize().width,0);
            }
        };
        setAdjusting(cb,false);

        cb.setSelectedItem(null);
        cb.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (!isAdjusting(cb))
                {
                    if (cb.getSelectedItem() != null)
                        setText(cb.getSelectedItem().toString());
                }
            }
        });
        cb.setRenderer(new DefaultListCellRenderer()
        {
            @Override
            public java.awt.Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null)
                {
//                    this.setText(value.toString());
//                    if (c.getFootprint() != null)
//                        this.setText("<html><b>"+c.getName()+"</b><br>"+c.getDescription()+"</html>");
//                    else
//                        this.setText("<html><b>"+c.getName()+"</b></html>");
                }
                return this;
            }
        });
        addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                int code = e.getKeyCode();
                setAdjusting(cb,true);
                if (code == KeyEvent.VK_SPACE)
                {
                    if (cb.isPopupVisible())
                        e.setKeyCode(KeyEvent.VK_SPACE);
                }
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN)
                {
                    e.setSource(cb);
                    cb.dispatchEvent(e);
                    if (code == KeyEvent.VK_DOWN)
                    {
                        cb.setPopupVisible(true);
                    }
                    if (code == KeyEvent.VK_ENTER)
                    {
                        setText(cb.getSelectedItem().toString());
                        cb.setPopupVisible(false);
                    }
                }
                if (code == KeyEvent.VK_ESCAPE)
                    cb.setPopupVisible(false);
                setAdjusting(cb,false);
            }
        });
        getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                updateList();
            }
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                updateList();
            }
            @Override
            public void changedUpdate(DocumentEvent e)
            {
                updateList();
            }
            
            private void updateList()
            {
                setAdjusting(cb,true);
//                model.removeAllElements();
                String text = getText();
                if (!text.isEmpty() && items!=null)
                {
                    for (String item : items)
                    {
                        if (item.toLowerCase().startsWith(text.toLowerCase()))
                            model.addElement(item);
                    }
                }
                cb.setPopupVisible(model.getSize() > 0);
                setAdjusting(cb,false);
            }
        });
        
        setLayout(new BorderLayout());
        add(cb,BorderLayout.SOUTH);
    }
    
    private static boolean isAdjusting(JComboBox cb)
    {
        if (cb.getClientProperty("is_adjusting") instanceof Boolean)
        {
            return (Boolean) cb.getClientProperty("is_adjusting");
        }
        return false;
    }
    
    private static void setAdjusting(JComboBox cb, boolean adjusting)
    {
        cb.putClientProperty("is_adjusting", adjusting);
    }
    
    public void updateItems(List<Component> cmps)
    {
        setAdjusting(cb,true);
        suggestionData = (Object) cmps;
        model.removeAllElements();
        for (Component c : cmps)
            model.addElement(c.getName());
        
//        SwingUtilities.invokeLater(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                cb.setPopupVisible(true);
//                cb.repaint();
//            }
//        });
        cb.setPopupVisible(true);
        setAdjusting(cb,false);
    }
}
