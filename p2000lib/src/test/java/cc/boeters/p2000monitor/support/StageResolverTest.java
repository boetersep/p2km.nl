package cc.boeters.p2000monitor.support;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import cc.boeters.p2000monitor.support.StageResolver.Stage;

public class StageResolverTest {

	@Test
	public void testDevelopment() {
		System.setProperty("stage", "development");
		assertEquals(Stage.DEVELOPMENT, StageResolver.resolve());
	}

	@Test
	public void testEmpty() {
		System.setProperty("stage", "");
		assertEquals(Stage.PRODUCTION, StageResolver.resolve());
	}

	@Test
	public void testNotSet() {
		System.getProperties().remove("stage");
		assertEquals(Stage.PRODUCTION, StageResolver.resolve());
	}

	@Test
	public void testOther() {
		System.setProperty("stage", "bogus");
		assertEquals(Stage.PRODUCTION, StageResolver.resolve());
	}

	@Test
	public void testProduction() {
		System.setProperty("stage", "production");
		assertEquals(Stage.PRODUCTION, StageResolver.resolve());
	}
}
