package org.computer.knauss.reqtDiscussion.ui.uiModel;

import static org.junit.Assert.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.junit.Test;

public class DiscussionTableModelTest {

	@Test
	public void testGetRowCount() {
		DiscussionTableModel dtm = new DiscussionTableModel();
		assertEquals(0, dtm.getRowCount());
		dtm.setDiscussions(new Discussion[3]);
		assertEquals(3, dtm.getRowCount());
		dtm.setDiscussions(null);
		assertEquals(0, dtm.getRowCount());
		assertNull(dtm.getValueAt(3, 1));
	}

	@Test
	public void testAddData() {
		DiscussionTableModel wtm = new DiscussionTableModel();
		assertEquals(2, wtm.getColumnCount());

		Discussion d1 = new Discussion();
		Discussion d2 = new Discussion();
		Discussion d3 = new Discussion();
		d1.setId(1);
		d2.setId(2);
		d3.setId(3);

		d1.setSummary("sum1");
		d2.setSummary("sum2");
		d3.setSummary("sum3");

		DTMListener l = new DTMListener();
		wtm.addTableModelListener(l);

		assertFalse(l.tableChanged);
		wtm.setDiscussions(new Discussion[] { d1, d2, d3 });
		assertTrue(l.tableChanged);
		
		assertEquals("ID", wtm.getColumnName(0));
		assertEquals("Summary", wtm.getColumnName(1));

		assertFalse(wtm.isCellEditable(0, 0));
		assertFalse(wtm.isCellEditable(0, 1));

		assertEquals(Integer.class, wtm.getColumnClass(0));
		assertEquals(String.class, wtm.getColumnClass(1));

		assertEquals(3, wtm.getRowCount());

		assertEquals(1, wtm.getValueAt(0, 0));
		assertEquals(2, wtm.getValueAt(1, 0));
		assertEquals(3, wtm.getValueAt(2, 0));
		assertEquals("sum1", wtm.getValueAt(0, 1));
		assertEquals("sum2", wtm.getValueAt(1, 1));
		assertEquals("sum3", wtm.getValueAt(2, 1));
	}

	class DTMListener implements TableModelListener {

		boolean tableChanged = false;

		@Override
		public void tableChanged(TableModelEvent e) {
			this.tableChanged = true;
		}

	}
}
