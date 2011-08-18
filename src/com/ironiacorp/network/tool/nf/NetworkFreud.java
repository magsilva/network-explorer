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


package com.ironiacorp.network.tool.nf;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Multimap;
import com.ironiacorp.datastructure.Tuple;
import com.ironiacorp.network.protocol.ethernet.MacAddress;

public class NetworkFreud
{
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
