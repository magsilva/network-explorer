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

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.io.IoUtil;
import com.ironiacorp.network.protocol.slp.SLPAnalyzer;
import com.ironiacorp.network.protocol.slp.SLPMessage;
import com.ironiacorp.network.protocol.slp.SLPServiceRequestMessage;

public class SLPAnalyzerTest
{
	private SLPAnalyzer analyzer;
	
	@Before
	public void setUp() throws Exception
	{
		analyzer = new SLPAnalyzer();
	}

	@Test
	public void testParse()
	{
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("packets/slp/SLP-ServiceRequestMessage-CmapServer-5.04.03.dump");
		byte[] data = IoUtil.toByteArray(is);
		SLPMessage msg = analyzer.parse(data);
		assertTrue(msg instanceof SLPServiceRequestMessage);
	}

}
