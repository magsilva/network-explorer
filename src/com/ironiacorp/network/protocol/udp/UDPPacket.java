package com.ironiacorp.network.protocol.udp;

import com.ironiacorp.io.StreamUtil;
import com.ironiacorp.network.protocol.Packet;
import com.ironiacorp.network.protocol.ip.IPPacket;


public class UDPPacket implements Packet
{
	public static final int ipProtoUDP = 17;
	
	public static final int udpHeaderLength = 8;

	
	private int sourcePort;
	
	private int destinationPort;
	
	byte[] payload;

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
	
	public void parse(byte[] packet)
	{
		final int inUDPHeaderSrcPortOffset = 0;
		final int inUDPHeaderDstPortOffset = 2;

		int srcPortOffset = inUDPHeaderSrcPortOffset;
		sourcePort = StreamUtil.convertShort(packet, srcPortOffset);

		int dstPortOffset = inUDPHeaderDstPortOffset;
		destinationPort = StreamUtil.convertShort(packet, dstPortOffset);

		int payloadDataStart = udpHeaderLength;
		payload = new byte[packet.length - payloadDataStart];
		System.arraycopy(packet, payloadDataStart, payload, 0, payload.length);
	}

	@Override
	public int getHeaderSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTrailerSize() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}
}
