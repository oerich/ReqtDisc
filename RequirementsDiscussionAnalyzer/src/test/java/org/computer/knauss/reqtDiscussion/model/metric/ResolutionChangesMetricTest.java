package org.computer.knauss.reqtDiscussion.model.metric;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.Incident;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ResolutionChangesMetricTest {

	private ResolutionChangesMetric metric;

	@Before
	public void setUp() throws Exception {
		this.metric = new ResolutionChangesMetric();
	}

	@After
	public void tearDown() throws Exception {
		DiscussionFactory.getInstance().clear();
	}

	@Test
	public void testNoChanges() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(1234);
		assertEquals(0.0,
				this.metric.considerDiscussions(new Discussion[] { d }), 0.001);
	}

	@Test
	public void testOneChange() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(1234);
		Incident i = new Incident();
		i.setDate(new Date(System.currentTimeMillis()));
		i.setName("resolution");
		i.setSummary("0 => 1");
		d.addIncidents(new Incident[] { i });
		assertEquals(1.0,
				this.metric.considerDiscussions(new Discussion[] { d }), 0.001);
	}

	@Test
	public void testOneChangeAndOtherIncident() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(1234);
		Incident i = new Incident();
		i.setDate(new Date(System.currentTimeMillis()));
		i.setName("resolution");
		i.setSummary("0 => 1");

		Incident other = new Incident();
		other.setDate(new Date(System.currentTimeMillis()));
		other.setName("summary");
		other.setSummary("test text => Metric Test Text");
		d.addIncidents(new Incident[] { i, other });
		assertEquals(1.0,
				this.metric.considerDiscussions(new Discussion[] { d }), 0.001);
	}

	@Test
	public void testFiveChanges() {
		Discussion d = DiscussionFactory.getInstance().getDiscussion(1234);
		Incident[] incidents = new Incident[5];
		for (int i = 0; i < incidents.length; i++) {
			incidents[i] = new Incident();
			incidents[i].setDate(new Date(System.currentTimeMillis()
					- ((5 - i) * 1000)));
			incidents[i].setName("resolution");
			incidents[i].setSummary(i + " => " + (i + 1));
		}
		d.addIncidents(incidents);
		assertEquals(5.0,
				this.metric.considerDiscussions(new Discussion[] { d }), 0.001);
	}

	@Test
	public void testNoDiscussions() {
		assertEquals(0.0, this.metric.considerDiscussions(new Discussion[] {}),
				0.001);
	}

}
