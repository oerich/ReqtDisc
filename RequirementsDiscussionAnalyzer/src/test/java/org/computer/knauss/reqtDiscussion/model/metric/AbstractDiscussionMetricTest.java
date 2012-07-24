package org.computer.knauss.reqtDiscussion.model.metric;

import static org.junit.Assert.*;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.partition.TimeIntervalPartition;
import org.junit.Test;

public class AbstractDiscussionMetricTest {

	@Test
	public void testComputeMetric() {
		// a few discussions we believe belong together.
		Discussion d1 = DiscussionFactory.getInstance().getDiscussion(13764);
		Discussion d2 = DiscussionFactory.getInstance().getDiscussion(40774);
		Discussion d3 = DiscussionFactory.getInstance().getDiscussion(40775);

		DiscussionEvent de1 = new DiscussionEvent();
		DiscussionEvent de2 = new DiscussionEvent();
		DiscussionEvent de3 = new DiscussionEvent();

		d1.setCreator("Creator1");
		d1.setCreationDate(new Date(System.currentTimeMillis() - 10
				* TimeIntervalPartition.MILLIS_PER_DAY));
		d2.setCreator("Creator2");
		d2.setCreationDate(new Date(System.currentTimeMillis() - 8
				* TimeIntervalPartition.MILLIS_PER_DAY));
		d3.setCreator("Creator3");
		d3.setCreationDate(new Date(System.currentTimeMillis() - 7
				* TimeIntervalPartition.MILLIS_PER_DAY));

		de1.setCreator("Creator1");
		de1.setCreationDate(new Date(System.currentTimeMillis() - 9
				* TimeIntervalPartition.MILLIS_PER_DAY));
		de2.setCreator("Creator2");
		de2.setCreationDate(new Date(System.currentTimeMillis() - 7
				* TimeIntervalPartition.MILLIS_PER_DAY));
		de3.setCreator("Creator3");
		de3.setCreationDate(new Date(System.currentTimeMillis() - 6
				* TimeIntervalPartition.MILLIS_PER_DAY));

		d1.addDiscussionEvents(new DiscussionEvent[] { de1 });
		d2.addDiscussionEvents(new DiscussionEvent[] { de2 });
		d3.addDiscussionEvents(new DiscussionEvent[] { de3 });

		ContributorNumberMetric m = new ContributorNumberMetric();
		m.computeMetric(new Discussion[] { d1, d2, d3 });

		assertEquals(3, m.getMin(), 0.01);
		assertEquals(3, m.getMax(), 0.01);
		assertEquals(3, m.getAverage(), 0.01);

		assertEquals(1, m.getResults().keySet().size());
		assertEquals("13764,40774,40775", m.getResults().keySet().toArray()[0]);
		assertEquals(3, m.getResults().get("13764,40774,40775"), 0.01);
	}

}
