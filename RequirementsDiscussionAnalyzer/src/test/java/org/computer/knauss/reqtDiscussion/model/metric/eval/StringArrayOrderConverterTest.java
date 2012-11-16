package org.computer.knauss.reqtDiscussion.model.metric.eval;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringArrayOrderConverterTest {

	private StringArrayOrderConverter converter;

	@Before
	public void setUp() throws Exception {
		this.converter = new StringArrayOrderConverter();
		// the order should be indifferent, happy-ending, discordant,
		// back-to-draft, text-book, procrastination, unknown

		this.converter.setLeft(new String[] { "unknown", "indifferent",
				"discordant", "procrastination", "back-to-draft",
				"happy-ending", "text-book" });
		this.converter.setRight(new String[] { "indifferent", "happy-ending",
				"discordant", "back-to-draft", "text-book", "procrastination",
				"unknown" });
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testL2R() {
		assertEquals(6, this.converter.convertL2R(0));

	}

	@Test
	public void testR2L() {
		assertEquals(1, this.converter.convertR2L(0));
		assertEquals(5, this.converter.convertR2L(1));

	}

	@Test
	public void testNotIncludedInTarget() {
		this.converter.setLeft(new String[] { "unknown", "MIST", "indifferent",
				"discordant", "procrastination", "back-to-draft",
				"happy-ending", "text-book" });
		
		assertEquals(1, this.converter.convertL2R(1));
	}
	
	@Test (expected=ArrayIndexOutOfBoundsException.class)
	public void testNotIncludedInSource() {
		this.converter.setLeft(new String[] { "unknown", "MIST", "indifferent",
				"discordant", "procrastination", "back-to-draft",
				"happy-ending", "text-book" });
		
		assertEquals(1, this.converter.convertR2L(7));
	}
}
