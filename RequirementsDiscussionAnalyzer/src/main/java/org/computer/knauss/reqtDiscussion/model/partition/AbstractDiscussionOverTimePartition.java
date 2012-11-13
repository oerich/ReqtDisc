package org.computer.knauss.reqtDiscussion.model.partition;

import java.sql.Date;
import java.util.List;
import java.util.Vector;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.ModelElement;

public abstract class AbstractDiscussionOverTimePartition implements
		IDiscussionOverTimePartition {

	private ModelElement[] modelElements;

	@Override
	public void setModelElements(ModelElement[] comments) {
		this.modelElements = comments;
	}

	@Override
	public ModelElement[] getModelElements() {
		return this.modelElements;
	}

	@Override
	public abstract int getPartitionForModelElement(ModelElement wc);

	@Override
	public ModelElement[] getModelElementsForPartition(int partition) {
		List<ModelElement> tmp = new Vector<ModelElement>();

		for (ModelElement wc : this.modelElements) {
			if (partition == getPartitionForModelElement(wc))
				tmp.add(wc);
		}

		return tmp.toArray(new ModelElement[0]);
	}

	@Override
	public void setTimeInterval(ModelElement[] selectedElements) {
		Date firstDate = new Date(Long.MAX_VALUE);
		Date lastDate = new Date(0);
		for (ModelElement d : selectedElements) {
			if (firstDate.after(d.getCreationDate()))
				firstDate = d.getCreationDate();
			if (lastDate.before(d.getCreationDate()))
				lastDate = d.getCreationDate();
			if (d instanceof Discussion)
				for (DiscussionEvent de : ((Discussion) d)
						.getDiscussionEvents()) {
					if (lastDate.before(de.getCreationDate()))
						lastDate = de.getCreationDate();
				}
		}
		setTimeInterval(firstDate, lastDate);
	}

	@Override
	public boolean isInClass(ModelElement me) {
		return ((DiscussionEvent) me).isInClass();
	}

	@Override
	public boolean isClassified(ModelElement me) {
		return ((DiscussionEvent) me).isClassified();
	}
}
