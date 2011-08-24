package com.ironiacorp.network;

import static org.junit.Assert.*;

import java.net.DatagramPacket;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

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
