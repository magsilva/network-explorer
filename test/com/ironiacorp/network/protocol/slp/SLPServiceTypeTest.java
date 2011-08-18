package com.ironiacorp.network.protocol.slp;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ironiacorp.network.protocol.slp.SLPServiceType;

public class SLPServiceTypeTest
{
	@Test
	public void testAbstractServiceType()
	{
		SLPServiceType st = new SLPServiceType("tftp");
		assertTrue(st.isAbstract());
		assertEquals("tftp", st.getAbstractType());
		assertNull(st.getConcreteType());
		assertEquals("IANA", st.getNamingAuthority());
	}

	@Test
	public void testAbstractServiceTypeWithNamingAuthority()
	{
		SLPServiceType st = new SLPServiceType("x.one");
		assertTrue(st.isAbstract());
		assertEquals("x", st.getAbstractType());
		assertNull(st.getConcreteType());
		assertEquals("one", st.getNamingAuthority());
	}
	
	@Test
	public void testConcreteServiceType()
	{
		SLPServiceType st = new SLPServiceType("printer:lpr");
		assertFalse(st.isAbstract());
		assertEquals("printer", st.getAbstractType());
		assertEquals("lpr", st.getConcreteType());
		assertEquals("IANA", st.getNamingAuthority());
	}

	@Test
	public void testConcreteServiceTypeWithNamingAuthority()
	{
		SLPServiceType st = new SLPServiceType("x.one:y");
		assertFalse(st.isAbstract());
		assertEquals("x", st.getAbstractType());
		assertEquals("y", st.getConcreteType());
		assertEquals("one", st.getNamingAuthority());
	}
	
	@Test
	public void testMatches_SameWithSameNamingAuthority()
	{
		SLPServiceType st1 = new SLPServiceType("x.one:y");
		SLPServiceType st2 = new SLPServiceType("x.one:y");
		assertTrue(st1.matches(st2));
	}

	@Test
	public void testMatches_SameAbstractTypeWithDifferentNamingAuthority()
	{
		SLPServiceType st1 = new SLPServiceType("x.one");
		SLPServiceType st2 = new SLPServiceType("x.two");
		assertFalse(st1.matches(st2));
	}

	@Test
	public void testMatches_SameConcreteTypeWithDifferentNamingAuthority()
	{
		SLPServiceType st1 = new SLPServiceType("x.one:y");
		SLPServiceType st2 = new SLPServiceType("x.two:y");
		assertFalse(st1.matches(st2));
	}

	
	@Test
	public void testMatches_DifferentWithSameNamingAuthority()
	{
		SLPServiceType st1 = new SLPServiceType("z.one:y");
		SLPServiceType st2 = new SLPServiceType("x.one:y");
		assertFalse(st1.matches(st2));
	}
	
	
	@Test
	public void testMatches_MatchesAbstractConcrete()
	{
		SLPServiceType st1 = new SLPServiceType("z");
		SLPServiceType st2 = new SLPServiceType("z:y");
		assertTrue(st1.matches(st2));
	}
	
	@Test
	public void testMatches_MatchesConcrete()
	{
		SLPServiceType st1 = new SLPServiceType("printer:http");
		SLPServiceType st2 = new SLPServiceType("printer:http");
		assertTrue(st1.matches(st2));
	}
}
