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

package com.ironiacorp.network;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.network.protocol.slp.SLPMessage;
import com.ironiacorp.network.protocol.slp.SLPServiceRequestMessage;
import com.ironiacorp.network.tool.listener.BroadcastListener;
import com.ironiacorp.patterns.observer.Change;
import com.ironiacorp.patterns.observer.ChangeSet;
import com.ironiacorp.patterns.observer.ObjectChange;
import com.ironiacorp.patterns.observer.Observer;

public class BroadcastListenerTest
{
	private BroadcastListener listener;
	
	private class DatagramPacketDumper implements Observer
	{
		@Override
		public void notify(ChangeSet changeSet)
		{
			Iterator<Change> i = changeSet.iterator();
			while (i.hasNext()) {
				ObjectChange change = (ObjectChange) i.next();
				DatagramPacket packet = (DatagramPacket) change.getObject();
				System.out.println(packet.getAddress() + ":" + packet.getPort() + "[" + packet.getLength() + "]" + packet.getData());
				
				SLPMessage msg = new SLPMessage();
				msg.parse(packet.getData());
				System.out.println(msg.toString());
				
				SLPServiceRequestMessage msg2 = new SLPServiceRequestMessage();
				msg2.parse(packet.getData());
				System.out.println(msg.toString());
			}
		}
		
	}
	
	
	@Before
	public void setUp() throws Exception
	{
		listener = new BroadcastListener();
	}

	@Test
	public void testListen() throws Exception
	{
		listener.setPort(4747);
		listener.addObserver(new DatagramPacketDumper());
		listener.startListening();
		Thread.sleep(90000);
		listener.stopListening();
	}
}
