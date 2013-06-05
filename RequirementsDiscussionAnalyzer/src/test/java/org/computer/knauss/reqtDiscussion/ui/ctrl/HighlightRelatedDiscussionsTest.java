package org.computer.knauss.reqtDiscussion.ui.ctrl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.junit.Before;
import org.junit.Test;

public class HighlightRelatedDiscussionsTest {

	private HighlightRelatedDiscussions hrw;

	@Before
	public void setUp() throws Exception {
		this.hrw = new HighlightRelatedDiscussions();
	}

	@Test
	public void testWithFirstIDExisting() {
		int[] wiIDs = this.hrw.getRelatedDiscussionIDs(13764);

		assertEquals(3, wiIDs.length);
		assertEquals(13764, wiIDs[0]);
		assertEquals(40774, wiIDs[1]);
		assertEquals(40775, wiIDs[2]);
	}

	@Test
	public void testWithLaterIDExisting() {
		int[] wiIDs = this.hrw.getRelatedDiscussionIDs(40774);

		assertEquals(3, wiIDs.length);
		assertEquals(13764, wiIDs[0]);
		assertEquals(40774, wiIDs[1]);
		assertEquals(40775, wiIDs[2]);
	}

	@Test
	public void testNonExisting() {
		int[] wiIDs = this.hrw.getRelatedDiscussionIDs(0123);

		assertEquals(1, wiIDs.length);
		assertEquals(0123, wiIDs[0]);
	}

	@Test
	public void testGetAllAggregatedDiscussions() {
		Discussion[] discussions = new Discussion[4];
		discussions[0] = DiscussionFactory.getInstance().getDiscussion(0123);
		discussions[1] = DiscussionFactory.getInstance().getDiscussion(13764);
		discussions[2] = DiscussionFactory.getInstance().getDiscussion(40774);
		discussions[3] = DiscussionFactory.getInstance().getDiscussion(40775);

		List<Discussion[]> result = this.hrw
				.getAllAggregatedDiscussions(discussions);
		assertEquals(2, result.size());
		assertEquals(1, result.get(1).length);
		assertEquals(3, result.get(0).length);
	}

	@Test
	public void testGetAllAggregatedDiscussionsIncomplete() {
		Discussion[] discussions = new Discussion[3];
		discussions[0] = DiscussionFactory.getInstance().getDiscussion(0123);
		discussions[1] = DiscussionFactory.getInstance().getDiscussion(13764);
		discussions[2] = DiscussionFactory.getInstance().getDiscussion(40774);

		List<Discussion[]> result = this.hrw
				.getAllAggregatedDiscussions(discussions);
		assertEquals(2, result.size());
		assertEquals(1, result.get(1).length);
		assertEquals(2, result.get(0).length);
	}

}
