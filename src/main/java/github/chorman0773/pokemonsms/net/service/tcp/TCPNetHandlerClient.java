package github.chorman0773.pokemonsms.net.service.tcp;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.connection.Connection;

public class TCPNetHandlerClient implements INetHandlerRemote {
	
	private Connection conn;
	
	private static final KeyPairGenerator generator;
	
	static {
		try {
			generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(2048);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
	
	public TCPNetHandlerClient(PacketDecoder dec, Socket sock) {
		conn = new Connection(dec,sock,generator.genKeyPair());
	}

	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.CLIENT;
	}

	@Override
	public void close() throws ProtocolError {
		try {
			conn.close();
		}catch(ProtocolError e) {
			throw e;
		}
		catch(IOException e) {
			throw new ProtocolError(e);
		}
	}

	@Override
	public NetworkSide getRemote() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}

	@Override
	public IPacket recieve() throws ProtocolError {
		// TODO Auto-generated method stub
		try {
			return conn.get();
		} catch(ProtocolError e) {
			throw e;
		} catch (IOException e) {
			throw new ProtocolError(e);
		} 
	}

	@Override
	public void send(IPacket packet) throws ProtocolError {
		try {
			conn.send(packet);
		} catch (IOException e) {
			throw new ProtocolError(e);
		}
	}
	
	public void handleProtocolError(ProtocolError perr) throws ProtocolError {
		//TODO send error message to client
		close();
	}

}
