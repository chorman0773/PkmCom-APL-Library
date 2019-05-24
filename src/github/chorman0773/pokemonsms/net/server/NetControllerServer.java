package github.chorman0773.pokemonsms.net.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.ProtocolError;

public class NetControllerServer implements INetController {
	
	private Set<INetHandlerServer> connections = new HashSet<>();
	private ServerSocket sock;
	private AtomicBoolean open = new AtomicBoolean();
	private Object lock = new Object();
	private KeyPair serverKeyPair;
	private Thread incomingThread;
	
	private void handleIncoming() {
		while(open.get()) {
			try {
				Socket s = sock.accept();
				NetHandlerServer handler = new NetHandlerServer(s,serverKeyPair,this);
				synchronized(lock) {
					connections.add(handler);
				}
			}catch(SocketTimeoutException e) {
				
			}
			catch(IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public NetControllerServer(SocketAddress addr,KeyPair pair) throws IOException {
		sock = new ServerSocket();
		sock.bind(addr);
		sock.setSoTimeout(1000);
		open.set(true);
		this.serverKeyPair = pair;
		incomingThread = new Thread(this::handleIncoming);
		incomingThread.start();
	}

	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}

	@Override
	public void close() throws IOException {
		if(!open.get())
			return;
		open.set(false);
		try {
			incomingThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(INetHandlerServer conn:connections)
			conn.close();

		sock.close();
	}

	@Override
	public Stream<? extends INetHandlerRemote> getRemotes() {
		// TODO Auto-generated method stub
		return connections.stream();
	}

	@Override
	public List<? extends INetHandlerRemote> listRemotes() {
		// TODO Auto-generated method stub
		return new ArrayList<>(connections);
	}

	@Override
	public ServerSocket getServer() {
		// TODO Auto-generated method stub
		return sock;
	}

	public void closeConnection(NetHandlerServer netHandlerServer) throws ProtocolError {
		synchronized(lock) {
			connections.remove(netHandlerServer);
		}
		netHandlerServer.close();
	}
	
	public void addRemote(INetHandlerServer server) {
		synchronized(lock) {
			connections.add(server);
		}
	}

}
