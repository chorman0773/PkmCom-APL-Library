package github.chorman0773.pokemonsms.net.lan;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.connection.MulticastConnection;
import github.chorman0773.pokemonsms.net.discovery.lan.Discovery;
import github.chorman0773.pokemonsms.net.discovery.lan.ListeningForServersPacket;
import github.chorman0773.pokemonsms.net.discovery.lan.NoLongerAvailablePacket;
import github.chorman0773.pokemonsms.net.discovery.lan.ServiceAdvertisementPacket;
import github.chorman0773.pokemonsms.net.service.Service;

public class LanClientListener implements Discovery, AutoCloseable {
	
	public final class LocalServer{
		private Service service;
		private Set<String> modes;
		private boolean handshakePassword;
		private LocalServer(Service service,boolean password,Set<String> modes) {
			this.service = service;
			this.modes = modes;
			this.handshakePassword = password;
			for(Consumer<Service> target:accepters)
				target.accept(service);
		}
		
		public Set<String> getModes(){
			return Collections.unmodifiableSet(modes);
		}
		
		public INetHandlerRemote openConnection() throws IOException {
			//TODO implement alternative handshaking.
			if(handshakePassword)
				throw new ProtocolError("This implementation does not support password based alternative handshaking");
			return service.openConnection();
		}
		
		public Service getService() {
			return this.service;
		}
		
		public boolean hasPassword() {
			return handshakePassword;
		}
	}
	
	private List<Consumer<Service>> accepters = new ArrayList<>();
	
	private MulticastConnection conn;
	private volatile boolean open;
	private Object doneTarget;
	
	public LanClientListener() throws IOException {
		servers = Collections.synchronizedMap(new HashMap<>());
		conn = new MulticastConnection(discoveryDecoder,advertiseAddress,advertisePort);
	}
	
	private void listenForServices() {
		try {
			conn.send(new ListeningForServersPacket());
			while(open) {
				IPacket p = conn.recieve();
				if(p instanceof NoLongerAvailablePacket) {
					Service s = ((NoLongerAvailablePacket)p).getService();
					servers.remove(s);
				}else if(p instanceof ServiceAdvertisementPacket) {
					ServiceAdvertisementPacket srvAdv = (ServiceAdvertisementPacket)p;
					Service srv = srvAdv.getService();
					accepters.forEach(c->c.accept(srv));
					servers.put(srv,new LocalServer(srv,srvAdv.hasFlags(0x01),srvAdv.getModes()));
				}
			}
		}catch(IOException e) {
			throw new RuntimeException(e);
		}finally {
			synchronized(doneTarget) {
				doneTarget.notify();
			}
		}
	}
	
	private Map<Service,LocalServer> servers;
	
	public Stream<LocalServer> localServers(){
		return servers.values().stream();
	}

	@Override
	public void close() throws IOException, InterruptedException {
		servers.clear();
		synchronized(doneTarget) {
			this.open = false;
			doneTarget.wait();
			conn.close();
		}
	}
	

}
