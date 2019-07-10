package github.chorman0773.pokemonsms.net.service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.client.NetHandlerClient;

public class PkmComOverTCP implements IBaseProtocol {

	public PkmComOverTCP() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isValidAddress(String ip) {
	    try {
	        if ( ip == null || ip.isEmpty() ) {
	            return false;
	        }

	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if ( ip.endsWith(".") ) {
	            return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}

	@Override
	public boolean isValidPort(int port) {
		// TODO Auto-generated method stub
		return port<65536&&port>0;
	}

	@Override
	public String getProtocolName() {
		// TODO Auto-generated method stub
		return "PkmCom/TCP";
	}

	@Override
	public INetHandlerRemote openRemote(PacketDecoder dec,String addr, int port) throws IOException {
		Socket s = new Socket();
		s.connect(new InetSocketAddress(addr,port));
		return new NetHandlerClient(dec,s);
	}

	@Override
	public INetController createController(PacketDecoder dec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddress() {
		// TODO Auto-generated method stub
		try {
			return InetAddress.getLocalHost().toString();
		} catch (UnknownHostException e) {
			return "127.0.0.1";
		}
	}

	@Override
	public int allocatePort() {
		try {
			ServerSocket sock = new ServerSocket();
			sock.bind(new InetSocketAddress(0));
			int port = sock.getLocalPort();
			sock.close();
			return port;
		}catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

}
