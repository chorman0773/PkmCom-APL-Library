package github.chorman0773.pokemonsms.net.lan;

import java.io.Closeable;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.connection.MulticastConnection;
import github.chorman0773.pokemonsms.net.discovery.lan.Discovery;
import github.chorman0773.pokemonsms.net.discovery.lan.ListeningForServersPacket;
import github.chorman0773.pokemonsms.net.discovery.lan.ServiceAdvertisementPacket;
import github.chorman0773.pokemonsms.net.pipe.NetHandlerServerPipe;
import github.chorman0773.pokemonsms.net.server.INetHandlerServer;
import github.chorman0773.pokemonsms.net.service.Service;

public class NetControllerClientLan implements INetController,Discovery {
	
	private NetHandlerServerPipe serverPipe;
	
	private List<INetHandlerServer> remotes;
	
	private MulticastConnection advertiseConnection;
	
	private Deque<INetHandlerServer> acceptWaiting;
	private Object waitingLock;
	private IOException waitingException;
	private boolean open;
	
	

	private Set<String> supportedModes;
	private Map<Service,INetController> services;
	
	private void waitForConnectionsOn(INetController controller) {
		while(open) {
			try {
				INetHandlerServer server = controller.accept();
				synchronized(waitingLock) {
					acceptWaiting.add(server);
					waitingLock.notify();
				}
				remotes.add(server);
			}catch(IOException e) {
				synchronized(waitingLock) {
					waitingException = e;
					waitingLock.notify();
				}
			}
		}
	}
	
	private void waitForConnections() {
		services.values().parallelStream().forEach(this::waitForConnectionsOn);
	}
	
	private void sendServices() {
		services.keySet().parallelStream().forEach(s->{
			try {
				this.advertiseConnection.send(new ServiceAdvertisementPacket(s,0,supportedModes));
			} catch (ProtocolError e) {
				throw new RuntimeException(e);
			}
		});
	}
	
	private void runMulticastServer() {
		sendServices();
		while(open) {
			try {
				IPacket p = advertiseConnection.recieve();
				if(p instanceof ListeningForServersPacket)
					sendServices();
			} catch (ProtocolError e) {
				//IGNORE erroneous packets
			}
		}
	}
	
	public NetControllerClientLan(Set<Service> services,Set<String> modes,Consumer<INetHandlerServer> serverController) throws IOException {
		this.services = new HashMap<>();
		services.forEach(s->this.services.computeIfAbsent(s, v->v.getBaseProtocol().createController(discoveryDecoder)));
		advertiseConnection = new MulticastConnection(Discovery.discoveryDecoder,advertiseAddress,advertisePort);
		this.supportedModes = Collections.unmodifiableSet(modes);
		new Thread(this::waitForConnections) {
			public synchronized void start() {
				this.setDaemon(true);
				super.start();
			}
		}.start();
		new Thread(this::runMulticastServer) {
			public synchronized void start() {
				this.setDaemon(true);
				super.start();
			}
		}.start();
		this.remotes = Collections.synchronizedList(new ArrayList<>());
	}
	
	
	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}

	@Override
	public void close() throws IOException {
		synchronized(waitingLock) {
			open = false;
			for(INetController c:services.values())
				c.close();
			waitingLock.notifyAll();
			this.remotes.clear();
			this.serverPipe.close();
		}
	}
	
	public INetHandlerServer getServerPipe() {
		return this.serverPipe;
	}
	
	public INetHandlerRemote getClientPipe() {
		return this.serverPipe.getOtherSide();
	}

	@Override
	public Stream<? extends INetHandlerRemote> getRemotes() {
		// TODO Auto-generated method stub
		return remotes.parallelStream();
	}

	@Override
	public List<? extends INetHandlerRemote> listRemotes() {
		// TODO Auto-generated method stub
		return Collections.unmodifiableList(remotes);
	}
	
	public Set<String> getModes(){
		return supportedModes;
	}


	@Override
	public INetHandlerServer accept() throws IOException {
		try {
			synchronized(waitingLock) {
				if(!open)
					throw new IOException("Connection Closed");
				if(waitingException!=null) {
					IOException threw = waitingException;
					waitingException = null;
					throw threw;
				}
				while(acceptWaiting.isEmpty()) {
					waitingLock.wait();
					if(!open)
						throw new IOException("Connection Closed while waiting");
					if(waitingException!=null) {
						IOException threw = waitingException;
						waitingException = null;
						throw threw;
					}
				}
				return acceptWaiting.pop();
			}
		}catch(InterruptedException e) {
			throw new InterruptedIOException();
		}
	}

}
