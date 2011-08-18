/*
Copyright (c) 2010 Paul Royal <paulroyal@gmail.com>
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

    Redistributions of source code must retain the above copyright notice,
    this list of conditions and the following disclaimer.
    Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

/**
 * The sjpcap is a Java-only library (that do not rely on native pcap library)
 * that parses libpcap-created packet capture files to create simplified,
 * java-based object representations of IPv4-based IP, UDP and TCP packets.
 *  
 * See more at http://code.google.com/p/sjpcap/
 */
package com.ironiacorp.network;

import java.io.*;
import java.util.NoSuchElementException;

import com.ironiacorp.io.StreamUtil;
import com.ironiacorp.network.protocol.Packet;

public class PcapParser
{
	public static final long PCAP_MAGIC_NUMBER = 0xA1B2C3D4;
	
	public static final int GLOBAL_HEADER_SIZE = 24;
	
	public static final int PCAP_PACKET_HEADER_SIZE = 16;
	
	public static final int PCAP_PACKET_LENGTH_SIZE = 8;
	
	private InputStream is;

	private int readBytes(byte[] data)
	{
		int offset = 0;
		int read = -1;
		while (offset != data.length) {
			try {
				read = is.read(data, offset, data.length - offset);
			} catch (Exception e) {
				read = -1;
			}
			if (read == -1) {
				break;
			}
			offset = offset + read;
		}

		return offset;
	}

	private void readGlobalHeader()
	{
		byte[] globalHeader = new byte[GLOBAL_HEADER_SIZE];
		if (readBytes(globalHeader) == -1 || StreamUtil.convertInt(globalHeader) != PCAP_MAGIC_NUMBER) {
			throw new IllegalArgumentException("Invalid stream (it is not a PCAP dump file)");
		}
	}


	public void openFile(String path)
	{
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(new File(path));
			setInputStream(fis);
		} catch (Exception e) {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {}
			}
			throw new IllegalArgumentException("Invalid file", e);
		}
	}
	
	public void setInputStream(InputStream is)
	{
		this.is = is;
		readGlobalHeader();
	}
	
	public Packet getNext()
	{
		byte[] pcapPacketHeader = new byte[PcapParser.PCAP_PACKET_HEADER_SIZE];
		if (readBytes(pcapPacketHeader) == 0) {
			throw new NoSuchElementException();
		}
		
		long packetSize = StreamUtil.convertInt(pcapPacketHeader, PcapParser.PCAP_PACKET_LENGTH_SIZE);
		byte[] packet = new byte[(int) packetSize];
		if (readBytes(packet) == 0) {
			return null;
		}

		// TODO: Implement chaining here to detect packet type.
		
		return null;
	}
}
