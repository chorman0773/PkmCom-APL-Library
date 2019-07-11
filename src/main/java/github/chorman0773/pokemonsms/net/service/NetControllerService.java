package github.chorman0773.pokemonsms.net.service;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.server.INetHandlerServer;

public abstract class NetControllerService implements INetController {
	
	private Set<INetHandlerServer> connections = new HashSet<>();
	protected final Object lock = new Object();
	private Thread incomingThread;
	private Deque<INetHandlerServer> handleConnections = new LinkedList<>();
	private PacketDecoder dec;
	protected final AtomicBoolean open = new AtomicBoolean();
	private IOException waitingException;
	
	private void handleIncoming() {
		while(open.get()) {
			try {
				INetHandlerServer handler = acceptConnection(dec);
				synchronized(lock) {
					connections.add(handler);
					handleConnections.add(handler);
					lock.notify();
				}
			}
			catch(IOException e) {
				synchronized(lock) {
					waitingException = e;
					lock.notify();
				}
			}
		}
	}
	
	protected abstract INetHandlerServer acceptConnection(PacketDecoder dec)throws IOException;
	
	public NetControllerService(PacketDecoder dec) {
		this.dec = dec;
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

	public void closeConnection(INetHandlerServer netHandlerServer) throws IOException {
		synchronized(lock) {
			connections.remove(netHandlerServer);
		}
		netHandlerServer.close();
	}

	@Override
	public INetHandlerServer accept() throws IOException {
		try {
			synchronized(lock) {
				if(!open.getAcquire())
					throw new IOException("Connection Closed");
				if(waitingException!=null) {
					IOException threw = waitingException;
					waitingException = null;
					throw threw;
				}
				while(handleConnections.isEmpty()) {
					lock.wait();
					if(!open.getAcquire())
						throw new IOException("Connection Closed while waiting");
					if(waitingException!=null) {
						IOException threw = waitingException;
						waitingException = null;
						throw threw;
					}
				}
				return handleConnections.pop();
			}
		}catch(InterruptedException e) {
			throw new InterruptedIOException();
		}
	}

}
