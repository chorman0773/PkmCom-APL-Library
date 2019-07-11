package github.chorman0773.pokemonsms.net.server;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.service.Service;

public class NetControllerServer implements INetController {
	
	private List<INetHandlerServer> remotes;
	
	private Deque<INetHandlerServer> acceptWaiting;
	protected final Object waitingLock = new Object();
	private IOException waitingException;
	protected volatile boolean open;
	
	

	private Set<String> supportedModes;
	protected final Map<Service,INetController> services;
	
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
	
	public NetControllerServer(PacketDecoder dec,Set<Service> services,Set<String> modes) throws IOException {
		this.services = new HashMap<>();
		services.forEach(s->this.services.computeIfAbsent(s, v->v.getBaseProtocol().createController(dec)));
		this.supportedModes = Collections.unmodifiableSet(modes);
		new Thread(this::waitForConnections) {
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
		}
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
