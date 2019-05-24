package github.chorman0773.pokemonsms.net.pipe;

import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.IPacket;
import github.chorman0773.pokemonsms.net.NetworkSide;
import github.chorman0773.pokemonsms.net.ProtocolError;

/**
* The Client End of a {@link INetHandlerPipe}
*/
public class NetHandlerClientPipe extends NetHandlerPipe implements INetHandlerRemote {
	private NetHandlerServerPipe server;
	public NetHandlerClientPipe(NetHandlerServerPipe server) {
		this.server = server;
	}

	@Override
	public NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.CLIENT;
	}

	@Override
	public INetHandlerPipe getOtherSide() {
		// TODO Auto-generated method stub
		return server;
	}

	@Override
	public NetworkSide getRemote() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}


}
