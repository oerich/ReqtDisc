package org.computer.knauss.reqtDiscussion.model.machineLearning.eval;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.computer.knauss.reqtDiscussion.model.metric.PatternMetric;
import org.junit.Test;

public class ConfusionMatrixTest {

	@Test
	public void testMathOnWikipediaExample() {
		ConfusionMatrix cm = createWikipediaExample();

		assertEquals(5, cm.getTruePositives("Cat"));
		assertEquals(3, cm.getTruePositives("Dog"));
		assertEquals(11, cm.getTruePositives("Rabbit"));

		assertEquals(3, cm.getFalseNegatives("Cat"));
		assertEquals(2, cm.getFalsePositives("Cat"));
		assertEquals(17, cm.getTrueNegatives("Cat"));

		double p = 5.0 / 7.0;
		double r = 5.0 / 8.0;
		assertEquals(r, cm.getRecall("Cat"), 0.001);
		assertEquals(p, cm.getPrecision("Cat"), 0.001);

		assertEquals(2 * r * p / (r + p), cm.getFMeasure("Cat"), 0.001);

		assertEquals(17.0 / (17.0 + 2.0), cm.getSpecificity("Cat"), 0.001);

		int[][] confusionMatrixRaw = cm.getConfusionMatrix();
		assertEquals(5, confusionMatrixRaw[0][0]);
		assertEquals(3, confusionMatrixRaw[1][1]);
		assertEquals(11, confusionMatrixRaw[2][2]);
		assertEquals(3, confusionMatrixRaw[0][1]);
		assertEquals(0, confusionMatrixRaw[0][2]);
		assertEquals(2, confusionMatrixRaw[1][0]);
		assertEquals(1, confusionMatrixRaw[1][2]);
		assertEquals(0, confusionMatrixRaw[2][0]);
		assertEquals(2, confusionMatrixRaw[2][1]);
	}

	@Test
	public void testConvertMatrixOnWikipediaExample() {
		ConfusionMatrix cm = createWikipediaExample();

		ConfusionMatrix cm2 = cm.convertOrdering(new String[] { "Dog", "Cat",
				"Rabbit" });
		int[][] confusionMatrixRaw = cm2.getConfusionMatrix();
		assertEquals(3, confusionMatrixRaw[0][0]);
		assertEquals(5, confusionMatrixRaw[1][1]);
		assertEquals(11, confusionMatrixRaw[2][2]);
		assertEquals(2, confusionMatrixRaw[0][1]);
		assertEquals(1, confusionMatrixRaw[0][2]);
		assertEquals(3, confusionMatrixRaw[1][0]);
		assertEquals(0, confusionMatrixRaw[1][2]);
		assertEquals(2, confusionMatrixRaw[2][0]);
		assertEquals(0, confusionMatrixRaw[2][1]);

		// All the values should still be the same...
		assertEquals(5, cm2.getTruePositives("Cat"));
		assertEquals(3, cm2.getTruePositives("Dog"));
		assertEquals(11, cm2.getTruePositives("Rabbit"));

		assertEquals(3, cm2.getFalseNegatives("Cat"));
		assertEquals(2, cm2.getFalsePositives("Cat"));
		assertEquals(17, cm2.getTrueNegatives("Cat"));

		double p = 5.0 / 7.0;
		double r = 5.0 / 8.0;
		assertEquals(r, cm2.getRecall("Cat"), 0.001);
		assertEquals(p, cm2.getPrecision("Cat"), 0.001);

		assertEquals(2 * r * p / (r + p), cm2.getFMeasure("Cat"), 0.001);

		assertEquals(17.0 / (17.0 + 2.0), cm2.getSpecificity("Cat"), 0.001);
	}

	@Test(expected = RuntimeException.class)
	public void testBadNewOrder() {
		ConfusionMatrix cm = createWikipediaExample();

		cm.convertOrdering(new String[] { "Dog", "Tiger", "Rabbit" });
	}

	@Test
	public void testWrongCategory() {
		ConfusionMatrix cm = createWikipediaExample();
		try {
			cm.getTruePositives("Tiger");
		} catch (RuntimeException re) {
			assertTrue(re.getMessage().indexOf("Tiger") > 0);
		}
	}

	@Test
	public void testWithPatternMetric() {
		// basically tests the string array for initialization...
		ConfusionMatrix confusionMatrix = new ConfusionMatrix();
		confusionMatrix.init(new String[] { "indifferent", "happy-ending",
				"discordant", "back-to-draft", "textbook-example",
				"procrastination", "unknown" });

		PatternMetric m = new PatternMetric();
		for (int i = 0; i <= PatternMetric.PATTERNS.length; i++) {
			confusionMatrix.report(m.decode(i - 1), m.decode(i));
		}

		System.out.println(confusionMatrix.layoutConfusionMatrix(" \t ", "\n"));
		ConfusionMatrix cm2 = confusionMatrix.collapseCategories("suspicious",
				new String[] { "back-to-draft", "happy-ending",
						"discordant" });
		cm2 = cm2.collapseCategories("unsuspicious", new String[] {
				"textbook-example", "indifferent" });
		cm2 = cm2.collapseCategories("unknown", new String[] {"unknown","procrastination"});
		cm2 = cm2.collapseCategories("other", new String[] { "coord",
				"other", "no cl", "autog", "Solut" });
		System.out.println("---------------");
		System.out.println(cm2.layoutConfusionMatrix(" \t ", "\n"));
	}

	@Test
	public void testCollapseCategories() {
		ConfusionMatrix cm = createWikipediaExample();

		ConfusionMatrix cm2 = cm.collapseCategories("Cats'n'Dogs",
				new String[] { "Cat", "Dog" });

		assertEquals(2, cm2.getCategories().length);
		assertEquals("Cats'n'Dogs", cm2.getCategories()[0]);
		assertEquals(13, cm2.getTruePositives("Cats'n'Dogs"));
		assertEquals(11, cm2.getTruePositives("Rabbit"));

		ConfusionMatrix cm3 = cm.collapseCategories("No-Miao", new String[] {
				"Dog", "Rabbit" });
		assertEquals(2, cm3.getCategories().length);
		assertEquals("No-Miao", cm3.getCategories()[1]);
		assertEquals(17, cm3.getTruePositives("No-Miao"));
		assertEquals(5, cm3.getTruePositives("Cat"));

	}

	private ConfusionMatrix createWikipediaExample() {
		ConfusionMatrix cm = new ConfusionMatrix();
		cm.init(new String[] { "Cat", "Dog", "Rabbit" });

		cm.report("Cat", "Cat");
		cm.report("Cat", "Cat");
		cm.report("Cat", "Cat");
		cm.report("Cat", "Cat");
		cm.report("Cat", "Cat");

		cm.report("Cat", "Dog");
		cm.report("Cat", "Dog");
		cm.report("Cat", "Dog");

		cm.report("Dog", "Cat");
		cm.report("Dog", "Cat");

		cm.report("Dog", "Dog");
		cm.report("Dog", "Dog");
		cm.report("Dog", "Dog");

		cm.report("Dog", "Rabbit");

		cm.report("Rabbit", "Dog");
		cm.report("Rabbit", "Dog");

		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		cm.report("Rabbit", "Rabbit");
		return cm;
	}

}
