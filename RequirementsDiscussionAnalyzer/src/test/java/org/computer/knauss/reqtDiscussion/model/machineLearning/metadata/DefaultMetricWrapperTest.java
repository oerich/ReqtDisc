package org.computer.knauss.reqtDiscussion.model.machineLearning.metadata;

import static org.junit.Assert.*;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.metric.AbstractDiscussionMetric;
import org.junit.Before;
import org.junit.Test;

public class DefaultMetricWrapperTest {

	private final class TestMetric extends AbstractDiscussionMetric {
		double[] values = { 0, 1, 2, 3, 5, 8, 13, 21, 34, 54, 55 };
		int lastValue = 0;

		@Override
		public int measurementType() {
			return 0;
		}

		@Override
		public String getName() {
			return "Test Metric";
		}

		@Override
		public Double considerDiscussions(Discussion[] wi) {
			Double ret = values[lastValue];
			lastValue++;
			if (lastValue >= values.length)
				lastValue = 0;
			return ret;
		}
	}

	private TestMetric metric = new TestMetric();
	private DefaultMetricWrapper metricWrapper;

	@Before
	public void setUp() throws Exception {
		this.metricWrapper = new DefaultMetricWrapper();
		this.metricWrapper.init(metric,
				new Object[] { "low", "medium", "high" });
	}

	@Test
	public void testMeasure() {
		assertEquals(0, this.metricWrapper.measure(null));
		assertEquals(1, this.metricWrapper.measure(null));
		assertEquals(2, this.metricWrapper.measure(null));
		assertEquals(3, this.metricWrapper.measure(null));
		assertEquals(5, this.metricWrapper.measure(null));
		assertEquals(8, this.metricWrapper.measure(null));
		assertEquals(13, this.metricWrapper.measure(null));
		assertEquals(21, this.metricWrapper.measure(null));
		assertEquals(34, this.metricWrapper.measure(null));
		assertEquals(54, this.metricWrapper.measure(null));
		assertEquals(55, this.metricWrapper.measure(null));
		assertEquals(0, this.metricWrapper.measure(null));
	}

	@Test
	public void testMeasureWithPreprocess() {
		for (int i = 0; i < this.metric.values.length; i++) {
			this.metricWrapper.preprocess(null);
		}
		assertEquals("low", this.metricWrapper.measure(null));
		assertEquals("low", this.metricWrapper.measure(null));
		assertEquals("low", this.metricWrapper.measure(null));
		assertEquals("low", this.metricWrapper.measure(null));
		assertEquals("low", this.metricWrapper.measure(null));
		assertEquals("low", this.metricWrapper.measure(null));
		assertEquals("low", this.metricWrapper.measure(null));
		assertEquals("medium", this.metricWrapper.measure(null));
		assertEquals("medium", this.metricWrapper.measure(null));
		assertEquals("high", this.metricWrapper.measure(null));
		assertEquals("high", this.metricWrapper.measure(null));
		assertEquals("low", this.metricWrapper.measure(null));
	}

}
