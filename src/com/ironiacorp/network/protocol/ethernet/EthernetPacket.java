package com.ironiacorp.network.protocol.ethernet;

import java.net.InetAddress;

import com.ironiacorp.io.StreamUtil;
import com.ironiacorp.network.protocol.Packet;
import com.ironiacorp.network.protocol.ip.IPPacket;
import com.ironiacorp.network.protocol.tcp.TCPPacket;
import com.ironiacorp.network.protocol.udp.UDPPacket;

public class EthernetPacket implements Packet
{
	public static final int etherHeaderLength = 14;
	public static final int etherTypeOffset = 12;
	public static final int etherTypeIP = 0x800;

	public static final int verIHLOffset = 14;
	public static final int ipProtoOffset = 23;
	public static final int ipSrcOffset = 26;
	public static final int ipDstOffset = 30;

	public static final int ipProtoTCP = 6;
	public static final int ipProtoUDP = 17;

	public static final int udpHeaderLength = 8;
	
	private MacAddress source;
	
	private MacAddress destination;

	
	private boolean isIPPacket(byte[] packet)
	{
		int etherType = StreamUtil.convertShort(packet, etherTypeOffset);
		if (etherType != etherTypeIP) {
			return false;
		}
		return true;
	}

	private boolean isUDPPacket(byte[] packet)
	{
		if (!isIPPacket(packet))
			return false;
		return packet[ipProtoOffset] == ipProtoUDP;
	}

	private boolean isTCPPacket(byte[] packet)
	{
		if (!isIPPacket(packet))
			return false;
		return packet[ipProtoOffset] == ipProtoTCP;
	}

	private int getIPHeaderLength(byte[] packet)
	{
		return (packet[verIHLOffset] & 0xF) * 4;
	}

	private int getTCPHeaderLength(byte[] packet)
	{
		final int inTCPHeaderDataOffset = 12;

		int dataOffset = etherHeaderLength + this.getIPHeaderLength(packet)
				+ inTCPHeaderDataOffset;
		return ((packet[dataOffset] >> 4) & 0xF) * 4;
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

	private UDPPacket buildUDPPacket(byte[] packet)
	{
		final int inUDPHeaderSrcPortOffset = 0;
		final int inUDPHeaderDstPortOffset = 2;

		UDPPacket udpPacket = new UDPPacket(this.buildIPPacket(packet));

		int srcPortOffset = etherHeaderLength + this.getIPHeaderLength(packet)
				+ inUDPHeaderSrcPortOffset;
		udpPacket.src_port = StreamUtil.convertShort(packet, srcPortOffset);

		int dstPortOffset = etherHeaderLength + this.getIPHeaderLength(packet)
				+ inUDPHeaderDstPortOffset;
		udpPacket.dst_port = StreamUtil.convertShort(packet, dstPortOffset);

		int payloadDataStart = etherHeaderLength + this.getIPHeaderLength(packet)
				+ udpHeaderLength;
		byte[] data = new byte[packet.length - payloadDataStart];
		System.arraycopy(packet, payloadDataStart, data, 0, data.length);
		udpPacket.data = data;

		return udpPacket;
	}

	private TCPPacket buildTCPPacket(byte[] packet)
	{
		final int inTCPHeaderSrcPortOffset = 0;
		final int inTCPHeaderDstPortOffset = 2;

		TCPPacket tcpPacket = new TCPPacket(this.buildIPPacket(packet));

		int srcPortOffset = etherHeaderLength + this.getIPHeaderLength(packet)
				+ inTCPHeaderSrcPortOffset;
		tcpPacket.src_port = StreamUtil.convertShort(packet, srcPortOffset);

		int dstPortOffset = etherHeaderLength + this.getIPHeaderLength(packet)
				+ inTCPHeaderDstPortOffset;
		tcpPacket.dst_port = StreamUtil.convertShort(packet, dstPortOffset);

		int payloadDataStart = etherHeaderLength + this.getIPHeaderLength(packet)
				+ this.getTCPHeaderLength(packet);
		byte[] data = new byte[packet.length - payloadDataStart];
		System.arraycopy(packet, payloadDataStart, data, 0, data.length);
		tcpPacket.data = data;

		return tcpPacket;
	}

}
