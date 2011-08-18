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

package com.ironiacorp.network.analyzer.slp;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Locale;

import com.ironiacorp.datastructure.array.ArrayUtil;
import com.ironiacorp.io.StreamUtil;
import com.ironiacorp.network.NetworkProtocol;
import com.ironiacorp.network.TransportProtocol;
import com.ironiacorp.network.analyzer.Packet;

/**
 * SLP message. This class should not be directly used: its subclass
 * are better candidates. Nonetheless, if just information directly
 * related to basic SLP functions (those that can be read from its
 * header) are required, this class should be enough.
 */
public class SLPMessage implements Packet
{
	/**
	 * Message flag.
	 */
	public enum Flag
	{
		OVERFLOW(0x8),
		FRESH(0x4),
		REQUEST_MCAST(0x2);
		
		public final int code;
		
		private Flag(int code)
		{
			this.code = code;
		}
	}

	/**
	 * Message types or functions.
	 */
	public enum Function
	{
		SRVRQST((byte) 1, "Service Request"),
		SRVRPLY((byte) 2, "Service Reply"),
		SRVREG((byte) 3, "Service Registration"),
		SRVDEREG((byte) 4, "Service Deregistration"),
		SRVACK((byte) 5, "Service Acknowledgement"),
		ATTRRQST((byte) 6, "Attribute Request"),
		ATTRRPLY((byte) 7, "Attribute Reply"),
		DAADVERT((byte) 8, "DA Advertisement"),
		SRVTYPERQST((byte) 9, "Service Type Request"),
		SRVTYPERPLY((byte) 10, "Service Type Reply"),
		SAADVERT((byte) 11, "SA Advertisement");
		
		public final byte id;
		
		public final String name;
		
		private Function(byte id, String name)
		{
			this.id = id;
			this.name = name;
		}
	}

	public static final int HEADER_SIZE = 16;
	
	public static final int VERSION_OFFSET = 0;
	public static final int VERSION_SIZE = 1;
	
	public static final int FUNCTION_OFFSET = 1;
	public static final int FUNCTION_SIZE = 1;
	
	public static final int LENGTH_OFFSET = 2;
	public static final int LENGTH_SIZE = 3;

	public static final int FLAGS_OFFSET = 5;
	public static final int FLAGS_SIZE = 1;
	
	public static final int RESERVED_OFFSET = 5;
	public static final int RESERVED_SIZE = 2;
	
	public static final int EXTENSION_OFFSET = 7;
	public static final int EXTENSION_SIZE = 3;
	
	public static final int TRANSACTION_ID_OFFSET = 10;
	public static final int TRANSACTION_ID_SIZE = 2;
	
	public static final int LANGUAGE_LENGTH_OFFSET = 12;
	public static final int LANGUAGE_LENGTH_SIZE = 2;

	public static final int LANGUAGE_OFFSET = 14;
	public static final int LANGUAGE_SIZE = 2;

	
	public static final TransportProtocol transportProtocol = TransportProtocol.IP;
	
	private NetworkProtocol networkProtocol;
	
	public static final byte[] VALID_VERSIONS = {1, 2};
	
	private byte version;
	
	/**
	 * Length of the entire SLP message (header included).
	 */
	private int length;
	
	/**
	 * Function of the message.
	 */
	private Function function;

	/**
	 * Locale of the message.
	 */
	private Locale locale;

	/**
	 * the transaction ID.
	 */
	private int transactionId;

	/**
	 * Source address of the package.
	 */
	private InetAddress sourceAddress;
	
	/**
	 * Source port of the package.
	 */
	private int sourcePort;
	

	/**
	 * Destination address of the package
	 */
	private InetAddress destinationAddress;

	/**
	 * the sender or receiver port.
	 */
	private int destinationPort;
	
	/**
	 * Set when the message length exceeds what can fit into a datagram.
	 */
	private boolean overflow;
	
	/**
	 * Configured on every Service Registration message.
	 */
	private boolean fresh;
	
	/**
	 * Identify whether the message was sent using multicasting or broadcasting.
	 */
	private boolean multicast;
	
	
	public NetworkProtocol getNetworkProtocol()
	{
		return networkProtocol;
	}

	public void setNetworkProtocol(NetworkProtocol networkProtocol)
	{
		this.networkProtocol = networkProtocol;
	}

	public byte getVersion()
	{
		return version;
	}

