package org.computer.knauss.reqtDiscussion.model;

import java.util.List;
import java.util.Vector;

public abstract class ModelElement {

	private List<IModelElementListener> listeners = new Vector<IModelElementListener>();

	public void addModelElementListener(IModelElementListener l) {
		this.listeners.add(l);
	}

	public void removeModelElementListener(IModelElementListener l) {
		this.listeners.remove(l);
	}

	public void fireModelElementChanged(ModelElement me) {
		if (me == null)
			me = this;
		for (IModelElementListener l : this.listeners) {
			l.modelElementChanged(me);
		}
	}

	public abstract int getID();
}
