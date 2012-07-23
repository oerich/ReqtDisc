package org.computer.knauss.reqtDiscussion.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class NameFilterTest {

	@Test
	public void test() {

		IClassificationFilter.NAME_FILTER.setName("rater1");
		assertEquals("rater1", IClassificationFilter.NAME_FILTER.getName());
		IClassificationFilter.NAME_FILTER.setName("rater1,rater2");
		assertEquals("rater1,rater2", IClassificationFilter.NAME_FILTER.getName());
		IClassificationFilter.NAME_FILTER.setName(null);
		assertEquals("null", IClassificationFilter.NAME_FILTER.getName());
		
		
	}

}
