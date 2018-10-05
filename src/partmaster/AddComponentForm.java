package partmaster;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AddComponentForm extends JPanel
    implements ActionListener, KeyListener
{
    private final JLabel nameLabel = new JLabel("Name");
    private final SuggestTextField nameField = new SuggestTextField();
    private final JButton addButton = new JButton("Add");
    private JCheckBox toggleBox = new JCheckBox("Search Online",true);
    private JLabel statusLabel = new JLabel("Checking connection...");
    
    private String lastText;
    
    private Thread searchThread = null;
    private PartSearch ps;
	   
    public AddComponentForm()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets =  new Insets(10,10,10,10);
        GridbagHelper.addComponent(nameLabel,(JPanel)this,gbc,0,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE);
        nameField.setColumns(30);
        GridbagHelper.addComponent(nameField,(JPanel)this,gbc,1,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
        GridbagHelper.addComponent(toggleBox,(JPanel)this,gbc,2,0,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
        GridbagHelper.addComponent(addButton,(JPanel)this,gbc,1,1,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE);
        
        toggleBox.setEnabled(false);
        toggleBox.setSelected(false);
        
        /* Label for search engine status */
        Thread connectThread = new Thread()
        {
            @Override
            public void run()
            {
                Octopart.Status status = Octopart.ping();
                if (status == Octopart.Status.OK)
                {
                    statusLabel.setText("Connection established successfully");
                    toggleBox.setEnabled(true);
                    toggleBox.setSelected(true);
                }
                else if (status == Octopart.Status.INVALID_KEY)
                {
                    statusLabel.setText("Invalid API key");
                    toggleBox.setEnabled(false);
                    toggleBox.setSelected(false);
                }
                else
                {
                    statusLabel.setText("Connection could not be established");
                    toggleBox.setEnabled(false);
                    toggleBox.setSelected(false);
                }
            }
        };
        connectThread.start();
        GridbagHelper.addComponent(statusLabel,(JPanel)this,gbc,1,2,1,1,0,0,GridBagConstraints.CENTER,GridBagConstraints.NONE);

        /* Add Listeners */
        nameField.addKeyListener((KeyListener)this);
        addButton.addActionListener((ActionListener)this);
    }
	
    /* Action Event Handling */
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        if (ae.getSource() == addButton)
        {
            Component cmp;
            if (toggleBox.isSelected())
            {
//                List<String> data = ps.searchFor(nameField.getText());
                cmp = ps.getFoundComponentByName(nameField.getText());
                if (cmp == null)
                    cmp = new Component(nameField.getText());
//                cmp = new Component(nameField.getText(),);
            }
            else
                cmp = new Component(nameField.getText());
            Component.addComponent(cmp);
            dispatchEvent(new ComponentEvent(this,ComponentEvent.COMPONENT_HIDDEN));
        }
    }

    /* Key Event Handling */
    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if (searchThread != null && searchThread.isAlive())
        {
            ps.kill();
            searchThread.interrupt();
//            Logger.println("Stopping ongoing search");
        }
        if (!nameField.getText().equals(lastText) && (e.getKeyCode()!=KeyEvent.VK_ENTER) && toggleBox.isSelected())
        {
            searchThread = new Thread()
            {
                @Override
                public void run() {
                    ps = new PartSearch(nameField.getText());
                    ps.run();
                    if (ps.getSearchResults() != null)
                    {
                        Logger.println("Searching for: "+nameField.getText());
                        for (int i=0; i<ps.getSearchResults().size(); i++)
                        {
//                            Logger.print(ps.getSearchResults().get(i));
//                            Logger.print("|");
                        }
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                nameField.updateItems(ps.getFoundComponents());
                            }
                        });
                        
                    }
                }
            };
            searchThread.start();
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER)
        {
                Logger.println("Enter pressed");			
        }
        lastText = nameField.getText();
    }
    
    /**
     * Gives focus and initialises the caret position in the search field.
     */
    public void initialiseCaret()
    {
        nameField.requestFocusInWindow();
        nameField.setCaretPosition(0);
    }
}