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


package com.ironiacorp.network.protocol;

import java.util.Iterator;
import java.util.Stack;


public class RawPacket implements Packet
{
	private byte[] data;
	
	private Stack<Packet> encapsulatedPackets;
	
	public RawPacket()
	{
		encapsulatedPackets = new Stack<Packet>();
	}
	
	public void setData(byte[] data)
	{
		this.data = data;
		encapsulatedPackets.clear();
	}
	
	public void push(Packet packet)
	{
		encapsulatedPackets.push(packet);
	}
	
	public Packet pop()
	{
		return encapsulatedPackets.pop();
	}
	
	public byte[] getPayload()
	{
		Iterator<Packet> i = encapsulatedPackets.iterator();
		int headerEnd = 0;
		int trailerStart = data.length;
		while (i.hasNext()) {
			Packet packet = i.next();
			headerEnd += packet.getHeaderSize();
			trailerStart -= packet.getTrailerSize();
		}
		byte[] payload = new byte[trailerStart - headerEnd];
		
		System.arraycopy(data, headerEnd, payload, 0, payload.length);
		
		return payload;
	}

	@Override
	public int getHeaderSize()
	{
		return 0;
	}

	@Override
	public int getTrailerSize()
	{
		return 0;
	}
	
	@Override
	public int getLength()
	{
		return data.length;
	}
}