package com.ironiacorp.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;

// http://download.oracle.com/javase/tutorial/networking/datagrams/broadcasting.html
public class MulticastListener extends IpListener
{
	public void listen()
	{
		byte[] buffer = new byte[bufferSize];
		try {
			MulticastSocket socket = new MulticastSocket(port);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.joinGroup(address);
			socket.setReuseAddress(true);
			socket.setSoTimeout(5000);
			socket.receive(packet);
			System.out.println(packet.getLength() + packet.getData().toString());
		} catch (SocketException se) {
			throw new IllegalArgumentException("Cannot listen to port " + port + " at address " + address, se);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Cannot receive package from port " + port + " at address " + address, ioe);
		}
	}
}
