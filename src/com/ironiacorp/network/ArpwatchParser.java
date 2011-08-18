package com.ironiacorp.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
				Tuple<MacAddress, InetAddress> tuple = new Tuple<MacAddress, InetAddress>(
								new MacAddress(tokens[0]),
								InetAddress.getByName(tokens[1]));
				result.add(tuple);
			}
		} catch (IOException e) {
		}

		return result;
	}

}
