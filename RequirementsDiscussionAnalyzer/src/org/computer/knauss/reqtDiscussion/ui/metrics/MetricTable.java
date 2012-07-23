package org.computer.knauss.reqtDiscussion.ui.metrics;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractNetworkMetric;

public class MetricTable implements TableModel {

	private List<TableModelListener> listeners = new Vector<TableModelListener>();
	private List<AbstractDiscussionMetric> metrics = new LinkedList<AbstractDiscussionMetric>();
	private List<String> keys = new LinkedList<String>();

	public MetricTable() {
		Collections.addAll(this.metrics,
				AbstractDiscussionMetric.STANDARD_METRICS);
		Collections
				.addAll(this.metrics, AbstractDiscussionMetric.OTHER_METRICS);
		Collections
				.addAll(this.metrics, AbstractNetworkMetric.STANDARD_METRICS);
	}

	@Override
	public void addTableModelListener(TableModelListener l) {
		this.listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int col) {
		if (col == 0)
			return String.class;
		return Double.class;
	}

	@Override
	public int getColumnCount() {
		return this.metrics.size() + 1;
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0)
			return "ID";
		return this.metrics.get(col - 1).getName();
	}

	@Override
	public int getRowCount() {
		return this.keys.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (col == 0)
			return this.keys.get(row);
		return this.metrics.get(col - 1).getResults().get(this.keys.get(row));
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.listeners.remove(l);
	}

	@Override
	public void setValueAt(Object arg0, int row, int col) {

	}

	public void fireTableChanged() {
		this.keys.clear();
		this.keys.addAll(this.metrics.get(0).getResults().keySet());
		Collections.sort(this.keys);

		TableModelEvent tme = new TableModelEvent(this);
		for (TableModelListener l : this.listeners)
			l.tableChanged(tme);
	}

	public AbstractDiscussionMetric[] getMetrics() {
		return this.metrics.toArray(new AbstractDiscussionMetric[0]);
	}

}
