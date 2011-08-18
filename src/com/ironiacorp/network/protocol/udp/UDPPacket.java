package com.ironiacorp.network.protocol.udp;

import com.ironiacorp.network.protocol.ip.IPPacket;


public class UDPPacket extends IPPacket{
    
    public UDPPacket(IPPacket packet){
	this.src_ip = packet.src_ip;
	this.dst_ip = packet.dst_ip;
    }

    public int src_port;
    public int dst_port;
    
    public byte[] data;
}
