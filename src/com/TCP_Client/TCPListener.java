package com.TCP_Client;

public interface TCPListener {
	//public void onTCPMessageRecieved(String message);
	public void onTCPMessageRecieved(byte[] bs);
	public void onTcpConnectionStatusChanged(boolean isConnectedNow);
}
