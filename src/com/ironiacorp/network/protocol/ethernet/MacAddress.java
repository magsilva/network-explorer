package com.ironiacorp.network.protocol.ethernet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.ironiacorp.network.UnsignedByte;

/**
 * Convenience class for storing MacAddresses and getting byte arrays and String representations
 *
 * @author pgautam
 *
 */
public class MacAddress
{
	private byte[] address;

	public MacAddress(String macAddress)
	{
		if (macAddress == null) {
			throw new IllegalArgumentException(new NullPointerException());
		}

		address = toByteArray(macAddress);
		if (address == null) {
			throw new IllegalArgumentException("Invalid MAC address: " + macAddress);
		}
	}

	public MacAddress(byte[] macAddress)
	{
		if (macAddress == null || macAddress.length != 6) {
			throw new IllegalArgumentException("Invalid MAC address");
		}

		this.address = macAddress;
	}

	private byte[] toByteArray(String macAddress)
	{
		String[] macAddr = macAddress.toUpperCase().split(":");
		byte[] ub = new byte[6];

		if (macAddr.length != 6) {
			return null;
		}
		for (int i = 0; i < macAddr.length; i++) {
			char[] octet = macAddr[i].toCharArray();
			for (int j = 0; j < octet.length; j++) {
				if (! Character.isDigit(octet[j]) && ! (octet[j] == 'A' || octet[j] == 'B' || octet[j] == 'C' || octet[j] == 'D' || octet[j] == 'E' || octet[j] == 'F')) {
					return null;
				}
			}
		}

		for (int i = 0; i < macAddr.length; i++) {
			char[] chars = macAddr[i].toCharArray();
			int c = 0;
			c = (int) (Character.isDigit(chars[0]) ? (chars[0] - '0') : (chars[0] - 'A' + 10));
			c <<= 4; // left shift by 4 bits a.k.a multiply by 16

			c += (int) (Character.isDigit(chars[1]) ? (chars[1] - '0') : (chars[1] - 'A' + 10));

			ub[i] = (byte) c;
		}

		return ub;
	}


	private String toString(byte[] bytes)
	{
		StringBuilder macAddress = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			macAddress.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			if (i < 5) {
				macAddress.append(":");
			}
		}

		return macAddress.toString();
	}

	private String toString(UnsignedByte[] unsignedBytes)
	{
		if (unsignedBytes.length < 6) {
			throw new IllegalArgumentException("Invalid MAC address");
		}

		StringBuilder macAddress = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			macAddress.append(String.format("%02x", unsignedBytes[i].toInt()));
			if (i < 5) {
				macAddress.append(":");
			}
		}

		return macAddress.toString();
	}

	private long unsignedByteToLong(byte b)
	{
		return (long) b & 0xFF;
	}

	/**
	 * gets the long value from byte array
	 *
	 * @param addr
	 */
	private long byte2Long(byte addr[])
	{
		long address = 0;
		if (addr != null) {
			if (addr.length == 6) {
				address = unsignedByteToLong(addr[5]);
				address |= (unsignedByteToLong(addr[4]) << 8);
				address |= (unsignedByteToLong(addr[3]) << 16);
				address |= (unsignedByteToLong(addr[2]) << 24);
				address |= (unsignedByteToLong(addr[1]) << 32);
				address |= (unsignedByteToLong(addr[0]) << 40);
			}
		}
		return address;
	}

	private String bytesToString(byte[] bytes, char ch)
	{
		StringBuffer sb = new StringBuffer(17);
		for (int i = 44; i >= 0; i -= 4) {
			int nibble = ((int) (byte2Long(bytes) >>> i)) & 0xf;
			char nibbleChar = (char) (nibble > 9 ? nibble + ('A' - 10) : nibble + '0');
			sb.append(nibbleChar);
			if ((i & 0x7) == 0 && i != 0) {
				sb.append(ch);
			}
		}
		return sb.toString();
	}



	/**
	 * convert to byteArray as required by functions
	 *
	 * @return
	 */
	private UnsignedByte[] toUnsignedByteArray(String macAddress)
	{
		if (macAddress == null) {
			throw new NullPointerException("No MacAddress Set");
		}
		String[] macAddr = macAddress.toUpperCase().split(":");
		UnsignedByte[] ub = new UnsignedByte[6];
		for (int i = 0; i < macAddr.length; i++) {
			// Log.i(TAG+"unsigned", macAddr[i]);
			char[] chars = macAddr[i].toCharArray();
			int c = 0;
			c = (int) (Character.isDigit(chars[0]) ? (chars[0] - '0') : (chars[0] - 'A' + 10));
			c <<= 4; // left shift by 4 bits a.k.a multiply by 16

			c += (int) (Character.isDigit(chars[1]) ? (chars[1] - '0') : (chars[1] - 'A' + 10));

			ub[i] = new UnsignedByte(c);
			// Log.i(TAG+"unsigned", ub[i] + "");
		}
		return ub;
	}


	public long toLong()
	{
		UnsignedByte[] ubs = toUnsignedByteArray(toString());
		long address = 0;
		if (ubs != null) {
			if (ubs.length == 6) {
				address = ubs[5].toInt();
				address |= ubs[4].toInt() << 8;
				address |= ubs[3].toInt() << 16;
				address |= ubs[2].toInt() << 24;
				address |= ubs[1].toInt() << 32;
				address |= ubs[0].toInt() << 40;
			}
		}
		return address;
	}

	/**
	 * gets a byteString to be transferred over the network.
	 *
	 * @return
	 */
	public String toByteString()
	{
		UnsignedByte[] ubs = toUnsignedByteArray(toString());

		byte[] bs = new byte[6];
		for (int i = 0; i < 6; i++) {
			bs[i] = (byte) ubs[i].toInt();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(bs);
		} catch (IOException e) {
		}
		return baos.toString();
	}





	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(address);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MacAddress other = (MacAddress) obj;
		if (!Arrays.equals(address, other.address))
			return false;
		return true;
	}

	/**
	 * Returns the String representation of bytes.
	 *
	 * Gets the unsignedbytes and converts to hex string format that's saved.
	 */
	@Override
	public String toString()
	{
		return toString(address);
	}
}
