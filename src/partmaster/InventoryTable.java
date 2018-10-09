/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package partmaster;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author spidru
 */
public class InventoryTable extends JTable implements TableModelListener
{
    private DefaultTableModel model;
    
    private final String[] columnNames =
    {
        "Name",
        "Description",
        "Package",
        "Stock"
    };
    
    public InventoryTable()
    {
        model = new DefaultTableModel(columnNames, 0);
        super.setModel(model);
        
        // add sorting functionality (sort by name ascending by default)
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(super.getModel());
        super.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        int defaultColumnIndexToSort = 0;
        sortKeys.add(new RowSorter.SortKey(defaultColumnIndexToSort, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);
        sorter.sort();
        
        TableColumnModel tcm = super.getColumnModel();
        TableColumn tc = tcm.getColumn(3);
        tc.setCellEditor(new SpinnerEditor());
        
        // Tweak size
        super.setRowHeight(20);
        super.getColumnModel().getColumn(1).setMinWidth(200);
        super.getColumnModel().getColumn(3).setMaxWidth(50);        
    }
    
    public DefaultTableModel getTableModel()
    {
        return model;
    }
    
    public void redraw()
    {
        // temporarily disable listener (otherwise exceptions are thrown by tableChanged())
        model.removeTableModelListener((TableModelListener)this);
        // update model
        model.setRowCount(0);   
        for (int i = 0; i < Component.getList().size(); i++)
        {
            model.addRow(Component.getComponentObject(i));
        }
        model.addTableModelListener((TableModelListener)this);
        // trigger a call to tableChanged()
        model.fireTableDataChanged();
    }
    
//    @Override
//    public java.awt.Component prepareRenderer(TableCellRenderer renderer, int row, int column)
//    {
//        java.awt.Component component = super.prepareRenderer(renderer, row, column);
//        int rendererWidth = component.getPreferredSize().width;
//        TableColumn tableColumn = getColumnModel().getColumn(column);
//        tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));
//        return component;
//    }
    
    /* Table Event Handling */
    @Override
    public void tableChanged(TableModelEvent tme)
    {
        super.tableChanged(tme);
        int col = tme.getColumn();
        int row = tme.getFirstRow();
        switch (tme.getColumn())
        {
            case 0:
                Component.getList().get(row).setName(model.getValueAt(row,col).toString());
                break;
            case 1:
                Component.getList().get(row).setDescription(model.getValueAt(row,col).toString());
                break;
            case 2:
                Component.getList().get(row).setFootprint(model.getValueAt(row,col).toString());
                break;
            case 3:
                Component.getList().get(row).setStock(Integer.parseInt(model.getValueAt(row,col).toString()));
                break;
            default:
//				Logger.println("Unsupported table change event occured");
                break;
        }
    }
    
    public static class SpinnerEditor extends DefaultCellEditor
    {
        JSpinner spinner;
        JSpinner.DefaultEditor editor;
        JTextField textField;
        boolean valueSet;

        // Initializes the spinner.
        public SpinnerEditor() {
            super(new JTextField());
            spinner = new JSpinner();
            editor = ((JSpinner.DefaultEditor)spinner.getEditor());
            textField = editor.getTextField();
            textField.addFocusListener( new FocusListener() {
                @Override
                public void focusGained( FocusEvent fe ) {
                    //textField.setSelectionStart(0);
                    //textField.setSelectionEnd(1);
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            if ( valueSet ) {
                                textField.setCaretPosition(1);
                            }
                        }
                    });
                }
                @Override
                public void focusLost( FocusEvent fe ) {
                }
            });
            textField.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent ae ) {
                    stopCellEditing();
                }
            });
        }

        // Prepares the spinner component and returns it.
        @Override
        public java.awt.Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column
        ) {
            if ( !valueSet ) {
                spinner.setValue(value);
            }
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    textField.requestFocus();
                }
            });
            return spinner;
        }

        @Override
        public boolean isCellEditable( EventObject eo ) {
            if ( eo instanceof KeyEvent ) {
                KeyEvent ke = (KeyEvent)eo;
                System.err.println("key event: "+ke.getKeyChar());
                textField.setText(String.valueOf(ke.getKeyChar()));
                //textField.select(1,1);
                //textField.setCaretPosition(1);
                //textField.moveCaretPosition(1);
                valueSet = true;
            } else {
                valueSet = false;
            }
            return true;
        }

        // Returns the spinners current value.
        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }

        @Override
        public boolean stopCellEditing()
        {
            try
            {
                editor.commitEdit();
                spinner.commitEdit();
            } catch ( java.text.ParseException e ) {
                JOptionPane.showMessageDialog(null,
                    "Invalid value, discarding.");
            }
            return super.stopCellEditing();
        }
    }
}
