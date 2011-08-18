package com.ironiacorp.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Multimap;
import com.ironiacorp.network.protocol.ethernet.MacAddress;

public class NetworkFreud
{
	public void getMac()
	{
		try {

			InetAddress address = InetAddress.getLocalHost();
			System.out.println("Address " + address);
			/*
			 * Get NetworkInterface for the current host and then read the hardware address.
			 */
			NetworkInterface ni = NetworkInterface.getByInetAddress(address);
			System.out.println("NI = " + ni);
			byte[] mac = ni.getHardwareAddress();

			/*
			 * Extract each array of mac address and convert it to hexa with the following format
			 * 08-00-27-DC-4A-9E.
			 */

			System.out.println("MAC Address is ");
			for (int i = 0; i < mac.length; i++) {
				System.out.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "");
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		ArpDatabase database = new ArpDatabase();
		ArpwatchParser parser = new ArpwatchParser();
		List<Tuple<MacAddress, InetAddress>> list = parser.parse();
		Iterator<Tuple<MacAddress, InetAddress>> i = list.iterator();
		while (i.hasNext()) {
			Tuple<MacAddress, InetAddress> tuple = i.next();
			database.add(tuple.getT(), tuple.getU());
		}
		Multimap<InetAddress, MacAddress> bogusIP = database.findBogusIP();
		Iterator<InetAddress> ips = bogusIP.keySet().iterator();
		while (ips.hasNext()) {
			InetAddress ip = ips.next();
			System.out.println(ip.toString());
			Collection<MacAddress> macs = bogusIP.get(ip);
			Iterator<MacAddress> j = macs.iterator();
			while (j.hasNext()) {
				MacAddress mac = j.next();
				System.out.println("\t" + mac.toString());
			}
		}

	}
}
