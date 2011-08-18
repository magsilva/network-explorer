package com.ironiacorp.network.protocol.tcp;

import com.ironiacorp.network.protocol.ip.IPPacket;


public class TCPPacket extends IPPacket
{
	private int sourcePort;
	
	private int destinationPort;

	public int getSourcePort()
	{
		return sourcePort;
	}

	public void setSourcePort(int sourcePort)
	{
		this.sourcePort = sourcePort;
	}

	public int getDestinationPort()
	{
		return destinationPort;
	}

	public void setDestinationPort(int destinationPort)
	{
		this.destinationPort = destinationPort;
	}
}
