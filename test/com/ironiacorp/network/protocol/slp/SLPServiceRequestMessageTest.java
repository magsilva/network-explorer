/*
Copyright (C) 2011 Marco Aurélio Graciotto Silva <magsilva@ironiacorp.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package com.ironiacorp.network.protocol.slp;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.io.IoUtil;
import com.ironiacorp.network.protocol.slp.SLPServiceRequestMessage;

public class SLPServiceRequestMessageTest
{

	@Before
	public void setUp() throws Exception
	{}

	@Test
	public void testProcessPacket() throws Exception
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("packets/slp/SLP-ServiceRequestMessage-CmapServer-5.04.03.dump");
		byte[] data = IoUtil.toByteArray(is);
		SLPServiceRequestMessage msg = new SLPServiceRequestMessage();
		msg.parse(data);
		assertEquals(Locale.ENGLISH, msg.getLocale());
	}
	
	@Test
	public void testProcessPacket2() throws Exception
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("packets/slp/SLP-ServiceRequestMessage-Indexer-CmapServer-5.04.03.dump");
		byte[] data = IoUtil.toByteArray(is);
		SLPServiceRequestMessage msg = new SLPServiceRequestMessage();
		msg.parse(data);
		assertEquals(Locale.ENGLISH, msg.getLocale());
	}

	@Test
	public void testProcessPacket3() throws Exception
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("packets/slp/SLP-ServiceRequestMessage-wsServerCXL-CmapServer-5.04.03.dump");
		byte[] data = IoUtil.toByteArray(is);
		SLPServiceRequestMessage msg = new SLPServiceRequestMessage();
		msg.parse(data);
		assertEquals(Locale.ENGLISH, msg.getLocale());
	}
	
	@Test
	public void testProcessPacket4() throws Exception
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("packets/slp/SLP-ServiceRequestMessage-cmapV3-CmapServer-5.04.03.dump");
		byte[] data = IoUtil.toByteArray(is);
		SLPServiceRequestMessage msg = new SLPServiceRequestMessage();
		msg.parse(data);
		assertEquals(Locale.ENGLISH, msg.getLocale());
	}
}
