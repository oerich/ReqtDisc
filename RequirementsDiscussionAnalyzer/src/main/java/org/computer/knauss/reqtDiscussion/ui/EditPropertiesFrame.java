package org.computer.knauss.reqtDiscussion.ui;

import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.computer.knauss.reqtDiscussion.ui.uiModel.PropertiesTableModel;

public class EditPropertiesFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private PropertiesTableModel tableModel;

	public EditPropertiesFrame() {
		super("Edit data source properties");
		setLayout(new BorderLayout());

		this.tableModel = new PropertiesTableModel();

		add(new JScrollPane(new JTable(this.tableModel)), BorderLayout.CENTER);

		pack();
	}
	
	public void setProperties(Properties p ) {
		this.tableModel.setProperties(p);
	}
	
	public void setRemarks(Map<String, String> remarks) {
		this.tableModel.setRemarks(remarks);
	}

	public static void main(String[] args) {
		EditPropertiesFrame f = new EditPropertiesFrame();
		
		Properties p = new Properties();
		p.setProperty("Name", "oerich");
		
		Map<String, String> remarks = new HashMap<String, String>();
		remarks.put("Name", "Real name please");
		
		f.setProperties(p);
		f.setRemarks(remarks);
		
		f.pack();
		f.setVisible(true);
		
		System.out.println(p.getProperty("Name"));
	}
}
