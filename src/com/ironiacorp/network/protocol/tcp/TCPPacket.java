package com.ironiacorp.network.protocol.tcp;

import com.ironiacorp.network.protocol.ip.IPPacket;


public class TCPPacket extends IPPacket{

    public TCPPacket(IPPacket packet){
	this.src_ip = packet.src_ip;
	this.dst_ip = packet.dst_ip;
    }
    
    public int src_port;
    public int dst_port;
    
    public byte[] data;
}
