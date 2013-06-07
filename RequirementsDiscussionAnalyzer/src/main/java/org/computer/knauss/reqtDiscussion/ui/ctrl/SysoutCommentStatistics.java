package org.computer.knauss.reqtDiscussion.ui.ctrl;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;

public class SysoutCommentStatistics extends AbstractDiscussionIterationCommand {
	private static final String NO_CL = "no cl";
	private static final String OTHER = "other";
	private static final String COORD = "coord";
	private static final String CLARI = "clari";
	private static final long serialVersionUID = 1L;

	public SysoutCommentStatistics() {
		super("Print comment statistics on command line");
	}

	@Override
	protected void preProcessingHook() {
		System.out.println("ID\t" + CLARI + "\t" + COORD + "\t" + OTHER + "\t"
				+ NO_CL);
	}

	@Override
	protected void processDiscussionHook(Discussion[] d) {
		System.out.print(d[0].getID());
		System.out.print("\t");
		int clari = 0, coord = 0, other = 0, no_cl = 0;
		for (DiscussionEvent de : getDiscussionEvents(d)) {
			String classification = de.getReferenceClassification().substring(
					0, 5);
			if (CLARI.equals(classification))
				clari++;
			else if (COORD.equals(classification))
				coord++;
			else if (OTHER.equals(classification))
				other++;
			else if (NO_CL.equals(classification))
				no_cl++;
			else
				System.err.println(classification);
		}
		System.out.print(clari);
		System.out.print("\t");
		System.out.print(coord);
		System.out.print("\t");
		System.out.print(other);
		System.out.print("\t");
		System.out.print(no_cl);
		System.out.println();
	}

}
