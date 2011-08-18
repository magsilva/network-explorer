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

import com.ironiacorp.network.protocol.slp.SLPAnalyzer;
import com.ironiacorp.network.protocol.slp.SLPMessage;


// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4212324
// http://stackoverflow.com/questions/835960/java-on-linux-listening-to-broadcast-messages-on-a-bound-local-address
// http://stackoverflow.com/questions/2950715/udp-broadcast-in-java
public class BroadcastListener extends IpListener
{
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
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			InetSocketAddress socketAddress = new InetSocketAddress(port);
			SLPAnalyzer analyzer = new SLPAnalyzer();
			// socket.setBroadcast(true); // Do not enable, it is useless
			socket.setReuseAddress(true);
			if (socket.getReuseAddress()) {
				// Socket reuse implementation on Java is stupid, do not care about it
				// throw new UnsupportedOperationException("Current JVM implementation cannot reuse ports");
			}
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
