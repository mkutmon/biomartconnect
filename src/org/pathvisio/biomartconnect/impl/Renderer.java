package org.pathvisio.biomartconnect.impl;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

public class Renderer implements TableCellRenderer {
	
	TableModel dataModel =  null;
	
	public Renderer(TableModel dataModel){
		this.dataModel = dataModel;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
		
	    JTextField editor = new JTextField();
	    if (value != null)
	      editor.setText(value.toString());
	    if(dataModel.getColumnName(column).equals("Associated Gene Name")){
		    editor.setBackground((row % 2 == 0) ? Color.white : Color.cyan);
	    }
	    return editor;
	    
	}
}