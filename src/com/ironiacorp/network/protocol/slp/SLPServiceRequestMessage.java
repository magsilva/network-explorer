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

package com.ironiacorp.network.protocol.slp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ironiacorp.io.StreamUtil;

public class SLPServiceRequestMessage extends SLPMessage
{
	private static Logger log = LoggerFactory.getLogger(SLPServiceRequestMessage.class);
	
	private static final int PREVIOUS_RESPONDER_LIST_LENGTH_SIZE = 2;
	private int previousResponderListSize;
	
	private static final int SERVICE_TYPE_LENGTH_SIZE = 2;
	private int serviceTypeSize;
	
	private static final int SCOPE_LIST_LENGTH_SIZE = 2;
	private int scopeListSize;
	
	private static final int PREDICATE_LENGTH_SIZE = 2;
	private int predicateSize;

	private List<InetAddress> addresses;
	
	private List<SLPServiceType> serviceTypes;
	
	private List<String> scopes;
	
	public List<InetAddress> getAddresses()
	{
		return addresses;
	}
	
	public List<SLPServiceType> getServiceTypes()
	{
		return serviceTypes;
	}
	
	/**
	 * Get the bytes of the message body in the following RFC 2608 compliant
	 * format:
	 * 
	 * <pre>
	 * 	 0                   1                   2                   3
	 * 	 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	|	Service Location header (function = SrvRqst = 1)	    	|
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	|	length of <PRList>	|	<PRList> String	                    \
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	|	length of <service-type>	|	<service-type> String    	\
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	|	length of <scope-list>	|	<scope-list> String          	\
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	| length of predicate string	| Service Request <predicate>   \
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * 	| length of <SLP SPI> string	|	<SLP SPI> String	        \
	 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	 * </pre>
	 */
	@Override
	public void parse(byte[] packet)
	{
		super.parse(packet);
		processPreviousResponderList(packet);
		processServiceType(packet);
		processScopeList(packet);
	}

	private void processServiceType(byte[] packet)
	{	
		serviceTypeSize = StreamUtil.readInt(packet, HEADER_SIZE + PREVIOUS_RESPONDER_LIST_LENGTH_SIZE + previousResponderListSize, SERVICE_TYPE_LENGTH_SIZE);
		String[] rawServiceTypes = StreamUtil.readString(packet, HEADER_SIZE + PREVIOUS_RESPONDER_LIST_LENGTH_SIZE + previousResponderListSize + SERVICE_TYPE_LENGTH_SIZE, serviceTypeSize).split(",");
		serviceTypes = new ArrayList<SLPServiceType>();
		
		for (String rawServiceType : rawServiceTypes) {
			SLPServiceType service = new SLPServiceType(rawServiceType);
			serviceTypes.add(service);
		}
	}

	private void processScopeList(byte[] packet)
	{
		int offset = HEADER_SIZE + PREVIOUS_RESPONDER_LIST_LENGTH_SIZE + previousResponderListSize + SERVICE_TYPE_LENGTH_SIZE +	serviceTypeSize;
		scopeListSize = StreamUtil.readInt(packet, offset, SCOPE_LIST_LENGTH_SIZE);
		String[] rawScopes = StreamUtil.readString(packet, offset + SCOPE_LIST_LENGTH_SIZE, scopeListSize).split(",");
		scopes = new ArrayList<String>();
		
		for (String rawscope : rawScopes) {
			scopes.add(rawscope);
		}
	}

	private void processPreviousResponderList(byte[] packet)
	{
		previousResponderListSize = StreamUtil.readInt(packet, HEADER_SIZE, PREVIOUS_RESPONDER_LIST_LENGTH_SIZE);
		String previousResponderList = StreamUtil.readString(packet, HEADER_SIZE + PREVIOUS_RESPONDER_LIST_LENGTH_SIZE, previousResponderListSize);
		StringBuilder address = new StringBuilder();
		StringBuilder octet = new StringBuilder();
		int octets = 0;

		log.info("Parsing address of services: " + previousResponderListSize);
		if (! previousResponderList.startsWith("127") && previousResponderList.contains("127")) {
			previousResponderList = previousResponderList.replace("127.", ",127.");
		}
		
		if (previousResponderList.startsWith("127.0.0.1")) {
			try {
				addresses.add(Inet4Address.getByName("127.0.0.1"));
			} catch (Exception e) {}
			previousResponderList = previousResponderList.replaceFirst("127.0.0.1", "");
		}
		
		addresses = new ArrayList<InetAddress>();
		for (int i = 0; i < previousResponderList.length(); i++) {
			char c = previousResponderList.charAt(i);
			
			// Add new octet
			if (c == '.') {
				address.append(octet);
				address.append('.');
				octet.setLength(0);
				octets++;
			}
		
			// Add new address
			if (c == ',') {
				address.append(octet);
				octets++;
				if (octets == 4) {
					try {
						addresses.add(Inet4Address.getByName(address.toString()));
					} catch (Exception e) {
						// throw new IllegalArgumentException(e);
					}
				} else {
					// TODO: Cannot parse malformed list.
				}
				address.setLength(0);
				octet.setLength(0);
				octets = 0;
			}
			
			// Add new char or new address 
			if (c != '.' && c != ',') {
				if (octets == 3 && octet.length() == 3) {
					try {
						address.append(octet);
						addresses.add(Inet4Address.getByName(address.toString()));
					} catch (Exception e) {
						throw new IllegalArgumentException(e);
					}
					address.setLength(0);
					octet.setLength(0);
					octets = 0;
				}
				octet.append(c);
			}
		}
		
		// Handle the last bits 
		address.append(octet);
		if (address.length() > 0) {
			try {
				addresses.add(Inet4Address.getByName(address.toString()));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		if (log.isInfoEnabled()) {
			StringBuilder data = new StringBuilder();
			for (InetAddress debugAddress : addresses) {
				data.append(debugAddress.getHostAddress());
				data.append(" ");
			}
			log.info("Addresses of services: " + data.toString());
		}
	}
}
