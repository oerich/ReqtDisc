package org.computer.knauss.reqtDiscussion.ui.uiModel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class PropertiesTableModel implements TableModel {
	private static final String[] COLUMN_NAMES = new String[] { "Key", "Value",
			"Remark" };
	private List<TableModelListener> listeners = new Vector<TableModelListener>();
	private Properties properties;
	private Map<String, String> remarks;
	private String[] keys;

	@Override
	public void addTableModelListener(TableModelListener l) {
		this.listeners.add(l);
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return COLUMN_NAMES[columnIndex];
	}

	public void setProperties(Properties p) {
		this.properties = p;
		this.keys = this.properties.stringPropertyNames()
				.toArray(new String[0]);
		Arrays.sort(this.keys);
		fireTableChanged();
	}

	private void fireTableChanged() {
		TableModelEvent e = new TableModelEvent(this);
		for (TableModelListener l : this.listeners)
			l.tableChanged(e);
	}

	public void setRemarks(Map<String, String> remarks) {
		this.remarks = remarks;
		fireTableChanged();
	}

	@Override
	public int getRowCount() {
		if (this.properties == null)
			return 0;
		return this.properties.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String key = this.keys[rowIndex];
		if (columnIndex == 0)
			return key;
		if (columnIndex == 1)
			return this.properties.getProperty(key);
		if (columnIndex == 2) {
			if (this.remarks == null)
				return null;
			return this.remarks.get(key);
		}

		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
		this.listeners.remove(l);
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			this.properties.setProperty(this.keys[rowIndex], aValue.toString());
		}
	}

}
