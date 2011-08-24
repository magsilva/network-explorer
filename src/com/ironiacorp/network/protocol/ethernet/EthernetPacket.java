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

package com.ironiacorp.network.protocol.ethernet;

import com.ironiacorp.ecc.crc32.CRC32_Direct;
import com.ironiacorp.io.StreamUtil;
import com.ironiacorp.network.protocol.Packet;


public class EthernetPacket implements Packet
{
	public static final int PREAMBLE_SIZE = 7;
	public static final short[] PREAMBLE_DATA = {170, 170, 170, 170, 170, 170, 170};
	
	public static final int START_OF_FRAME_DELIMITER_OFFSET = PREAMBLE_SIZE;
	public static final int START_OF_FRAME_DELIMITER_SIZE = 1;
	public static final short START_OF_FRAME_DELIMITER_DATA = 171;
	
	public static final int MAC_ADDRESS_SIZE = 6;
	
	public static final int DESTINATION_ADDRESS_OFFSET = 8;
	public static final int SOURCE_ADDRESS_OFFSET = DESTINATION_ADDRESS_OFFSET + MAC_ADDRESS_SIZE;
	
	public static final int ETHERTYPE_SIZE = 2;
	public static final int ETHERTYPE_OFFSET = SOURCE_ADDRESS_OFFSET + MAC_ADDRESS_SIZE;
	//	public static final int ETHERTYPE_OFFSET = TAG_OFFSET + TAG_SIZE;
	public static final int ETHERTYPE_IPV4 = 0x800;
	public static final int ETHERTYPE_ARP = 0x806;
	public static final int ETHERTYPE_802_1Q = 0x8100;
	public static final int ETHERTYPE_IPV6 = 0x86DD;

	
	public static final int PAYLOAD_OFFSET = ETHERTYPE_OFFSET + ETHERTYPE_SIZE;
	public static final int MAX_PAYLOAD_SIZE = 1500;
	
	public static final int CRC_SIZE = 4;
	
	public static final int INTERFRAME_GAP_SIZE = 12;
	

	private MacAddress source;
	
	private MacAddress destination;

	private byte[] payload;

	@Override
	public int getHeaderSize()
	{
		int size = PREAMBLE_SIZE + START_OF_FRAME_DELIMITER_SIZE + (MAC_ADDRESS_SIZE * 2) + ETHERTYPE_SIZE;
		return size;
	}

	@Override
	public int getTrailerSize() 
	{
		return CRC_SIZE;
	}

	@Override
	public int getLength()
	{
		return getHeaderSize() + payload.length + getTrailerSize();
	}
	
	public MacAddress getSource() {
		return source;
	}

	public void setSource(MacAddress source) {
		this.source = source;
	}

	public MacAddress getDestination() {
		return destination;
	}

	public void setDestination(MacAddress destination) {
		this.destination = destination;
	}

	public void parse(byte[] data)
	{
		byte[] preamble = new byte[7];
		System.arraycopy(data,  0, preamble, 0, PREAMBLE_SIZE);
		for (int i = 0; i < PREAMBLE_SIZE; i++) {
			if (preamble[i] != PREAMBLE_DATA[i]) {
				throw new IllegalArgumentException("Invalid preamble");
			}
		}
		
		byte delimiter = data[START_OF_FRAME_DELIMITER_OFFSET];
		if (delimiter != START_OF_FRAME_DELIMITER_DATA) {
			throw new IllegalArgumentException("Invaild start of frame delimiter");
		}
		
		byte[] address = new byte[MAC_ADDRESS_SIZE];
		System.arraycopy(data, SOURCE_ADDRESS_OFFSET, address, 0, MAC_ADDRESS_SIZE);
		destination = new MacAddress(address);
		System.arraycopy(data, SOURCE_ADDRESS_OFFSET, address, 0, MAC_ADDRESS_SIZE);
		source = new MacAddress(address);
		
		int sizeOrType = StreamUtil.readInt(data, ETHERTYPE_OFFSET, ETHERTYPE_SIZE);
		if (sizeOrType < 1500) {
			payload = new byte[sizeOrType];
			System.arraycopy(data, PAYLOAD_OFFSET, payload, 0, payload.length);
		}
		
		CRC32_Direct crc32 = new CRC32_Direct();
		byte[] crc32original = new byte[CRC_SIZE];
		byte[] crc32new = crc32.calculate(payload);
		System.arraycopy(data, PAYLOAD_OFFSET, crc32original, 0, CRC_SIZE);
		for (int i = 0; i < CRC_SIZE; i++) {
			if (crc32original[i] != crc32new[i]) {
				throw new IllegalArgumentException("Invalid CRC32 code");
			}
		}
	}
}

