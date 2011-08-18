package com.ironiacorp.network.protocol.ethernet;

import com.ironiacorp.network.protocol.Packet;
import com.ironiacorp.network.tool.PacketAnalyzer;

public class EthernetAnalyzer implements PacketAnalyzer
{
	
	@Override
	public Packet parse(byte[] data)
	{
		EthernetPacket packet = new EthernetPacket();
		
		
		return packet;
	}
	
}
