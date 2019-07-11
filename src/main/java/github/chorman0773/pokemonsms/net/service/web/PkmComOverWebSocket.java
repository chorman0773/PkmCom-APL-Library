package github.chorman0773.pokemonsms.net.service.web;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import github.chorman0773.pokemonsms.net.INetController;
import github.chorman0773.pokemonsms.net.INetHandlerRemote;
import github.chorman0773.pokemonsms.net.PacketDecoder;
import github.chorman0773.pokemonsms.net.ProtocolError;
import github.chorman0773.pokemonsms.net.service.IBaseProtocol;

public class PkmComOverWebSocket implements IBaseProtocol {

	public PkmComOverWebSocket() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isValidAddress(String addr) {
		return false;
	}

	@Override
	public boolean isValidPort(int port) {
		// TODO Auto-generated method stub
		return port>0&&port<65536;
	}

	@Override
	public String getProtocolName() {
		// TODO Auto-generated method stub
		return "PkmCom/WebSocket";
	}

	@Override
	public INetHandlerRemote openRemote(PacketDecoder dec, String addr, int port) throws IOException {
		try {
			if(port!=443)
				addr += ":"+port;
			URI rl = new URI("wss",addr,"");
			return new NetHandlerWSSClient(dec,rl);
		} catch (URISyntaxException e) {
			throw new ProtocolError(e);
		}
	}

	@Override
	public INetController createController(PacketDecoder dec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalAddress() {
		// TODO Auto-generated method stub
		return "127.0.0.1";
	}

	@Override
	public int allocatePort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
