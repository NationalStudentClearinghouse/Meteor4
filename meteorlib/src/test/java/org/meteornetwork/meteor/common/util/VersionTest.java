/*******************************************************************************
 * Copyright 2002 National Student Clearinghouse
 * 
 * This code is part of the Meteor system as defined and specified 
 * by the National Student Clearinghouse and the Meteor Sponsors.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ******************************************************************************/
package org.meteornetwork.meteor.common.util;

import junit.framework.TestCase;

import org.junit.Test;

public class VersionTest extends TestCase {

	@Test
	public void testSetVersionString() {
		Version version = new Version("4.2.1.3");
		assertEquals("4.2.1.3", version.toString());
		
		version.setVersion("6.7.8");
		assertEquals("6.7.8.0", version.toString());
		
		version.setVersion("3.5");
		assertEquals("3.5.0.0", version.toString());
		
		version.setVersion("8");
		assertEquals("8.0.0.0", version.toString());
	}

	@Test
	public void testSetVersionSequenceInteger() {
		Version version = new Version();
		version.setVersion(Version.Sequence.MAJOR, 1);
		assertEquals("1.0.0.0", version.toString());
		
		version.setVersion(Version.Sequence.MINOR, 2);
		assertEquals("1.2.0.0", version.toString());
		
		version.setVersion(Version.Sequence.RELEASE, 1);
		assertEquals("1.2.1.0", version.toString());
		
		version.setVersion(Version.Sequence.MAINTENANCE, 4);
		assertEquals("1.2.1.4", version.toString());
	}
	
	@Test
	public void testMatches() {
		Version version = new Version("6.5.7.1");
		
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
		
		assertTrue(version.matches("x.x.x.x"));
		assertTrue(version.matches("x.x"));
		assertTrue(version.matches("x"));
		
		assertTrue(version.matches("6.5.7.1"));
		assertFalse(version.matches("6.5.7.2"));
		assertTrue(version.matches("6.5.7.x"));
		assertTrue(version.matches("x.x.x.1"));
		assertFalse(version.matches("x.x.6.1"));
		assertFalse(version.matches("7.x.x.1"));
		assertFalse(version.matches("x.6.x.1"));
		assertTrue(version.matches("6.x.7.1"));
		
		assertTrue(version.matches("6.x"));
		assertTrue(version.matches("6.5"));
		assertTrue(version.matches("6"));
		assertFalse(version.matches("6.6"));
		assertFalse(version.matches("7.x"));
		assertFalse(version.matches("7"));
	}
}
