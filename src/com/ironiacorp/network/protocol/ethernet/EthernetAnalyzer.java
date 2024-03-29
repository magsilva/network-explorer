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

package com.ironiacorp.network.protocol.ethernet;

import com.ironiacorp.network.protocol.PacketAnalyzer;
import com.ironiacorp.network.protocol.RawPacket;

public class EthernetAnalyzer implements PacketAnalyzer<EthernetPacket>
{
	@Override
	public EthernetPacket parse(byte[] data)
	{
		EthernetPacket packet = new EthernetPacket();
		return packet;
	}
	
	@Override
	public EthernetPacket parse(RawPacket rawPacket)
	{
		EthernetPacket packet = parse(rawPacket.getPayload());
		if (packet != null) {
			rawPacket.push(packet);
		}
		
		return packet;
	}
}
