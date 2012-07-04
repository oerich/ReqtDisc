package org.computer.knauss.reqtDiscussion.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DiscussionEventTest {

	@Test
	public void testGetTagString() {
		DiscussionEvent de = new DiscussionEvent();
		assertEquals("", de.getTagString());
		de.addTag("one Tag");
		assertEquals("one Tag", de.getTagString());
		de.addTag("second Tag");
		assertEquals("one Tag,second Tag", de.getTagString());
		de.removeTag("one Tag");
		assertEquals("second Tag", de.getTagString());

		de.addTag("third Tag");
		assertTrue(de.hasTag("second Tag"));
		assertTrue(de.hasTag("third Tag"));
		assertFalse(de.hasTag("one Tag"));
	}

}
