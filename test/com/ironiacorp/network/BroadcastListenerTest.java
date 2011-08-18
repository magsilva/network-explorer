package com.ironiacorp.network;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.network.tool.listener.BroadcastListener;

public class BroadcastListenerTest
{
	private BroadcastListener listener;
	

	@Before
	public void setUp() throws Exception
	{
		listener = new BroadcastListener();
	}

	@Test
	public void testListen()
	{
		listener.setPort(4747);
//		listener.setAddress("255.255.255.255");
//		listener.setAddress("192.168.1.120");
		listener.setAddress("0.0.0.0");
		listener.listen();
	}

}
