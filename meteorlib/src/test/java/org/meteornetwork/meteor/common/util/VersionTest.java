package org.meteornetwork.meteor.common.util;

import junit.framework.TestCase;

import org.junit.Test;

public class VersionTest extends TestCase {

	@Test
	public void testSetVersionString() {
		Version version = new Version("4.2.1");
		assertEquals("4.2.1", version.toString());
		
		version.setVersion("3.5");
		assertEquals("3.5.0", version.toString());
		
		version.setVersion("8");
		assertEquals("8.0.0", version.toString());
	}

	@Test
	public void testSetVersionSequenceInteger() {
		Version version = new Version();
		version.setVersion(Version.Sequence.MAJOR, 1);
		assertEquals("1.0.0", version.toString());
		
		version.setVersion(Version.Sequence.MINOR, 2);
		assertEquals("1.2.0", version.toString());
		
		version.setVersion(Version.Sequence.RELEASE, 1);
		assertEquals("1.2.1", version.toString());
	}
	
	@Test
	public void testDiluteVersion() {
		Version version = new Version("5.4.3");
		
		version = version.diluteVersion(Version.Sequence.RELEASE);
		assertEquals("5.4.3", version.toString());
		
		version = version.diluteVersion(Version.Sequence.MINOR);
		assertEquals("5.4.0", version.toString());
		
		version.setVersion(Version.Sequence.RELEASE, 6);
		version = version.diluteVersion(Version.Sequence.MAJOR);
		assertEquals("5.0.0", version.toString());
	}
	
	@Test
	public void testMatches() {
		Version version = new Version("6.5.7");
		
		assertTrue(version.matches("6.5.7"));
		assertFalse(version.matches("6.5.6"));
		assertTrue(version.matches("6.5.x"));
		assertFalse(version.matches("6.6.7"));
		assertFalse(version.matches("6.6.6"));
		assertFalse(version.matches("6.6.x"));
		assertTrue(version.matches("6.x.7"));
		assertFalse(version.matches("6.x.6"));
		assertTrue(version.matches("6.x.x"));
		
		assertFalse(version.matches("5.5.7"));
		assertFalse(version.matches("5.5.6"));
		assertFalse(version.matches("5.5.x"));
		assertFalse(version.matches("5.6.7"));
		assertFalse(version.matches("5.6.6"));
		assertFalse(version.matches("5.6.x"));
		assertFalse(version.matches("5.x.7"));
		assertFalse(version.matches("5.x.6"));
		assertFalse(version.matches("5.x.x"));
		
		assertTrue(version.matches("x.5.7"));
		assertFalse(version.matches("x.5.6"));
		assertTrue(version.matches("x.5.x"));
		assertFalse(version.matches("x.6.7"));
		assertFalse(version.matches("x.6.6"));
		assertFalse(version.matches("x.6.x"));
		assertTrue(version.matches("x.x.7"));
		assertFalse(version.matches("x.x.6"));
		assertTrue(version.matches("x.x.x"));	
	}
}
