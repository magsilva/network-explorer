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

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class IpListener implements NetworkListener
{
	public static final int DEFAULT_BUFFER_SIZE = 1500;

	protected int port;
	
	protected int bufferSize = DEFAULT_BUFFER_SIZE;

	protected InetAddress address;
	
	public IpListener() {
		super();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public void setAddress(String ipAddress)
	{
		try {
			address = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid IP address", e);
		}
	}
}