package org.computer.knauss.reqtDiscussion.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import org.computer.knauss.reqtDiscussion.ui.uiModel.PropertiesTableModel;

public class EditPropertiesFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private PropertiesTableModel tableModel;
	private JFileChooser fileChooser;

	public EditPropertiesFrame() {
		super("Edit data source properties");
		setLayout(new BorderLayout());

		this.tableModel = new PropertiesTableModel();

		add(new JScrollPane(new JTable(this.tableModel)), BorderLayout.CENTER);

		JTextArea helpLabel = new JTextArea();
		helpLabel.setEditable(false);
		// helpLabel.setBackground(Color.WHITE);
		// helpLabel.setForeground(Color.WHITE);
		helpLabel.setWrapStyleWord(true);
		helpLabel.setLineWrap(true);
		helpLabel.setColumns(60);
		helpLabel
				.setText("Here you can edit the properties of the data source."
						+ "Entries in the third column highlight missing values."
						+ "Remember that you can always choose a different data "
						+ "source. Just close this window, when the changes are done.");

		JPanel spacer = new JPanel();
		spacer.setBackground(Color.WHITE);
		spacer.setForeground(Color.WHITE);
		spacer.add(helpLabel);
		add(spacer, BorderLayout.NORTH);

		JPanel buttonPanel = new JPanel();
		add(buttonPanel, BorderLayout.SOUTH);
		JButton loadButton = new JButton("Load");
		loadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int i = getFileChooser().showOpenDialog(
						EditPropertiesFrame.this);
				if (i == JFileChooser.APPROVE_OPTION) {
					try {
						tableModel.getProperties().load(
								new FileInputStream(getFileChooser()
										.getSelectedFile()));
						tableModel.fireTableChanged();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int i = getFileChooser().showSaveDialog(
						EditPropertiesFrame.this);
				if (i == JFileChooser.APPROVE_OPTION) {
					try {
						tableModel.getProperties().store(
								new FileOutputStream(getFileChooser()
										.getSelectedFile()), "");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		buttonPanel.add(loadButton);
		buttonPanel.add(saveButton);

		pack();
	}

	private JFileChooser getFileChooser() {
		if (this.fileChooser == null)
			this.fileChooser = new JFileChooser();
		return this.fileChooser;
	}

	public void setProperties(Properties p) {
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
