package github.chorman0773.pokemonsms.net.lan;

import java.io.IOException;
import java.util.Set;
import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.connection.MulticastConnection;
import github.chorman0773.pokemonsms.net.discovery.lan.Discovery;
import github.chorman0773.pokemonsms.net.discovery.lan.ListeningForServersPacket;
import github.chorman0773.pokemonsms.net.discovery.lan.ServiceAdvertisementPacket;
import github.chorman0773.pokemonsms.net.pipe.NetHandlerServerPipe;
import github.chorman0773.pokemonsms.net.server.INetHandlerServer;
import github.chorman0773.pokemonsms.net.server.NetControllerServer;
import github.chorman0773.pokemonsms.net.service.Service;

public class NetControllerClientLan extends NetControllerServer implements INetController,Discovery {
	
	private MulticastConnection advertiseConnection;
	
	private NetHandlerServerPipe serverPipe;

	
	private void sendServices() {
		services.keySet().parallelStream().forEach(s->{
			try {
				this.advertiseConnection.send(new ServiceAdvertisementPacket(s,0,this.getModes()));
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
	
	public NetControllerClientLan(PacketDecoder dec,Set<Service> services,Set<String> modes) throws IOException {
		super(dec,services,modes);
		advertiseConnection = new MulticastConnection(Discovery.discoveryDecoder,advertiseAddress,advertisePort);
		this.serverPipe = new NetHandlerServerPipe(this);
		new Thread(this::runMulticastServer) {
			public synchronized void start() {
				this.setDaemon(true);
				super.start();
			}
		}.start();
	}
	
	public void close() throws IOException {
		super.close();
		this.serverPipe.close();
	}
	
	
	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}
	
	public INetHandlerServer getServerPipe() {
		return this.serverPipe;
	}
	
	public INetHandlerRemote getClientPipe() {
		return this.serverPipe.getOtherSide();
	}

}
