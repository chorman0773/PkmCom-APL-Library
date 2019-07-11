package github.chorman0773.pokemonsms.net.service.tcp;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyPair;

import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.connection.Connection;
import github.chorman0773.pokemonsms.net.server.INetHandlerServer;

public class TCPNetHandlerServer implements INetHandlerServer {
	private Connection conn;
	private TCPNetControllerServer controller;
	public TCPNetHandlerServer(PacketDecoder dec,Socket sock,KeyPair keys,TCPNetControllerServer controller) throws IOException {
		conn = new Connection(dec,sock,keys);
		this.controller = controller;
		try {
			conn.handshake();
		}catch(IOException e) {
			close();
		}
	}
	public void handleProtocolError(ProtocolError perr) throws ProtocolError {
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
