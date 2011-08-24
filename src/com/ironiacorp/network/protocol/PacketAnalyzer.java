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

package com.ironiacorp.network.protocol;


public interface PacketAnalyzer<T extends Packet>
{
	/**
	 * Analyze a byte array and create a Packet instance.
	 * 
	 * @param packet Payload of the packet.
	 * 
	 * @return A packet instance or NULL if it could not be parsed.
	 */
	T parse(byte[] packet);
	
	T parse(RawPacket packet);
}
