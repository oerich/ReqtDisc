package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.model.IFilteredWorkitemCommentList;
import org.computer.knauss.reqtDiscussion.model.WorkitemCommentTagFilter;

public class AddFilterCommand extends AbstractCommand {

	private static final long serialVersionUID = 1L;
	private IFilteredWorkitemCommentList list;

	public AddFilterCommand(IFilteredWorkitemCommentList list) {
		super("Add filter");

		this.list = list;
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String input = JOptionPane
				.showInputDialog("Please enter a substring. Workitemcomments in a class that contains this string are filtered out.");

		if (input != null)
			this.list.addFilter(new WorkitemCommentTagFilter(input));
	}

}
