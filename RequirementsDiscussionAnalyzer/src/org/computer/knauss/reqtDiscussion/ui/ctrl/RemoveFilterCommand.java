package org.computer.knauss.reqtDiscussion.ui.ctrl;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.computer.knauss.reqtDiscussion.model.IDiscussionEventFilter;
import org.computer.knauss.reqtDiscussion.model.IFilteredDiscussionEventList;

public class RemoveFilterCommand extends AbstractCommand {

	private static final long serialVersionUID = 1L;
	private IFilteredDiscussionEventList list;

	public RemoveFilterCommand(IFilteredDiscussionEventList list) {
		super("Remove filter");

		this.list = list;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object filter = JOptionPane.showInputDialog(null,
				"Please select the filter that should be removed.",
				"Remove Filter", JOptionPane.QUESTION_MESSAGE, null,
				this.list.getFilters(), null);
		this.list.removeFilter((IDiscussionEventFilter) filter);
	}

}
