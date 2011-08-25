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

package com.ironiacorp.network.tool.listener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

import com.ironiacorp.patterns.observer.ChangeSet;
import com.ironiacorp.patterns.observer.ObjectChange;
import com.ironiacorp.patterns.observer.ObservationSubject;
import com.ironiacorp.patterns.observer.Observer;
import com.ironiacorp.patterns.observer.ThreadSafeObservationSubject;


/**
 * Broadcast listener.
 * 
 * Listen for broadcast (thus, UDP -- aka Datagram -- packets) on every network
 * interface (we really would like to restrict the listening to just one
 * interface, but Java is way limited in low-level network configuration).
 * 
 * Further information on broadcast support at:
 * <ul>
 * 		<li>http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4212324</li>
 * 		<li>http://stackoverflow.com/questions/835960/java-on-linux-listening-to-broadcast-messages-on-a-bound-local-address</li>
 * 		<li>http://stackoverflow.com/questions/2950715/udp-broadcast-in-java</li>
 * </ul>
 */
public class BroadcastListener extends IpListener implements ObservationSubject
{
	private static final int DEFAULT_TIMEOUT = 2000;
	
	private ThreadSafeObservationSubject observationSubject;
	
	private LinkedBlockingQueue<DatagramPacket> packetQueue;

	private boolean enabled;
	
	private ListenerThread listeningThread;
	private Thread listeningThreadHandler;
	
	private ProcessingThread processingThread;
	private Thread processingThreadHandler;

	private class ListenerThread implements Runnable
	{
		private int currentReceptionErrors; 
		
		private int totalReceptionErrors;
		
		public ListenerThread()
		{
			this.currentReceptionErrors = 0;
			this.totalReceptionErrors = 0;
		}
		
		/**
		 * Listen for broadcast on the specified port.
		 * 
		 * Please, do not change any single line of code in this method. Java
		 * network support is a nightmare, awfully implemented.
		 */
		public void listen()
		{
			byte[] buffer = new byte[bufferSize];
			try {
				DatagramSocket socket = new DatagramSocket(null); // Do not try anything else than 'null' as argument, otherwise it will not work properly
				InetSocketAddress socketAddress = new InetSocketAddress(port);
				DatagramPacket packet;
				
				// socket.setBroadcast(true); // Do not enable, it is useless
				socket.setReuseAddress(true);
				if (socket.getReuseAddress()) {
					// Socket reuse implementation on Java is stupid, do not care about it
					// throw new UnsupportedOperationException("Current JVM implementation cannot reuse ports");
				}
				socket.bind(socketAddress);
				while (enabled) {
					try {
						if (currentReceptionErrors > 0) {
							Thread.sleep((currentReceptionErrors << 2) * 100);
						}
						packet = new DatagramPacket(buffer, buffer.length);
						socket.receive(packet);
						if (packet.getLength() < buffer.length) {
							byte[] newbuf = new byte[packet.getLength()];
							System.arraycopy(buffer, 0, newbuf, 0, newbuf.length);
							packet.setData(newbuf);
							if (buffer.length > (2 * packet.getLength())) {
								System.out.println("Buffer is to big, consider reducing it (" + buffer.length + "/" + packet.getLength() + ")");
							}
						} else {
							System.out.println("Possibly truncate data");
						}
						packetQueue.add(packet);
						currentReceptionErrors = 0;
					} catch (IOException ioe) {
						currentReceptionErrors++;
						totalReceptionErrors++;
					} catch (InterruptedException ie) {
					}
				}
			} catch (SocketException se) {
				throw new IllegalArgumentException("Cannot listen to port " + port + " at address " + address, se);
			}
		}

		@Override
		public void run() {
			listen();
		}
	}
	
	private class ProcessingThread implements Runnable
	{
		private BroadcastListener listener;
		
		public ProcessingThread(BroadcastListener listener)
		{
			this.listener = listener;
		}
		
		@Override
		public void run()
		{
			while (enabled) {
				try {
					DatagramPacket packet = packetQueue.take();
					ObjectChange change = new ObjectChange(packet);
					ChangeSet changeSet = new ChangeSet(listener);
					changeSet.add(change);
					notifyObservers(changeSet);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	
	public BroadcastListener()
	{
		super();
		setAddress("0.0.0.0"); // Not actually required by the current implementation
		observationSubject = new ThreadSafeObservationSubject();
		packetQueue = new LinkedBlockingQueue<DatagramPacket>();
		enabled = false;
	}
	
	@Override
	public void addObserver(Observer observer)
	{
		observationSubject.addObserver(observer);
	}

	@Override
	public void deleteObserver(Observer observer)
	{
		observationSubject.deleteObserver(observer);		
	}

	@Override
	public void notifyObservers()
	{
		observationSubject.notifyObservers();
	}

	@Override
	public void notifyObservers(ChangeSet changeSet)
	{
		observationSubject.notifyObservers(changeSet);		
	}

	@Override
	public synchronized void startListening()
	{
		enabled = true;
		
		if (listeningThread == null) {
			listeningThread = new ListenerThread();
		}
		
		listeningThreadHandler = new Thread(listeningThread);
		listeningThreadHandler.start();

		if (processingThread == null) {
			processingThread = new ProcessingThread(this);
		}
		processingThreadHandler = new Thread(processingThread);
		processingThreadHandler.start();
	}
	
	public synchronized void stopListening()
	{
		enabled = false;
		
		if (listeningThreadHandler != null) {
			try {
				listeningThreadHandler.interrupt();
				listeningThreadHandler.join(DEFAULT_TIMEOUT);
				// We try to interrupt, but it is not possible to interrupt I/O calls...
				if (listeningThreadHandler.isAlive()) {
					listeningThreadHandler.stop();
				}
			} catch (InterruptedException e) {
			}
		}
		listeningThreadHandler = null;
		
		if (processingThread != null) {
			try {
				processingThreadHandler.interrupt();
				processingThreadHandler.join(DEFAULT_TIMEOUT);
				// We try to interrupt, but it is not possible to interrupt I/O calls...
				if (processingThreadHandler.isAlive()) {
					processingThreadHandler.stop();
				}
			} catch (InterruptedException e) {
			}
		}
		processingThread = null;
	}
}
