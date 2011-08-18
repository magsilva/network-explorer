package com.ironiacorp.network.protocol.slp;

import java.nio.charset.Charset;

import com.ironiacorp.io.StreamUtil;

/**
 * A Service URL encodes the address to a network service. It has the form:
 * "service:" [service type] "://" [address]
 */
public class SLPServiceURL
{	
	public enum Lifetime
	{
		NONE(0),
		DEFAULT(10800),
		MAXIMUM(65535),
		PERMANENT(1);

		public final int value;
		
		private Lifetime(int value)
		{
			this.value = value;
		}
	}
	
	private static final int URL_ENTRY_RESERVED_RELATIVE_OFFSET = 0;
	private static final int URL_ENTRY_RESERVED_SIZE = 1;

	private static final int URL_ENTRY_LIFETIME_RELATIVE_OFFSET = 1;
	private static final int URL_ENTRY_LIFETIME_SIZE = 2;

	private static final int URL_ENTRY_URL_LENGTH_RELATIVE_OFFSET = 3;
	private static final int URL_ENTRY_URL_LENGTH_SIZE = 2;

	private static final int URL_ENTRY_URL_RELATIVE_OFFSET = 5;
	
	public static final String PREFIX = "service:";
	
	public static final String ADDRESS_PREFIX = "://";
	
	public static final String PORT_PREFIX = ":";
	
	private SLPServiceType serviceType;
	
	private String host;
	
	private int port = -1;
	
	private int lifetime = 0;
	
	public int getLength()
	{
		int length = 5 + PREFIX.getBytes().length + serviceType.getLength();
		if (host != null) {
			length += ADDRESS_PREFIX.getBytes().length + host.getBytes().length;
			if (port != -1) {
				length += PORT_PREFIX.getBytes().length + Integer.toString(port).getBytes().length;
			}
		}
		
		return length;
	}

	public SLPServiceURL(byte[] packet, int offset)
	{
		processServiceUrl(packet, offset);
	}
	
	public SLPServiceURL(String url, int lifetime)
	{
		this.lifetime = lifetime;
		processUrl(url);
	}
	
	public SLPServiceType getServiceType()
	{
		return serviceType;
	}

	public void setServiceType(SLPServiceType serviceType)
	{
		this.serviceType = serviceType;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
	}

	public int getLifetime()
	{
		return lifetime;
	}

	public void setLifetime(int lifetime)
	{
		this.lifetime = lifetime;
	}
	
	/**
	 *
	 * 	<pre>
	 * 	 0                   1                   2                   3
	 * 	 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	|	Reserved	|         Lifetime              |    URL Length |
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	|URL len, contd.|	                URL (variable length)	    \
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	|# of URL auths | 	Auth. blocks (if any)                       \
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * <pre>
	 */
	private void processServiceUrl(byte[] packet, int offset)
	{
		processLifetime(packet, offset);
		processUrl(packet, offset);
	}
	

	private void processLifetime(byte[] packet, int offset)
	{
		setLifetime(StreamUtil.readInt(packet, offset + URL_ENTRY_LIFETIME_RELATIVE_OFFSET, URL_ENTRY_LIFETIME_SIZE));
	}
	
	private void processUrl(byte[] packet, int offset)
	{
		String url;
		int length = StreamUtil.readInt(packet, offset + URL_ENTRY_URL_LENGTH_RELATIVE_OFFSET, URL_ENTRY_URL_LENGTH_SIZE);
		byte[] rawurl = new byte[length];
		for (int i = 0, j = offset + URL_ENTRY_URL_RELATIVE_OFFSET; i < length; i++, j++) {
			rawurl[i] = packet[j];
		}
		url = new String(rawurl, Charset.forName("UTF-8"));		
		processUrl(url);
	}
	
	private void processUrl(String url)
	{
		if (! url.startsWith(PREFIX)) {
			throw new IllegalArgumentException("Invalid service type: "	+ serviceType);
		}

		int pathStart = url.indexOf(ADDRESS_PREFIX);
		if (pathStart == -1) {
			SLPServiceType serviceType = new SLPServiceType(url.substring(PREFIX.length()));
			setServiceType(serviceType);
		} else {
			SLPServiceType serviceType = new SLPServiceType(url.substring(PREFIX.length(), pathStart));
			setServiceType(serviceType);
			processAddress(url.substring(pathStart + ADDRESS_PREFIX.length()));
		}
	}
	
	/**
	 * <addrspec> is a hostname (which should be used if possible) or dotted
	 * decimal notation for a hostname, followed by an optional ‘:’ and
	 * port number.
	 */
	private void processAddress(String url)
	{
		int portStart = url.lastIndexOf(PORT_PREFIX);
		
		if (portStart == -1) {
			setHost(url);
		} else {
			try {
				setHost(url.substring(0, portStart));
				setPort(Integer.valueOf(url.substring(portStart + 1)));
			} catch (NumberFormatException nfe) {
				setHost(url);
			}
		}
	}
	
}
