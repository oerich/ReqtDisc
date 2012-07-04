package org.computer.knauss.reqtDiscussion.model.clarificationPatterns;

import java.util.List;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class DiscussionListModel implements ListModel {
	private List<ListDataListener> listeners = new Vector<ListDataListener>();

	private DiscussionEvent[] comments;

	@Override
	public void addListDataListener(ListDataListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public Object getElementAt(int row) {
		String content = this.comments[row].getContent();
		if (content.length() > 15) {
			content = content.substring(0, 12) + "...";
		}
		return content;
	}

	@Override
	public int getSize() {
		if (this.comments == null)
			return 0;
		return this.comments.length;
	}

	@Override
	public void removeListDataListener(ListDataListener listener) {
		this.listeners.remove(listener);
	}

	public void setComments(DiscussionEvent[] comments) {
		this.comments = comments;
		for (ListDataListener l : this.listeners) {
			l.contentsChanged(new ListDataEvent(this,
					ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
		}
	}

}
