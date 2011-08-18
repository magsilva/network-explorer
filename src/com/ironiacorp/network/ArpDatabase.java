package com.ironiacorp.network;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.ironiacorp.network.protocol.ethernet.MacAddress;

public class ArpDatabase
{
	private Multimap<InetAddress, MacAddress> ypIM;

	private Multimap<MacAddress, InetAddress> ypMI;


	public ArpDatabase()
	{
		ypIM = HashMultimap.create();
		ypMI = HashMultimap.create();
	}

	public void add(InetAddress ip, MacAddress mac)
	{
		ypIM.put(ip, mac);
		ypMI.put(mac, ip);
	}

	public void add(MacAddress mac, InetAddress ip)
	{
		ypIM.put(ip, mac);
		ypMI.put(mac, ip);
	}

	public Multimap<InetAddress, MacAddress> findBogusIP()
	{
		Multimap<InetAddress, MacAddress> result = HashMultimap.create();
		Iterator<InetAddress> i = ypIM.keySet().iterator();
		while (i.hasNext()) {
			InetAddress ip = i.next();
			Collection<MacAddress> macs = ypIM.get(ip);
			if (macs.size() > 1) {
				Iterator<MacAddress> j = macs.iterator();
				while (j.hasNext()) {
					MacAddress mac = j.next();
					result.put(ip, mac);
				}
			}
		}

		return result;
	}
}
