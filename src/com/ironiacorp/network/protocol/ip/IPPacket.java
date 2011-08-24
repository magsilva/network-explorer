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

package com.ironiacorp.network.protocol.ip;

import java.net.InetAddress;

import com.ironiacorp.io.StreamUtil;
import com.ironiacorp.network.protocol.Packet;
import com.ironiacorp.network.protocol.ethernet.EthernetPacket;


public class IPPacket implements Packet
{
	public static final int ipProtoTCP = 6;

	public static final int ipProtoOffset = 23;
	public static final int ipSrcOffset = 26;
	public static final int ipDstOffset = 30;
	public static final int verIHLOffset = 0;

	
    private InetAddress source;
    
    private InetAddress destination;

	private int getIPHeaderLength(byte[] packet)
	{
		return (packet[verIHLOffset] & 0xF) * 4;
	}  
    
	public InetAddress getSource()
	{
		return source;
	}

	public void setSource(InetAddress source)
	{
		this.source = source;
	}

	public InetAddress getDestination()
	{
		return destination;
	}

	public void setDestination(InetAddress destination)
	{
		this.destination = destination;
	}
	
	private IPPacket buildIPPacket(byte[] packet)
	{
		IPPacket ipPacket = new IPPacket();

		byte[] srcIP = new byte[4];
		System.arraycopy(packet, ipSrcOffset, srcIP, 0, srcIP.length);
		try {
			ipPacket.setSource(InetAddress.getByAddress(srcIP));
		} catch (Exception e) {
			return null;
		}

		byte[] dstIP = new byte[4];
		System.arraycopy(packet, ipDstOffset, dstIP, 0, dstIP.length);
		try {
			ipPacket.setDestination(InetAddress.getByAddress(dstIP));
		} catch (Exception e) {
			return null;
		}

		return ipPacket;
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
