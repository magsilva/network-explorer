package com.ironiacorp.network.protocol.udp;

import com.ironiacorp.network.protocol.ip.IPPacket;


public class UDPPacket extends IPPacket
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
