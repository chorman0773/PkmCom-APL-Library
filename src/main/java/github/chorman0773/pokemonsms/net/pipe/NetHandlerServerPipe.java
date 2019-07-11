package github.chorman0773.pokemonsms.net.pipe;

import java.io.IOException;
import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.server.INetHandlerServer;
import github.chorman0773.pokemonsms.net.service.tcp.TCPNetControllerServer;

/**
* The Server end of an {@link INetHandlerPipe}.<br/>
* This class is also an {@link INetHandlerServer}.
*/
public class NetHandlerServerPipe extends NetHandlerPipe implements INetHandlerServer{
	private INetController controller;
	private NetHandlerClientPipe client;
	
	public NetHandlerServerPipe(INetController controller) {
		this.controller = controller;
		client = new NetHandlerClientPipe(this);
	}


	@Override
	public INetHandlerPipe getOtherSide() {
		// TODO Auto-generated method stub
		return client;
	}


	@Override
	public INetController getController() {
		// TODO Auto-generated method stub
		return controller;
	}
	
	@Override
	public void handleProtocolError(ProtocolError e) throws ProtocolError {
		
	}

}
