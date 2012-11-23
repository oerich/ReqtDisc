package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import java.sql.Date;
import java.util.Random;

import org.computer.knauss.reqtDiscussion.model.Discussion;
import org.computer.knauss.reqtDiscussion.model.DiscussionEvent;
import org.computer.knauss.reqtDiscussion.model.DiscussionEventClassification;
import org.computer.knauss.reqtDiscussion.model.DiscussionFactory;
import org.computer.knauss.reqtDiscussion.model.machineLearning.ClassifierManager;
import org.computer.knauss.reqtDiscussion.model.partition.TimeIntervalPartition;
import org.computer.knauss.reqtDiscussion.ui.ctrl.ProgressMonitorProbe;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AbstractKFoldCrossEvaluationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		DiscussionFactory.getInstance().clear();
	}

	@Test
	public void testDiscussionEventLevel() {
		AbstractKFoldCrossEvaluation eval = AbstractKFoldCrossEvaluation.DISCUSSION_EVENT_LEVEL;
		eval.setAggregateDiscussions(false);
		eval.setBucketAllocationStrategy(new EventBasedRandomAllocationStrategy());
		eval.setClassifier(ClassifierManager.getInstance().getClassifier());
		eval.setReferenceRaterName("gpoo,eric1");
		ProgressMonitorProbe pmb = new ProgressMonitorProbe();
		ConfusionMatrix cm = eval.evaluate(10, createRandomTestData(), pmb);

		System.out.println(cm.layoutConfusionMatrix(" \t ", "\n"));
	}

	@Test
	public void testDiscussionLevel() {
		AbstractKFoldCrossEvaluation eval = AbstractKFoldCrossEvaluation.DISCUSSION_LEVEL;
		eval.setAggregateDiscussions(false);
		eval.setBucketAllocationStrategy(new GreedyDiscussionEventAllocationStrategy());
		eval.setClassifier(ClassifierManager.getInstance().getClassifier());
		eval.setReferenceRaterName("gpoo,eric1");
		ProgressMonitorProbe pmb = new ProgressMonitorProbe();
		ConfusionMatrix cm = eval.evaluate(10, createRandomTestData(), pmb);

		System.out.println(cm.layoutConfusionMatrix(" \t ", "\n"));
	}

	private Discussion[] createRandomTestData() {
		Random r = new Random();
		@SuppressWarnings("deprecation")
		Date baseDate = new Date(77, 8, 17);
		Discussion[] ret = new Discussion[100];
		for (int i = 0; i < ret.length; i++) {
			DiscussionEvent[] des = new DiscussionEvent[r.nextInt(20)];
			for (int j = 0; j < des.length; j++) {
				des[j] = new DiscussionEvent();
				des[j].setCreationDate(new Date(baseDate.getTime() + i
						* TimeIntervalPartition.MILLIS_PER_DAY + j
						* TimeIntervalPartition.MILLIS_PER_DAY));
				des[j].setCreator("test");
				des[j].setDiscussionID(i);
				des[j].setID(j);
				DiscussionEventClassification dec = new DiscussionEventClassification();
				if (r.nextBoolean()) {
					dec.setClassification("clarif");
					des[j].setContent("Content" + j
							+ " is clarification related.");
				} else {
					dec.setClassification("other");
					des[j].setContent("Content" + j
							+ " is coordination related.");
				}
				// rater is hard coded...
				dec.setClassifiedby("eric1");
				dec.setConfidence(r.nextDouble());
				dec.setWorkitemcommentid(des[j].getID());
				des[j].insertOrUpdateClassification(dec);
			}
			ret[i] = DiscussionFactory.getInstance().getDiscussion(i);
			ret[i].addDiscussionEvents(des);
			ret[i].setCreationDate(new Date(baseDate.getTime() + i
					* TimeIntervalPartition.MILLIS_PER_DAY));
			ret[i].setCreator("test");
			ret[i].setDescription("Some description of a story");
			ret[i].setSummary("Some summary");
			ret[i].setType("story");
		}

		return ret;
	}
}
