package org.computer.knauss.reqtDiscussion.ui.visualization;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.junit.Test;

public class PixelPartitionTest {

	@Test
	public void testGetPartitionCount() {
		PixelPartition testitem = new PixelPartition();

		assertEquals(ICommentOverTimePartition.TYPE_PIXEL,
				testitem.getPartitionType());
		testitem.setPartitionType(10);
		assertEquals(ICommentOverTimePartition.TYPE_PIXEL,
				testitem.getPartitionType());
	}

	@Test
	public void testGetPartitionForWorkitemComment() {
		PixelPartition testitem = new PixelPartition();
		testitem.setTimeInterval(new Date(0), new Date(100));

		DiscussionEvent wc = new DiscussionEvent();
		wc.setContent("Testcomment");
		wc.setCreationDate(new Date(50));

		assertEquals(50, testitem.getPartitionForWorkitemComment(wc));

		testitem.setPartitionCount(600);
		assertEquals(300, testitem.getPartitionForWorkitemComment(wc));
	}

	@Test
	public void testOnlyOneEvent() {
		PixelPartition testitem = new PixelPartition();
		testitem.setTimeInterval(new Date(50), new Date(50));

		DiscussionEvent wc = new DiscussionEvent();
		wc.setContent("Testcomment");
		wc.setCreationDate(new Date(50));

		assertEquals(50, testitem.getPartitionForWorkitemComment(wc));

		testitem.setPartitionCount(600);
		assertEquals(300, testitem.getPartitionForWorkitemComment(wc));
	}
}
