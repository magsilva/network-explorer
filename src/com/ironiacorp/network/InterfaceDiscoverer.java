package com.ironiacorp.network;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class InterfaceDiscoverer
{
	private boolean ipv4 = true;

	private boolean ipv6 = false;

	private boolean localhost = false;

	public boolean isIpv4()
	{
		return ipv4;
	}

	public void setIpv4(boolean ipv4)
	{
		this.ipv4 = ipv4;
	}

	public boolean isIpv6()
	{
		return ipv6;
	}

	public void setIpv6(boolean ipv6)
	{
		this.ipv6 = ipv6;
	}

	public boolean isLocalhost()
	{
		return localhost;
	}

	public void setLocalhost(boolean localhost)
	{
		this.localhost = localhost;
	}

	public Set<InetAddress> getAddresses(NetworkInterface ni)
	{
		Enumeration<InetAddress> addresses = ni.getInetAddresses();
		Set<InetAddress> validAddresses = new TreeSet<InetAddress>();

		while (addresses.hasMoreElements()) {
			InetAddress address = addresses.nextElement();
			if (address.isLoopbackAddress() && !localhost) {
				continue;
			}
			if (address instanceof Inet6Address && !ipv6) {
				continue;
			}

			if (address instanceof Inet4Address && !ipv4) {
				continue;
			}

			validAddresses.add(address);
		}

		return validAddresses;
	}

	public Map<NetworkInterface, Set<InetAddress>> discover()
	{
		Map<NetworkInterface, Set<InetAddress>> result = new TreeMap<NetworkInterface, Set<InetAddress>>();
		Enumeration<NetworkInterface> niEnu = null;
		
		try {
			niEnu = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException se) {
			throw new IllegalArgumentException("Cannot list interfaces", se);
		}
		
		while (niEnu.hasMoreElements()) {
			NetworkInterface ni = niEnu.nextElement();
			Enumeration<NetworkInterface> vniEnu = ni.getSubInterfaces(); 
			Set<InetAddress> addresses = getAddresses(ni);
			if (addresses.size() > 0) {
				result.put(ni, addresses);
			}
			while (vniEnu.hasMoreElements()) {
				NetworkInterface vni = vniEnu.nextElement();
				addresses = getAddresses(vni);
				if (addresses.size() > 0) {
					result.put(vni, addresses);
				}
			}

		}
		
		return result;
	}
}
