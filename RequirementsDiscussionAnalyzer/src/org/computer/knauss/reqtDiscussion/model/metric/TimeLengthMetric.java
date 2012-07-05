package org.computer.knauss.reqtDiscussion.model.metric;

import java.sql.Date;
import java.text.DecimalFormat;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class TimeLengthMetric extends AbstractDiscussionMetric {

	private DecimalFormat df;

	@Override
	public String getName() {
		return "time length [days]";
	}

	@Override
	public Double considerDiscussions(Discussion[] wi) {
		if (wi == null || wi.length <= 0)
			return 0d;
		Date min = wi[0].getDateCreated();
		Date max = wi[0].getDateCreated();
		for (Discussion w : wi)
			for (DiscussionEvent wc : w.getAllComments()) {
				if (min.after(wc.getCreationDate()))
					min = wc.getCreationDate();
				if (max.before(wc.getCreationDate()))
					max = wc.getCreationDate();
			}

		// return the duration in days.
		return (double) (max.getTime() - min.getTime()) / 86400000;
	}

	public DecimalFormat getDecimalFormat() {
		if (this.df == null) {
			this.df = new DecimalFormat("#.##");
		}
		return this.df;
	}

}
