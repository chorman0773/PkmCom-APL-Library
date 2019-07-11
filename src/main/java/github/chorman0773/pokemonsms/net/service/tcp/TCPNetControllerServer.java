package github.chorman0773.pokemonsms.net.service.tcp;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.server.INetHandlerServer;
import github.chorman0773.pokemonsms.net.service.NetControllerService;

public class TCPNetControllerServer extends NetControllerService implements INetController {
	
	
	private ServerSocket sock;
	private KeyPair serverKeyPair;
	
	
	
	
	
	public TCPNetControllerServer(PacketDecoder dec,SocketAddress addr,KeyPair pair) throws IOException {
		super(dec);
		sock = new ServerSocket();
		sock.bind(addr);
		sock.setSoTimeout(1000);
		open.set(true);
		this.serverKeyPair = pair;
	}


	@Override
	protected INetHandlerServer acceptConnection(PacketDecoder dec) throws IOException {
		Socket s = sock.accept();
		TCPNetHandlerServer handler = new TCPNetHandlerServer(dec,s,serverKeyPair,this);
		return handler;
	}
	
	public void close() throws IOException {
		super.close();
		sock.close();
	}

}