	public void setVersion(byte version)
	{
		this.version = version;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public Function getFunction()
	{
		return function;
	}

	public void setFunction(Function function)
	{
		this.function = function;
	}

	public Locale getLocale()
	{
		return locale;
	}

	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	public int getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(int transactionId)
	{
		this.transactionId = transactionId;
	}

	public InetAddress getSourceAddress()
	{
		return sourceAddress;
	}

	public void setSourceAddress(InetAddress sourceAddress)
	{
		this.sourceAddress = sourceAddress;
	}

	public int getSourcePort()
	{
		return sourcePort;
	}

	public void setSourcePort(int sourcePort)
	{
		this.sourcePort = sourcePort;
	}

	public InetAddress getDestinationAddress()
	{
		return destinationAddress;
	}

	public void setDestinationAddress(InetAddress destinationAddress)
	{
		this.destinationAddress = destinationAddress;
	}

	public int getDestinationPort()
	{
		return destinationPort;
	}

	public void setDestinationPort(int destinationPort)
	{
		this.destinationPort = destinationPort;
	}

	private void processVersion(byte[] packet)
	{
		version = packet[VERSION_OFFSET];
		if (! ArrayUtil.has(VALID_VERSIONS, version)) {
			throw new IllegalArgumentException("Invalid version, bailing out: " + version);
		}
	}
	
	private void processFunction(byte[] packet)
	{
		byte functionId = packet[FUNCTION_OFFSET];
		for (Function f : Function.values()) {
			if (functionId == f.id) {
				function = f;
				return;
			}
		}
		
		throw new IllegalArgumentException("Invalid function: " + functionId);
	}


	private void processLength(byte[] packet)
	{
		length = StreamUtil.readInt(packet, LENGTH_OFFSET, LENGTH_SIZE);
		if (length < 0) {
			throw new IllegalArgumentException("Invalid package length: " + length);
		}
	}

	private void processFlags(byte[] packet)
	{
		// TODO: Fix it
		int flags = (byte) (packet[FLAGS_OFFSET] >> 5);
		overflow = (flags & Flag.OVERFLOW.code) != 0;
		multicast = (flags & Flag.REQUEST_MCAST.code) != 0;
		fresh = (flags & Flag.FRESH.code) != 0;
	}
	
	private void processReservedBits(byte[] packet)
	{
		int reserved = StreamUtil.readInt(packet, RESERVED_OFFSET, RESERVED_SIZE) << 3;
		reserved = reserved >> 3;
		if (reserved != 0) {
			// throw new IllegalArgumentException("Invalid package (reserved data not zeroed)");
		}
	}

	private void processExtensionOffset(byte[] packet)
	{
		int extensionOffset = StreamUtil.readInt(packet, EXTENSION_OFFSET, EXTENSION_SIZE);
	}
	
	private void processTransactionId(byte[] packet)
	{
		transactionId = (short) StreamUtil.readInt(packet, TRANSACTION_ID_OFFSET, TRANSACTION_ID_SIZE);
	}

	private void processLocale(byte[] packet)
	{
		short languageLength = (short) StreamUtil.readInt(packet, LANGUAGE_LENGTH_OFFSET, LANGUAGE_LENGTH_SIZE);
		byte[] languageData = new byte[languageLength];
		String language;
		
		for (int i = 0, j = LANGUAGE_OFFSET; i < languageLength; i++, j++) {
			languageData[i] = packet[j];
		}
		language = new String(languageData, Charset.forName("UTF-8"));
		locale = new Locale(language);
		if (locale == null) {
			throw new IllegalArgumentException("Invalid locale: " + language);
		}
	}
	
	
	/**
	 * The RFC 2608 SLP message header:
	 * 
	 * <pre>
	 *  0                   1                   2                   3
	 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |    Version    |  Function-ID  |            Length             |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * | Length, contd.|O|F|R|       reserved          |Next Ext Offset|
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |  Next Extension Offset, contd.|              XID              |
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * |      Language Tag Length      |         Language Tag          \
	 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 * 
	 * This method parses the header and then delegates the creation of the
	 * corresponding SLPMessage to the subclass that matches the funcID.
	 * 
	 * @param data Raw bytes of the message
	 * @throws IllegalArgumentException in case of any parsing errors.
	 */
	public void processPacket(byte[] packet)
	{
		processVersion(packet);
		processFunction(packet);
		processLength(packet);
		processFlags(packet);
		processReservedBits(packet);
		processExtensionOffset(packet);
		processTransactionId(packet);
		processLocale(packet);
	}
}
