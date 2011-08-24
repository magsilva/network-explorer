package com.ironiacorp.network.protocol.tcp;

import com.ironiacorp.io.StreamUtil;
import com.ironiacorp.network.protocol.Packet;
import com.ironiacorp.network.protocol.ip.IPPacket;


public class TCPPacket implements Packet
{
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

	private int getTCPHeaderLength(byte[] packet)
	{
		final int inTCPHeaderDataOffset = 12;

		int dataOffset = inTCPHeaderDataOffset;
		return ((packet[dataOffset] >> 4) & 0xF) * 4;
	}

	public void parse(byte[] packet)
	{
		final int inTCPHeaderSrcPortOffset = 0;
		final int inTCPHeaderDstPortOffset = 2;

		int srcPortOffset = inTCPHeaderSrcPortOffset;
		sourcePort = StreamUtil.convertShort(packet, srcPortOffset);

		int dstPortOffset = inTCPHeaderDstPortOffset;
		destinationPort = StreamUtil.convertShort(packet, dstPortOffset);

		int payloadDataStart = this.getTCPHeaderLength(packet);
		payload = new byte[packet.length - payloadDataStart];
		System.arraycopy(packet, payloadDataStart, packet, 0, packet.length);
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
