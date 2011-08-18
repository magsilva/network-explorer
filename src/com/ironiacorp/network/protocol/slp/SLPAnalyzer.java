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

import com.ironiacorp.network.PacketAnalyzer;
import com.ironiacorp.network.protocol.slp.SLPMessage.Function;

/**
 * SLPAnalyzer analyzes packets that contains SLP (Service Locator
 * Protocol) data.
 */
public class SLPAnalyzer implements PacketAnalyzer
{
	/**
	 * Guess message type by reading the function field of the packet
	 * (prettry straight forward, code copied from SLPMessage.processFunction().
	 * 
	 * @param packet Packet data.
	 * 
	 * @return Function type or null if it could not detect the function using
	 * this strategy.
	 */
	private SLPMessage.Function guessMessageType(byte[] packet)
	{
		try {
			byte functionId = packet[SLPMessage.FUNCTION_OFFSET];
			for (Function f : Function.values()) {
				if (functionId == f.id) {
					return f;
				}
			}
		} catch (Exception e) {}
		
		return null;
	}
	
	@Override
	public SLPMessage parse(byte[] packet)
	{
		Function function = guessMessageType(packet);
		SLPMessage msg;
		
		try {
			if (function != null) {
				switch (function) {
					case SRVRQST:
						msg = new SLPServiceRequestMessage();
						msg.processPacket(packet);
						return msg;
					default:
						msg = new SLPMessage();
						msg.processPacket(packet);
						return msg;
				}
			} else {
				msg = new SLPMessage();
				msg.processPacket(packet);
				return msg;
			}
		} catch (Exception e) {
			return null;
		}
	}
}
