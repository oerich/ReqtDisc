package org.computer.knauss.reqtDiscussion.model.partition;

import java.sql.Date;
import java.util.List;
import java.util.Vector;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public abstract class AbstractDiscussionOverTimePartition implements
		IDiscussionOverTimePartition {

	private DiscussionEvent[] discussionEvents;

	@Override
	public void setDiscussionEvents(DiscussionEvent[] comments) {
		this.discussionEvents = comments;
	}

	@Override
	public DiscussionEvent[] getDiscussionEvents() {
		return this.discussionEvents;
	}

	@Override
	public abstract int getPartitionForDiscussionEvent(DiscussionEvent wc);

	@Override
	public DiscussionEvent[] getDiscussionEventForPartition(int partition) {
		List<DiscussionEvent> tmp = new Vector<DiscussionEvent>();

		for (DiscussionEvent wc : this.discussionEvents) {
			if (partition == getPartitionForDiscussionEvent(wc))
				tmp.add(wc);
		}

		return tmp.toArray(new DiscussionEvent[0]);
	}

	@Override
	public boolean isInClass(DiscussionEvent wic) {
		return (wic.getReferenceClassification() != null && wic
				.getReferenceClassification().toLowerCase()
				.startsWith("clarif"));
	}

	@Override
	public void setTimeInterval(Discussion[] selectedDiscussions) {
		Date firstDate = new Date(Long.MAX_VALUE);
		Date lastDate = new Date(0);
		for (Discussion d : selectedDiscussions) {
			if (firstDate.after(d.getCreationDate()))
				firstDate = d.getCreationDate();
			for (DiscussionEvent de : d.getDiscussionEvents()) {
				if (lastDate.before(de.getCreationDate()))
					lastDate = de.getCreationDate();
			}
		}
		setTimeInterval(firstDate, lastDate);
	}
}
