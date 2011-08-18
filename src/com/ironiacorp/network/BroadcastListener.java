package com.ironiacorp.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import com.ironiacorp.network.protocol.slp.SLPAnalyzer;
import com.ironiacorp.network.protocol.slp.SLPMessage;


// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4212324
// http://stackoverflow.com/questions/835960/java-on-linux-listening-to-broadcast-messages-on-a-bound-local-address
// http://stackoverflow.com/questions/2950715/udp-broadcast-in-java
public class BroadcastListener extends IpListener
{
	public void listen()
	{
		byte[] buffer = new byte[bufferSize];
		try {
			DatagramSocket socket = new DatagramSocket(null);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			InetSocketAddress socketAddress = new InetSocketAddress(port);
			SLPAnalyzer analyzer = new SLPAnalyzer();
			// socket.setBroadcast(true);
			socket.setReuseAddress(true);
			if (socket.getReuseAddress()) {
				// throw new UnsupportedOperationException("Current JVM implementation cannot reuse ports");
			}
			// socket.setSoTimeout(100000);
			// socket.connect(socketAddress);
			socket.bind(socketAddress);
			while (true) {
				socket.receive(packet);
				SLPMessage msg = analyzer.parse(packet.getData());
				if (msg != null) {
					System.out.println("SUCCESS: " + packet.getLength() + msg.getFunction().toString());
				} else {
					System.out.println("FAILURE: " + packet.getLength());
				}
			}
		} catch (SocketException se) {
			throw new IllegalArgumentException("Cannot listen to port " + port + " at address " + address, se);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Cannot receive package from port " + port + " at address " + address, ioe);
		}
	}
	
}
