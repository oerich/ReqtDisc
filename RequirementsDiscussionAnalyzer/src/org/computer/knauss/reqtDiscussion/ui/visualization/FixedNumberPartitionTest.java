package org.computer.knauss.reqtDiscussion.ui.visualization;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.junit.Test;

public class FixedNumberPartitionTest {

	@Test
	public void testPartitionType() {
		FixedNumberPartition fnp = new FixedNumberPartition();
		assertEquals(ICommentOverTimePartition.TYPE_FIXED_NUMBER,
				fnp.getPartitionType());
		fnp.setPartitionCount(117);
		assertEquals(ICommentOverTimePartition.TYPE_FIXED_NUMBER,
				fnp.getPartitionType());
		fnp.setPartitionCount(ICommentOverTimePartition.TYPE_DAYS);
		assertEquals(ICommentOverTimePartition.TYPE_FIXED_NUMBER,
				fnp.getPartitionType());
	}

	@Test
	public void testSetPartitionCount() {
		FixedNumberPartition fnp = new FixedNumberPartition();
		assertEquals(8, fnp.getPartitionCount());
		fnp.setPartitionCount(4);
		assertEquals(4, fnp.getPartitionCount());
	}

	@Test
	public void testGetPartitionForWorkitemComment() {
		FixedNumberPartition fnp = new FixedNumberPartition();
		fnp.setPartitionCount(4);
		fnp.setTimeInterval(new Date(0), new Date(100));

		DiscussionEvent wc = new DiscussionEvent();
		wc.setContent("Testcomment");
		wc.setCreationDate(new Date(0));

		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(24));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(25));
		assertEquals(1, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(49));
		assertEquals(1, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(50));
		assertEquals(2, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(74));
		assertEquals(2, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(75));
		assertEquals(3, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(99));
		assertEquals(3, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(100));
		assertEquals(3, fnp.getPartitionForWorkitemComment(wc));

		wc.setCreationDate(new Date(101));
		assertEquals(3, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(-1));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
	}

	@Test
	public void testMinimumOnePartition() {
		FixedNumberPartition fnp = new FixedNumberPartition();
		fnp.setPartitionCount(0);
		assertEquals(1, fnp.getPartitionCount());
		fnp.setTimeInterval(new Date(0), new Date(100));

		DiscussionEvent wc = new DiscussionEvent();
		wc.setContent("Testcomment");
		wc.setCreationDate(new Date(0));

		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(24));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(25));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(49));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(50));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(74));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(75));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(99));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(100));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
		wc.setCreationDate(new Date(101));
		assertEquals(0, fnp.getPartitionForWorkitemComment(wc));
	}
}
