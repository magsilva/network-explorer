package com.ironiacorp.network.protocol.slp;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.network.protocol.slp.SLPServiceType;
import com.ironiacorp.network.protocol.slp.SLPServiceURL;

public class SLPServiceURLTest
{

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testFullAddress()
	{
		SLPServiceURL serviceUrl = new SLPServiceURL("service:tftp://bad.glad.org:8080", 0);
		SLPServiceType serviceType = serviceUrl.getServiceType();
		assertEquals("tftp", serviceType.getAbstractType());
		assertEquals("bad.glad.org", serviceUrl.getHost());
		assertEquals(8080, serviceUrl.getPort());
	}
	
	@Test
	public void testAddress_WithoutPort()
	{
		SLPServiceURL serviceUrl = new SLPServiceURL("service:tftp://bad.glad.org", 0);
		SLPServiceType serviceType = serviceUrl.getServiceType();
		assertEquals("tftp", serviceType.getAbstractType());
		assertEquals("bad.glad.org", serviceUrl.getHost());
	}

	@Test
	public void testAddress_WithoutHost()
	{
		SLPServiceURL serviceUrl = new SLPServiceURL("service:tftp", 0);
		SLPServiceType serviceType = serviceUrl.getServiceType();
		assertEquals("tftp", serviceType.getAbstractType());
	}
	
}
