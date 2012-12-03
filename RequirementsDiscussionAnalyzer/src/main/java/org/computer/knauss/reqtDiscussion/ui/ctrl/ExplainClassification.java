package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;

public class ExplainClassification extends AbstractCommand {

	private static final long serialVersionUID = 1L;
	private JFrame messageFrame;
	private JTable table;

	public ExplainClassification() {
		super("Explain classifier");
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		TableModel model = ClassifierManager.getInstance().getClassifier()
				.explainClassification(null);
		if (model == null)
			return;

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				model);
		getTable().setRowSorter(sorter);
		getTable().setModel(model);

		getFrame().setVisible(true);
	}

	private JFrame getFrame() {
		if (this.messageFrame == null) {
			this.messageFrame = new JFrame("Explanation");
			this.messageFrame.add(new JScrollPane(getTable()));
			this.messageFrame.setSize(300, 300);
			this.messageFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			this.messageFrame.pack();
		}
		return this.messageFrame;
	}

	private JTable getTable() {
		if (this.table == null) {
			this.table = new JTable();
		}
		return this.table;
	}
}
