package com.ironiacorp.network.protocol.slp;

import static org.junit.Assert.*;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.io.IoUtil;
import com.ironiacorp.network.protocol.slp.SLPMessage;

public class SLPMessageTest
{

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testProcessPacket() throws Exception
	{
		byte[] data = IoUtil.dumpFile("/media/magsilva/Downloads-August/BroadcastListener/test-resources/packets/slp/SLP-UnknownMessage3-CmapServer-5.04.03.dump");
		SLPMessage msg = new SLPMessage();
		msg.parse(data);
		assertEquals(Locale.ENGLISH, msg.getLocale());
	}
}
