package com.ironiacorp.network.analyzer.slp;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.io.IoUtil;

public class SLPServiceReplyMessageTest
{

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testProcessPacket() throws Exception
	{
		byte[] data = IoUtil.dumpFile("/home/magsilva/slp-packet");
		SLPServiceReplyMessage msg = new SLPServiceReplyMessage();
		msg.processPacket(data);
		assertEquals(Locale.ENGLISH, msg.getLocale());
	}
}
