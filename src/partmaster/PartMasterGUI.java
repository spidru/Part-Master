package partmaster;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.ToolTipBalloonStyle;

public class PartMasterGUI
    implements ActionListener, ComponentListener, WindowListener
{
	private static PartMasterGUI instance = null;
	
    private final String[] columnNames = {"Name","Description","Package","Stock"};
    public final String[] filterSearchNames = {"All","Name","Description","Package","Stock"};
    private final JButton addComponentButton = new JButton("Add Component");
    private final JButton deleteComponentButton = new JButton("Delete Component");
//    private final JButton filterSearchButton = new JButton("All");
    private JComboBox filterSearchBox = null;// = new JComboBox(filterSearchNames);
    private final JSearchTextField searchField = new JSearchTextField();
    
    private InventoryTable table;
    private DefaultTableModel model;
    JScrollPane scrollPane;
    
    private BalloonTip bt;
	
    public static PartMasterGUI getInstance()
    {
        if (instance == null)
                instance = new PartMasterGUI();
        return instance;
    }
    
    public void loadUI()
    {
        /* Look And Feel */
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
            //@url http://www.javasoft.de/synthetica/customize/
        }
//        catch (ParseException|UnsupportedLookAndFeelException e)
        catch(ClassNotFoundException|InstantiationException|IllegalAccessException|UnsupportedLookAndFeelException e)
        {
            Logger.println("Cannot load L&F");
        }
        
        /* Frame */
        final JFrame frame = new JFrame("Part Master " + PartMaster.VERSION);
        frame.setVisible(true);
        frame.setSize(800,600); // TODO Replace with frame.pack()?
        try
        {
            Image img = ImageIO.read(getClass().getClassLoader().getResource(FileHandler.MAIN_ICON));
            frame.setIconImage(img);
        }
        catch (IOException e) {Logger.println("Could not load frame icon");}
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        
        /* Menu Bar */
        JMenuBar menu = new JMenuBar();
        JMenu octopartMenu = new JMenu("Octopart");
        JMenuItem setApiKeyItem = new JMenuItem("Set API Key...");
        octopartMenu.add(setApiKeyItem);
        menu.add(octopartMenu);
        frame.setJMenuBar(menu);
        setApiKeyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = (String) JOptionPane.showInputDialog(
                        frame,
                        "Enter an Octopart API key",
                        "Octopart API Key",
                        JOptionPane.PLAIN_MESSAGE,
                        null,//icon
                        null,// to show a text field
                        null
                    );
                if (s != null)
                {
                    Octopart.setApiKey(s);
                }
            }
        });
        
        /* Add Component Button */
        try
        {
            Image img = ImageIO.read(getClass().getClassLoader().getResource(FileHandler.ADD_ICON));
            img = img.getScaledInstance(addComponentButton.getPreferredSize().height/2,addComponentButton.getPreferredSize().height/2,Image.SCALE_SMOOTH);
            addComponentButton.setIcon(new ImageIcon(img));
        }
        catch (IOException e) {Logger.println("Could not load Add Component icon");}
        addComponentButton.setPreferredSize(addComponentButton.getPreferredSize());
        GridbagHelper.addComponent(addComponentButton,frame,gbc,0,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE);
        
        /* Delete Component Button */
        try
        {
            Image img = ImageIO.read(getClass().getClassLoader().getResource(FileHandler.DELETE_ICON));
            img = img.getScaledInstance(deleteComponentButton.getPreferredSize().height/2,deleteComponentButton.getPreferredSize().height/2,Image.SCALE_SMOOTH);
            deleteComponentButton.setIcon(new ImageIcon(img));
        }
        catch (IOException e) {Logger.println("Could not load Delete Component icon");}
        deleteComponentButton.setPreferredSize(addComponentButton.getPreferredSize());
        GridbagHelper.addComponent(deleteComponentButton,frame,gbc,1,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE);

        /* Search Filter ComboBox */
        Integer[] intArray = new Integer[filterSearchNames.length];
        for (int i=0; i<intArray.length; i++)
        {
            intArray[i] = i;
        }
        filterSearchBox = new JComboBox(intArray);
        ComboBoxRenderer renderer = new ComboBoxRenderer(filterSearchBox);
        filterSearchBox.setRenderer(renderer);
        filterSearchBox.setPreferredSize(addComponentButton.getPreferredSize());
        GridbagHelper.addComponent(filterSearchBox,frame,gbc,2,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE);
		
        /* Search Filter TextField */
        searchField.setPreferredSize(filterSearchBox.getPreferredSize());
        GridbagHelper.addComponent(searchField,frame,gbc,3,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
        
        /**********************************************************************/
        /* Table */
        
//        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1);
//        frame.add(new JSpinner(spinnerModel));
        
        table = new InventoryTable();
        scrollPane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        GridbagHelper.addComponent(scrollPane,frame,gbc,0,1,4,1,1,1,GridBagConstraints.FIRST_LINE_START,GridBagConstraints.BOTH);
        /**********************************************************************/
        
        /* Assign Listeners */
        frame.addWindowListener((WindowListener)this);
        addComponentButton.addActionListener((ActionListener)this);
        deleteComponentButton.addActionListener((ActionListener)this);
        
        filterSearchBox.addActionListener((ActionListener)this);

        /* Need UI update for L&F */
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                
                updateUI();
            }
        });
        table.redraw();
    }
    
    private void updateUI()
    {
        addComponentButton.updateUI();
        deleteComponentButton.updateUI();
        filterSearchBox.updateUI();
        searchField.updateUI();
        table.updateUI();
    }

    public void searchRecords()
    {
//		DefaultTableModel model = PartMasterGUI.getInstance().getTableModel();
        int filterCategory = PartMasterGUI.getInstance().getSearchFilterCategory();
        String text = searchField.getText().toUpperCase();
        model.setRowCount(0);
        for (int i=0; i<Component.getList().size(); i++)
        {
            if (filterCategory == 0 || filterCategory == 1)
            {
                String name = Component.getList().get(i).getName().toUpperCase();
                if (name.contains(text))
                {
                    model.addRow(Component.getComponentObject(i));
                    continue;
                }
            }
            if (filterCategory == 0 || filterCategory == 2)
            {
                String desc = Component.getList().get(i).getDescription().toUpperCase();
                if (desc.contains(text))
                {
                    model.addRow(Component.getComponentObject(i));
                    continue;
                }
            }
            if (filterCategory == 0 || filterCategory == 3)
            {
                String footprint = Component.getList().get(i).getFootprint();
                if (footprint == null)
                    continue;
                if (footprint.contains(text))
                {
                    model.addRow(Component.getComponentObject(i));
                    continue;
                }
            }
            if (filterCategory == 0 || filterCategory == 4)
            {
                String qty = Integer.toString(Component.getList().get(i).getQuantity());
                if (qty.equals(text))
                {
                    model.addRow(Component.getComponentObject(i));
//					continue;
                }
            }
        }
    }
    
    /* Action Event Handling */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == addComponentButton)
        {
            AddComponentForm acf = new AddComponentForm();
//            BalloonTipStyle bts = new EdgedBalloonStyle(Color.yellow, Color.yellow);
            BalloonTipStyle bts = new ToolTipBalloonStyle(Color.gray, Color.gray);
            bt = new BalloonTip(addComponentButton,acf,bts,true);
            bt.setCloseButton(BalloonTip.getDefaultCloseButton(),false);
            acf.addComponentListener((ComponentListener)this);
            acf.requestFocusInWindow();
            acf.initialiseCaret();
        }
        else if (ae.getSource() == deleteComponentButton)
        {
            int selRows[] = table.getSelectedRows();
            
            for (int i = selRows.length - 1; i >= 0; i--)
            {
                String name = (String) table.getValueAt(selRows[i], 0);
                Component cmp = Component.getComponentByName(name);
                Component.getList().remove(cmp);
//                Logger.println("Deleting: "+Component.getList().get(selRows[i]).getName());
            }
            table.redraw();
        }
        else if (ae.getSource() == filterSearchBox)
        {
            searchRecords();
        }
    }
	
    public int getSearchFilterCategory()
    {
            return filterSearchBox.getSelectedIndex();
    }
    
    /* Window Event Handling */
    @Override
    public void windowClosing(WindowEvent we)
    {
        FileHandler.saveDataFile(PartMaster.getDataFile());
        Config.save();
        Logger.println("Exiting program");
        Logger.flush();
        System.exit(0);
    }
    @Override
    public void windowOpened(WindowEvent we) {table.redraw();}    
    @Override
    public void windowClosed(WindowEvent we) {}
    @Override
    public void windowIconified(WindowEvent we) {}
    @Override
    public void windowDeiconified(WindowEvent we) {}
    @Override
    public void windowActivated(WindowEvent we) {}
    @Override
    public void windowDeactivated(WindowEvent we) {}

    @Override
    public void componentResized(ComponentEvent e) {
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        bt.setVisible(false);
        table.redraw();
    }
}