package com.ironiacorp.network.protocol.slp;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.io.IoUtil;
import com.ironiacorp.network.protocol.slp.SLPServiceReplyMessage;

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
		msg.parse(data);
		assertEquals(Locale.ENGLISH, msg.getLocale());
	}
}
