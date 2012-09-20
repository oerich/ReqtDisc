package org.computer.knauss.reqtDiscussion.model.metric;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.BackToDraftPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.ClosingGatePattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.HappyEndingPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.IPatternClass;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.IndifferentPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.NoSharedUnderstandingPattern;
import org.computer.knauss.reqtDiscussion.model.clarificationPatterns.PerfectPattern;
import org.computer.knauss.reqtDiscussion.model.partition.FixedNumberPartition;
import org.computer.knauss.reqtDiscussion.model.partition.IDiscussionOverTimePartition;

public class PatternMetric extends AbstractDiscussionMetric {

	public static final IPatternClass[] PATTERNS = new IPatternClass[] {
			new IndifferentPattern(), new NoSharedUnderstandingPattern(),
			new ClosingGatePattern(), new BackToDraftPattern(),
			new HappyEndingPattern(), new PerfectPattern() };
	private IDiscussionOverTimePartition partition;

	@Override
	public String getName() {
		return "Pattern";
	}

	@Override
	public Double considerDiscussions(Discussion[] discussions) {
		for (int i = 0; i < PATTERNS.length; i++) {
			IPatternClass p = PATTERNS[i];
			p.setCommentPartition(this.partition);
			if (p.matchesPattern(getEvents(discussions)))
				return (double) i;
		}
		return -1.0;
	}

	@Override
	public void initDiscussions(Discussion[] discussions) {
		if (this.partition == null) {
			this.partition = new FixedNumberPartition();
			this.partition.setPartitionCount(32);
		}
		this.partition.setTimeInterval(discussions);
		this.partition.setModelElements(getEvents(discussions));
	}

	private DiscussionEvent[] getEvents(Discussion[] discussions) {
		List<DiscussionEvent> tmp = new LinkedList<DiscussionEvent>();
		for (Discussion d : discussions)
			Collections.addAll(tmp, d.getDiscussionEvents());
		return tmp.toArray(new DiscussionEvent[0]);
	}

	@Override
	public int measurementType() {
		return NOMINAL_TYPE;
	}

	@Override
	public String decode(double val) {
		if (val >= 0 && val < PATTERNS.length)
			return PatternMetric.PATTERNS[(int) val].getName();
		return "unknown";
	}

}
