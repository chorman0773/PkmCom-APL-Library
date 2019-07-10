package github.chorman0773.pokemonsms.net.service;

import java.io.IOException;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.PacketDecoder;

public interface IBaseProtocol {
	public boolean isValidAddress(String addr);
	public boolean isValidPort(int port);
	public String getProtocolName();
	
	public INetHandlerRemote openRemote(PacketDecoder dec,String addr,int port)throws IOException;
	
	public INetController createController(PacketDecoder dec);
	
	public String getLocalAddress();
	public int allocatePort();
}
