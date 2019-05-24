package github.chorman0773.pokemonsms.net.multicast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;

import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.connection.MulticastConnection;

public class NetHandlerMulticast implements INetHandlerRemote {
	private MulticastConnection conn;
	public NetHandlerMulticast(InetAddress group,int port) throws IOException {
		conn = new MulticastConnection(group,port);
	}

	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.CLIENT;
	}

	@Override
	public void close() throws IOException {
		conn.close();
	}


	@Override
	public IPacket recieve() throws ProtocolError {
		// TODO Auto-generated method stub
		return conn.recieve();
	}

	@Override
	public void send(IPacket packet) throws ProtocolError {
		conn.send(packet);
	}

	@Override
	public NetworkSide getRemote() {
		// TODO Auto-generated method stub
		return NetworkSide.CLIENT;
	}

}
