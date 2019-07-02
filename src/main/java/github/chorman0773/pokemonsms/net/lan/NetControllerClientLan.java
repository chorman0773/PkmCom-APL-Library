package github.chorman0773.pokemonsms.net.lan;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.stream.Stream;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.connection.MulticastConnection;
import github.chorman0773.pokemonsms.net.pipe.NetHandlerServerPipe;

public class NetControllerClientLan implements INetController {
	
	private NetHandlerServerPipe serverPipe;
	
	private List<? extends INetHandlerRemote> remotes;
	
	private MulticastConnection advertiseConnection;
	
	private static final InetAddress advertiseAddress = null;
	
	public NetControllerClientLan() {
		
	}
	
	
	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Stream<? extends INetHandlerRemote> getRemotes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<? extends INetHandlerRemote> listRemotes() {
		// TODO Auto-generated method stub
		return null;
	}

}
