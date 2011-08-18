package com.ironiacorp.network;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.ironiacorp.network.PcapParser;
import com.ironiacorp.network.protocol.ip.IPPacket;
import com.ironiacorp.network.protocol.tcp.TCPPacket;
import com.ironiacorp.network.protocol.udp.UDPPacket;

import edu.gatech.sjpcap.packet.Packet;

public class PcapParserTest
{
	private PcapParser parser;

	@Before
	public void setUp() throws Exception
	{
		parser = new PcapParser();
		parser.openFile("/home/magsilva/Projects-ICMC/NetworkAnalyzer/resources/tcpdump.dump");
	}

	@Test
	public void testTcpdumpReader()
	{
		Packet packet = null;

		while ((packet = parser.getPacket()) != Packet.EOF) {
			assertNotNull(packet);
			if (packet instanceof TCPPacket) {
				TCPPacket tcp = (TCPPacket) packet;
				assertNotNull(tcp);
			} else if (packet instanceof UDPPacket) {
				UDPPacket udp = (UDPPacket) packet;
				assertNotNull(udp);
			} else if (packet instanceof IPPacket) {
				IPPacket ip = (IPPacket) packet;
				assertNotNull(ip);
			} else {
				assertNotNull(packet);
			}
		}
	}
}
