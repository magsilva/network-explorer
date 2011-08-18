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
