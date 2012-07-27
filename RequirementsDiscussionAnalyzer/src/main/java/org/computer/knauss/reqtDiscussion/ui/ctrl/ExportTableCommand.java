package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.table.TableModel;

public class ExportTableCommand extends AbstractCommand {

	private static final String DELIMITER = ";";
	private TableModel tableModel;

	public ExportTableCommand() {
		super("Export Table");
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void actionPerformed(ActionEvent event) {
		// get the filename from the user
		JFileChooser fc = new JFileChooser();
		int f = fc.showSaveDialog(null);
		if (f == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			
			try {
				// Write the column names
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < getTableModel().getColumnCount(); i++) {
					sb.append(getTableModel().getColumnName(i));
					sb.append(DELIMITER);
				}
				writer.write(sb.toString());
				writer.newLine();
				
				// Write the data
				for ( int row = 0; row < getTableModel().getRowCount(); row++ ) {
					sb = new StringBuffer();
					for (int col = 0; col < getTableModel().getColumnCount(); col++) {
						sb.append(getTableModel().getValueAt(row, col));
						sb.append(DELIMITER);
					}
					writer.write(sb.toString());
					writer.newLine();
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void setTableModel(TableModel model) {
		this.tableModel = model;
	}
	
	public TableModel getTableModel() {
		if (this.tableModel != null)
			return this.tableModel;
		return getDiscussionTableModel();
	}
}
