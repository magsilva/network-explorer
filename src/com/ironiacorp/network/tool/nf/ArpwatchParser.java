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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ironiacorp.datastructure.Tuple;
import com.ironiacorp.network.protocol.ethernet.MacAddress;

public class ArpwatchParser
{
	public static final String DEFAULT_DATABASE = "/var/lib/arpwatch/arp.dat";

	private File database;

	public ArpwatchParser()
	{
		this(new File(DEFAULT_DATABASE));
	}

	public ArpwatchParser(File database)
	{
		if (database == null || ! database.isFile() || ! database.canRead()) {
			throw new IllegalArgumentException("Cannot open database");
		}
		this.database = database;
	}

	public List<Tuple<MacAddress, InetAddress>> parse()
	{
		List<Tuple<MacAddress, InetAddress>> result = new ArrayList<Tuple<MacAddress,InetAddress>>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(database));
			String line = reader.readLine();
			Pattern separator = Pattern.compile("\\s");
			while ((line = reader.readLine()) != null) {
				String[] tokens = separator.split(line);
				MacAddress macAddress = new MacAddress(tokens[0]);
				InetAddress inetAddress = InetAddress.getByName(tokens[1]);
				Tuple<MacAddress, InetAddress> tuple = new Tuple<MacAddress, InetAddress>(macAddress, inetAddress);
				result.add(tuple);
			}
		} catch (IOException e) {
		}

		return result;
	}

}
