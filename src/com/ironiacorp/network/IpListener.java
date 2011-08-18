package com.ironiacorp.network;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class IpListener implements NetworkListener
{
	public static final int DEFAULT_BUFFER_SIZE = 1600;

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

	
	public abstract void listen();
}