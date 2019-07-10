package github.chorman0773.pokemonsms.net.server;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;

import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.connection.Connection;

public class NetHandlerServer implements INetHandlerServer {
	private Connection conn;
	private NetControllerServer controller;
	public NetHandlerServer(PacketDecoder dec,Socket sock,KeyPair keys,NetControllerServer controller) throws IOException {
		conn = new Connection(dec,sock,keys);
		this.controller = controller;
		try {
			conn.handshake();
		}catch(IOException e) {
			conn.close();
		}
	}
	private void handleProtocolError(ProtocolError perr) throws ProtocolError {
		//TODO send error message to client
		close();
	}
	
	public void send(IPacket packet) throws ProtocolError {
		try {
			conn.send(packet);
		}catch(ProtocolError perr) {
			handleProtocolError(perr);
		}catch(IOException e) {
			close();
		}
	}
	public IPacket recieve()throws ProtocolError{
		try {
			return conn.get();
		}catch(ProtocolError perr) {
			handleProtocolError(perr);
			throw perr;
		}catch(IOException e) {
			close();
			throw new ProtocolError(e);
		}
	}
	@Override
	public void close() throws ProtocolError {
		try {
			conn.close();
		}catch(IOException e) {
			throw new ProtocolError(e);
		}
	}
	@Override
	public INetController getController() {
		// TODO Auto-generated method stub
		return controller;
	}


}
