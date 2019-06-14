package github.chorman0773.pokemonsms.net.server;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.NetworkSide;

/**
 * An INetHandlerServer is a Server-side INetHandlerRemote.
 * This is a tagging interface which should be used, in place of specific NetHandlerServers to indicate that a connection to a client is desired.
 * It also provides a way to gain access to the INetController which manages this Connection.
 * @author chorm
 */
public interface INetHandlerServer extends INetHandlerRemote {

	@Override
	default NetworkSide getActive() {
		// TODO Auto-generated method stub
		return NetworkSide.SERVER;
	}

	@Override
	default NetworkSide getRemote() {
		// TODO Auto-generated method stub
		return NetworkSide.CLIENT;
	}
	
	INetController getController();

}
