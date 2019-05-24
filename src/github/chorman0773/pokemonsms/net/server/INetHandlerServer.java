package github.chorman0773.pokemonsms.net.server;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.NetworkSide;

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
